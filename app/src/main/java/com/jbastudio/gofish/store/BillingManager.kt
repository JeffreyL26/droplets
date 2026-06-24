package com.jbastudio.gofish.store

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.*
import com.jbastudio.gofish.ui.components.AvatarKind

/**
 * Wrapper um die Google Play Billing Library 7.x.
 *
 * Produkt-IDs (müssen exakt so im Play Console → Monetarisierung → Produkte angelegt sein):
 *   AD_FREE_SKU          = "gofish_ad_free"
 *   Skin-SKUs            = "gofish_skin_<KIND_NAME_LOWERCASE>"
 *                          z. B. "gofish_skin_whale", "gofish_skin_dolphin" …
 *
 * Verwendung in MainActivity:
 *   private val billing = BillingManager(this)
 *   override fun onDestroy() { billing.destroy() }
 *
 * Kauffluss:
 *   billing.launchPurchaseFlow(activity, sku) { success, sku -> … }
 *
 * Hinweis: Billing-Callbacks der Library laufen auf einem Hintergrund-Thread.
 * Das Ergebnis-[callback] wird daher auf den Main-Thread gepostet, damit der
 * aufrufende UI-Code (Compose-State) sicher reagieren kann.
 */
class BillingManager(context: Context) {

    companion object {
        const val AD_FREE_SKU = "gofish_ad_free"
        private const val SKIN_PREFIX = "gofish_skin_"

        fun skinSku(kind: AvatarKind): String = "$SKIN_PREFIX${kind.name.lowercase()}"

        /** Alle käuflichen Skin-SKUs (nur gesperrte Kinds). */
        val ALL_SKIN_SKUS: List<String> = AvatarKind.values()
            .filter { it.defaultTier == AvatarSkinTier.LOCKED }
            .map { skinSku(it) }

        val ALL_SKUS: List<String> = ALL_SKIN_SKUS + AD_FREE_SKU
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(::onPurchasesUpdated)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    private var pendingCallback: ((success: Boolean, sku: String?) -> Unit)? = null

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryAndRestorePurchases()
                }
            }
            override fun onBillingServiceDisconnected() {
                // Play stellt Verbindung automatisch wieder her; kein Retry nötig.
            }
        })
    }

    /**
     * Startet den Kauf-Dialog für das übergebene SKU.
     * [callback] wird auf dem Main-Thread mit success=true aufgerufen, wenn der
     * Kauf abgeschlossen wurde.
     */
    fun launchPurchaseFlow(
        activity: Activity,
        sku: String,
        callback: (success: Boolean, sku: String?) -> Unit
    ) {
        pendingCallback = callback
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(sku)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK
                || productDetailsList.isEmpty()) {
                finishPending(false, null)
                return@queryProductDetailsAsync
            }
            val productDetails = productDetailsList.first()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                )
                .build()
            billingClient.launchBillingFlow(activity, flowParams)
        }
    }

    private fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) handlePurchase(purchase)
            val sku = purchases.firstOrNull()?.products?.firstOrNull()
            finishPending(true, sku)
        } else {
            // Abbruch / Fehler — der Kauffluss endet ergebnislos.
            finishPending(false, null)
        }
    }

    /** Bestätigt einen Kauf und schaltet ihn im [StoreManager] frei (idempotent). */
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        // Bestätigen (Pflicht — sonst refundiert Play nach 3 Tagen)
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { _ -> }
        }

        // StoreManager aktualisieren (Snapshot-State ist threadsafe schreibbar)
        for (sku in purchase.products) {
            when {
                sku == AD_FREE_SKU -> StoreManager.setAdFree(true)
                sku.startsWith(SKIN_PREFIX) -> {
                    val kindName = sku.removePrefix(SKIN_PREFIX).uppercase()
                    runCatching { AvatarKind.valueOf(kindName) }
                        .onSuccess { StoreManager.unlockByIap(it) }
                }
            }
        }
    }

    /** Ruft den wartenden Kauf-Callback genau einmal auf dem Main-Thread auf. */
    private fun finishPending(success: Boolean, sku: String?) {
        val cb = pendingCallback ?: return
        pendingCallback = null
        mainHandler.post { cb(success, sku) }
    }

    /**
     * Beim Start bestehende Käufe abgleichen (Gerätewechsel, Deinstallation, etc.).
     * Wird automatisch nach erfolgreichem BillingClient-Setup aufgerufen.
     * Löst bewusst KEINEN pendingCallback aus (kein laufender Kauffluss).
     */
    private fun queryAndRestorePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { _, purchases ->
            for (purchase in purchases) handlePurchase(purchase)
        }
    }

    fun destroy() {
        billingClient.endConnection()
    }
}
