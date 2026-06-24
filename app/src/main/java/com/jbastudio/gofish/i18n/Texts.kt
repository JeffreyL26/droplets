package com.jbastudio.gofish.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import com.jbastudio.gofish.ui.components.AvatarColor
import com.jbastudio.gofish.ui.components.AvatarKind

/** Verfügbare Sprachen. Standard ist Deutsch. */
enum class Language(val tag: String) {
    DE("de"), ES("es"), EN("en"), FR("fr"), ZH("zh"), TL("tl")
}

fun languageFromTag(tag: String?): Language =
    Language.entries.firstOrNull { it.tag == tag } ?: Language.DE

/** Liefert die Texte für eine Sprache. */
fun textsFor(language: Language): Texts = when (language) {
    Language.DE -> DeTexts
    Language.ES -> EsTexts
    Language.EN -> EnTexts
    Language.FR -> FrTexts
    Language.ZH -> ZhTexts
    Language.TL -> TlTexts
}

/** Aktuelle Texte für Composables. */
val LocalTexts = staticCompositionLocalOf<Texts> { DeTexts }

/**
 * Alle anzeigbaren Texte der App — außer dem Titel „Go Fish!", der bewusst
 * unübersetzt bleibt. Parametrierte Texte sind Funktionen.
 */
interface Texts {
    // ── Hauptmenü / allgemein ──
    val tagline: String
    val findGame: String
    val hostLocal: String

    // ── Spieler-Karte ──
    val yourName: String
    val namePlaceholder: String
    fun hello(name: String): String      // Begrüßung im Hauptmenü, wenn ein Name gesetzt ist
    val yourAvatar: String
    fun avatarSummary(kind: String, color: String): String

    // ── Avatar-Dialog ──
    val chooseAvatarTitle: String
    val chooseAvatarSubtitle: String
    val colorLabel: String
    val doneBtn: String
    fun kindName(kind: AvatarKind): String
    fun colorName(color: AvatarColor): String

    // ── Navigation ──
    val mainMenu: String
    val backBtn: String

    // ── Einstellungen / Info ──
    val settingsTitle: String
    val publishedBy: String
    /** Titel des Soundeffekt-Reglers. */
    val soundLabel: String
    /** Titel des Musik-Reglers (Hintergrundmusik, getrennt vom Soundeffekt-Regler). */
    val musicLabel: String
    /** Label für die Gegnerzahl-Auswahl in der Lobby. */
    val opponentsLabel: String
    /** Wartehinweis beim Hosten/Beitreten mit mehreren Spielern (X von N verbunden). */
    fun lobbyWaiting(joined: Int, expected: Int): String
    /** Fehler: Online-Mehrspieler vom Relay (noch) nicht unterstützt. */
    val errOnlineMultiUnavailable: String

    // ── Lokal-Bildschirm ──
    val localTitle: String
    val localSubtitle: String
    val hostGame: String
    fun yourIp(ip: String): String
    val startServer: String
    val joinGame: String
    val hostIpPlaceholder: String
    val enterHostIp: String
    val joinBtn: String

    // ── Hosting-Dialog ──
    val hostingDialogTitle: String
    val shareIp: String
    val waitingForCoPlayer: String
    val cancelHosting: String
    val cancelConnection: String

    // ── Online-Bildschirm ──
    val findGameTitle: String
    val anyGame: String          // „Beliebiges Spiel" — Standard-Button (any-Match)
    val orPickCount: String      // Hinweis über der festen Gegnerzahl-Auswahl
    // ── Store / Monetarisierung ──
    val adFreeBtn: String        // „Werbefrei spielen"-Button
    val adFreeActive: String     // Status, wenn Ad-Free gekauft
    val watchAdUnlock: String    // Rewarded-Video-Unlock eines Skins
    val buyUnlock: String        // IAP-Unlock eines Skins
    val privacyOptionsBtn: String // UMP-Datenschutzoptionen (Einwilligung ändern/widerrufen)
    val onlineSubtitle: String
    val searchingOpponent: String
    val waitingForPlayer: String
    val cancelSearch: String
    val serverNotConfiguredTitle: String
    val serverNotConfiguredBody: String
    val setServer: String
    val changeServer: String
    val opponentFound: String

    // ── Server-Dialog ──
    val serverAddressTitle: String
    val serverDialogBody: String
    fun defaultServerLabel(url: String): String
    val applyBtn: String
    val useDefaultBtn: String

    // ── Status / Abbruch ──
    val startingServer: String
    fun connectingTo(ip: String): String
    val hostingCancelled: String
    val searchCancelled: String
    val operationCancelled: String
    val connectedWaiting: String
    val serverNotSetUp: String
    val defaultNamePlayer: String
    val defaultNameHost: String
    val defaultNameGuest: String

    // ── Fehlermeldungen ──
    val errHostingEnded: String
    val errConnectionLost: String
    val errServerConnLost: String
    val errInvalidServer: String
    val errServerUnreachableRelay: String
    val errServerUnreachableIp: String
    val errTimeout: String
    val errNoNetwork: String
    val errNoRoute: String
    val errPermission: String
    fun errGeneric(raw: String): String
    val unknownIp: String

    // ── Spiel ──
    val you: String
    val opponentDefault: String
    fun gameStartedAs(name: String): String
    fun youAskedGot(opponent: String, rank: String, n: Int): String
    fun youAskedGoFish(rank: String): String
    fun drawnCard(card: String): String
    fun drawnCardHit(card: String): String
    val deckEmpty: String
    fun oppAskedGot(rank: String, n: Int): String
    fun oppAskedGoFish(rank: String, wentFishing: Boolean): String
    /** Gegner zog beim Angeln die gesuchte Karte und ist erneut am Zug. */
    fun oppDrawnHit(rank: String): String
    /** Mehr-Gegner-Log: „fragte <Ziel> nach <Rang>" (Ziel benannt). */
    fun oppAskedGotAt(target: String, rank: String, n: Int): String
    fun oppAskedGoFishAt(target: String, rank: String): String
    /** „Du" als Frage-Ziel (z. B. „fragte dich"). */
    val youObject: String
    /** Ein Spieler hat die Partie verlassen — Karten zurück ins Deck. */
    fun playerLeftReshuffle(name: String): String
    /** Fehlermeldungen bei fehlender Auswahl. */
    val toastChooseCard: String
    val toastChooseOpponent: String
    fun youBook(rank: String): String
    fun oppBook(rank: String): String
    val gameOverLog: String
    val toastNoSuchCard: String
    fun toastMustHold(rank: String): String
    fun opponentLeft(opponent: String): String
    val connectionLostSession: String

    // ── Spiel-UI ──
    val deckBadge: String
    val yourBooks: String
    fun handAndBooks(cards: Int, books: Int): String
    val yourHand: String
    val scrollHint: String
    val noCardsLeft: String
    val logEmpty: String
    val chooseCard: String
    fun askFor(rank: String): String
    val turnYou: String
    fun turnWaiting(opponent: String): String
    fun turnGameOver(winner: String): String
    val exitTitle: String
    val exitBody: String
    val leaveBtn: String
    val stayBtn: String
    val sessionEndedTitle: String
    val toLobbyBtn: String
    val winTitle: String
    val loseTitle: String
    val tieTitle: String
    val winSubtitle: String
    fun loseSubtitle(winner: String): String
    val tieSubtitle: String
    val toMainMenu: String
    /** Lokalisierte Anzeige für ein Unentschieden (Server-Sentinel bleibt sprachneutral). */
    val drawWord: String

    // ── Animations-Overlays (Untertitel; "Go Fish!" bleibt unübersetzt) ──
    val animDrawCard: String
    val animDeckEmpty: String
    val animCaught: String
    /** Splash, wenn der Gegner einem Karten abnimmt (eigene Karten fliegen weg). */
    val animGaveAway: String
    val animBook: String
    fun animBookComplete(rank: String): String

    // ── Tutorial ──
    val tutorialBtn: String
    val welcomeTitle: String
    val welcomeQuestion: String
    val welcomeYes: String
    val welcomePlay: String
    val tutorialHintText: String
    val tutorialHintOk: String
    val tutorialNext: String
    val tutorialLeave: String
    val tutorialPickCard: String
    val tutorialPressAsk: String
    val tutorialIntro: String
    val tutorialAskIntro: String
    val tutorialGoFishIntro: String
    val tutorialOppTurn: String
    val tutorialFinish: String
    fun tutorialStole1(rank: String): String
    fun tutorialStole2(rank: String): String
    fun tutorialStole3(rank: String): String
    fun tutorialBook(rank: String): String
    fun tutorialGoFish(rank: String): String
}

// ════════════════════════════════════════════════════════════════════════════
//  DEUTSCH (Standard)
// ════════════════════════════════════════════════════════════════════════════

object DeTexts : Texts {
    override val tagline = "Schnapp dir den Fang!"
    override val findGame = "Finde ein Spiel!"
    override val hostLocal = "Lokal hosten"

    override val yourName = "Dein Name"
    override val namePlaceholder = "z. B. Käpt'n Nemo"
    override fun hello(name: String) = "Hallo $name!"
    override val yourAvatar = "Dein Avatar"
    override fun avatarSummary(kind: String, color: String) = "$kind · $color"

    override val chooseAvatarTitle = "Avatar wählen"
    override val chooseAvatarSubtitle = "Such dir ein Meeresbewohner aus."
    override val colorLabel = "Farbe"
    override val doneBtn = "Fertig"
    override fun kindName(kind: AvatarKind) = when (kind) {
        AvatarKind.FISH -> "Fisch"
        AvatarKind.SHARK -> "Hai"
        AvatarKind.WHALE -> "Wal"
        AvatarKind.DOLPHIN -> "Delfin"
        AvatarKind.PUFFER -> "Kugelfisch"
        AvatarKind.STARFISH -> "Seestern"
        AvatarKind.MOBY -> "Moby Dick"
        AvatarKind.NESSIE -> "Nessie"
        AvatarKind.BLOBFISH -> "Blobfisch"
        AvatarKind.KOI -> "Koifisch"
        AvatarKind.BACKFISCH -> "Backfisch"
        AvatarKind.MEGALODON -> "Megalodon"
    }
    override fun colorName(color: AvatarColor) = when (color) {
        AvatarColor.SUN -> "Sonnengelb"
        AvatarColor.CORAL -> "Korallrosa"
        AvatarColor.SEAFOAM -> "Seafoam"
        AvatarColor.LAVENDER -> "Lavendel"
        AvatarColor.OCEAN -> "Ozeanblau"
        AvatarColor.SANDY -> "Sandbeige"
    }

    override val mainMenu = "Hauptmenü"
    override val backBtn = "Zurück"
    override val settingsTitle = "Einstellungen"
    override val publishedBy = "Herausgeber"
    override val soundLabel = "Ton"
    override val musicLabel = "Musik"
    override val opponentsLabel = "Gegner"
    override fun lobbyWaiting(joined: Int, expected: Int) = "Warte auf Mitspieler … ($joined/$expected)"
    override val errOnlineMultiUnavailable = "Online-Mehrspieler ist noch nicht verfügbar."

    override val localTitle = "Lokal spielen"
    override val localSubtitle = "Hosten oder im selben WLAN beitreten"
    override val hostGame = "Spiel hosten"
    override fun yourIp(ip: String) = "Deine IP: $ip"
    override val startServer = "Server starten"
    override val joinGame = "Spiel beitreten"
    override val hostIpPlaceholder = "Host-IP (z. B. 192.168.0.42)"
    override val enterHostIp = "Bitte Host-IP eingeben"
    override val joinBtn = "Beitreten"

    override val hostingDialogTitle = "Spiel hosten"
    override val shareIp = "Gib diese IP an deinen Mitspieler weiter:"
    override val waitingForCoPlayer = "Warte auf Mitspieler …"
    override val cancelHosting = "Hosting abbrechen"
    override val cancelConnection = "Verbindung abbrechen"

    override val findGameTitle = "Spiel finden"
    override val anyGame = "Beliebiges Spiel"
    override val orPickCount = "… oder feste Gegnerzahl wählen"
    override val adFreeBtn = "Werbefrei spielen"
    override val adFreeActive = "✅ Werbefrei"
    override val watchAdUnlock = "Video ansehen"
    override val buyUnlock = "Kaufen"
    override val privacyOptionsBtn = "Datenschutzoptionen"
    override val onlineSubtitle = "Automatisch mit einem Gegner verbinden"
    override val searchingOpponent = "Suche nach Gegner …"
    override val waitingForPlayer = "Warte auf einen anderen Spieler, der gerade sucht."
    override val cancelSearch = "Suche abbrechen"
    override val serverNotConfiguredTitle = "Online-Server noch nicht eingerichtet"
    override val serverNotConfiguredBody =
        "Damit sich Spieler über das Internet automatisch finden, muss einmalig ein " +
            "Relay-Server laufen. Trag seine Adresse hier ein."
    override val setServer = "Server festlegen"
    override val changeServer = "Server ändern"
    override val opponentFound = "Gegner gefunden! Spiel startet …"

    override val serverAddressTitle = "Server-Adresse"
    override val serverDialogBody =
        "Nur zum Testen oder für einen eigenen Server nötig. Leer lassen = Standard-Server der App."
    override fun defaultServerLabel(url: String) = "Standard: $url"
    override val applyBtn = "Übernehmen"
    override val useDefaultBtn = "Standard verwenden"

    override val startingServer = "Server wird gestartet …"
    override fun connectingTo(ip: String) = "Verbinde mit $ip …"
    override val hostingCancelled = "Hosting abgebrochen."
    override val searchCancelled = "Suche abgebrochen."
    override val operationCancelled = "Vorgang abgebrochen."
    override val connectedWaiting = "Verbunden! Warte auf Spielstart …"
    override val serverNotSetUp = "Online-Server ist noch nicht eingerichtet."
    override val defaultNamePlayer = "Spieler"
    override val defaultNameHost = "Host"
    override val defaultNameGuest = "Gast"

    override val errHostingEnded = "Hosting beendet."
    override val errConnectionLost = "Verbindung wurde getrennt."
    override val errServerConnLost = "Verbindung zum Server getrennt."
    override val errInvalidServer = "Server-Adresse ungültig oder nicht erreichbar."
    override val errServerUnreachableRelay = "Server nicht erreichbar — läuft der Relay-Server?"
    override val errServerUnreachableIp = "Server nicht erreichbar — IP korrekt eingegeben?"
    override val errTimeout = "Zeitüberschreitung — Server antwortet nicht."
    override val errNoNetwork = "Kein Netzwerk verfügbar."
    override val errNoRoute = "Server unter dieser IP nicht erreichbar."
    override val errPermission = "Zugriff verweigert. Hat die App Netzwerk-Rechte?"
    override fun errGeneric(raw: String) = "Fehler: $raw"
    override val unknownIp = "Unbekannt"

    override val you = "Du"
    override val opponentDefault = "Gegner"
    override fun gameStartedAs(name: String) = "🎬 Spiel gestartet — du spielst als $name"
    override fun youAskedGot(opponent: String, rank: String, n: Int) =
        "fragtest nach $rank → $opponent gibt dir $n Karte(n) — nochmal dran."
    override fun youAskedGoFish(rank: String) = "fragtest nach $rank → Go Fish!"
    override fun drawnCard(card: String) = "   ↳ Gezogen: $card"
    override fun drawnCardHit(card: String) = "   ↳ Gezogen: $card  ✓ Treffer — nochmal dran."
    override val deckEmpty = "   ↳ Deck ist leer."
    override fun oppAskedGot(rank: String, n: Int) = "fragte nach $rank → nimmt $n Karte(n) von dir."
    override fun oppAskedGoFish(rank: String, wentFishing: Boolean) =
        "fragte nach $rank → Go Fish!" + if (wentFishing) " Zieht eine Karte." else ""
    override fun oppDrawnHit(rank: String) = "   ↳ Gezogen: $rank  ✓ Treffer — nochmal dran."
    override fun oppAskedGotAt(target: String, rank: String, n: Int) = "fragte $target nach $rank → $n Karte(n)"
    override fun oppAskedGoFishAt(target: String, rank: String) = "fragte $target nach $rank → Go Fish!"
    override val youObject = "dich"
    override fun playerLeftReshuffle(name: String) = "$name hat verlassen — Karten zurück ins Deck."
    override val toastChooseCard = "Wähle zuerst eine Karte."
    override val toastChooseOpponent = "Wähle zuerst einen Gegner."
    override fun youBook(rank: String) = "hast ein ${rank}er-Buch abgelegt!"
    override fun oppBook(rank: String) = "hat ein ${rank}er-Buch abgelegt!"
    override val gameOverLog = "🏁 Spiel beendet."
    override val toastNoSuchCard = "Diese Karte gibt es nicht."
    override fun toastMustHold(rank: String) = "Du musst mindestens eine $rank-Karte halten!"
    override fun opponentLeft(opponent: String) = "$opponent hat das Spiel verlassen.\nDie Sitzung wird beendet."
    override val connectionLostSession = "Verbindung zum Mitspieler verloren.\nDie Sitzung wird beendet."

    override val deckBadge = "🎴 Deck"
    override val yourBooks = "Deine Bücher"
    override fun handAndBooks(cards: Int, books: Int) = "🃏 $cards Karten   📚 $books Bücher"
    override val yourHand = "Deine Hand"
    override val scrollHint = "↕ scrollen"
    override val noCardsLeft = "Keine Karten mehr."
    override val logEmpty = "🌊 Noch nichts passiert …"
    override val chooseCard = "Karte wählen"
    override fun askFor(rank: String) = "Frage nach $rank"
    override val turnYou = "⬆️  Du bist dran — wähle deine Karte aus!"
    override fun turnWaiting(opponent: String) = "⏳  Warte auf $opponent …"
    override fun turnGameOver(winner: String) = "🏆 Spiel vorbei — $winner gewinnt!"
    override val exitTitle = "Spiel verlassen?"
    override val exitBody = "Sicher, dass du verlassen möchtest? Die Sitzung wird dadurch beendet."
    override val leaveBtn = "Verlassen"
    override val stayBtn = "Bleiben"
    override val sessionEndedTitle = "🌊 Sitzung beendet"
    override val toLobbyBtn = "Zur Lobby"
    override val winTitle = "Sieg!"
    override val loseTitle = "Verloren"
    override val tieTitle = "Unentschieden"
    override val winSubtitle = "Du hast die meisten Bücher gesammelt!"
    override fun loseSubtitle(winner: String) = "$winner hat das Spiel gewonnen."
    override val tieSubtitle = "Gleichstand — beide haben gleich viele Bücher."
    override val toMainMenu = "Zum Hauptmenü"
    override val drawWord = "Unentschieden"

    override val animDrawCard = "du ziehst eine Karte"
    override val animDeckEmpty = "Deck ist leer"
    override val animCaught = "geangelt!"
    override val animGaveAway = "abgegeben!"
    override val animBook = "📚  BUCH!"
    override fun animBookComplete(rank: String) = "${rank}er komplett"

    // ── Tutorial ──
    override val tutorialBtn = "Tutorial"
    override val welcomeTitle = "Willkommen zu GoFish!"
    override val welcomeQuestion = "Weißt du, wie man spielt?"
    override val welcomeYes = "Ja – lass uns loslegen"
    override val welcomePlay = "Tutorial spielen"
    override val tutorialHintText = "Kein Problem! Du kannst das Tutorial jederzeit über die Einstellungen starten."
    override val tutorialHintOk = "Alles klar"
    override val tutorialNext = "Weiter"
    override val tutorialLeave = "Verlassen"
    override val tutorialPickCard = "Tippe die markierte Karte an."
    override val tutorialPressAsk = "Tippe jetzt unten auf den Frage-Knopf."
    override val tutorialIntro = "Hallo! Ich zeige dir, wie GoFish läuft. Ziel: Sammle Bücher – vier Karten mit demselben Wert. Wer am Ende die meisten Bücher hat, gewinnt."
    override val tutorialAskIntro = "Du bist am Zug. Frage deinen Gegner nach einem Wert, den du selbst auf der Hand hast. Beginnen wir mit den 7ern."
    override val tutorialGoFishIntro = "Frag jetzt nach einem Wert, den dein Gegner wahrscheinlich nicht hat – den Damen."
    override val tutorialOppTurn = "Jetzt ist Fisherman am Zug. Er fragt dich nach Königen und nimmt sie dir ab. So spielt ihr abwechselnd."
    override val tutorialFinish = "Du hast alles gelernt. Besiege deinen Gegner!"
    override fun tutorialStole1(rank: String) = "Fisherman hatte eine ${rank} – du nimmst sie dir! Weil du eine Karte bekommen hast, bist du gleich nochmal dran."
    override fun tutorialStole2(rank: String) = "Stark! Fisherman hatte zwei ${rank}. Beide wandern auf deine Hand – und du bleibst am Zug."
    override fun tutorialStole3(rank: String) = "Wow, gleich drei ${rank}! Jetzt hast du vier davon …"
    override fun tutorialBook(rank: String) = "… und vier gleiche Karten sind ein Buch! Dein ${rank}-Buch wird beiseitegelegt. Genau darum geht es."
    override fun tutorialGoFish(rank: String) = "Go Fish! Fisherman hatte keine ${rank}, also angelst du eine Karte vom Stapel. Passt sie nicht, ist dein Zug vorbei."
}

// ════════════════════════════════════════════════════════════════════════════
//  ESPAÑOL
// ════════════════════════════════════════════════════════════════════════════

object EsTexts : Texts {
    override val tagline = "¡Atrapa tu pesca!"
    override val findGame = "¡Busca partida!"
    override val hostLocal = "Crear partida local"

    override val yourName = "Tu nombre"
    override val namePlaceholder = "p. ej. Capitán Nemo"
    override fun hello(name: String) = "¡Hola, $name!"
    override val yourAvatar = "Tu avatar"
    override fun avatarSummary(kind: String, color: String) = "$kind · $color"

    override val chooseAvatarTitle = "Elige un avatar"
    override val chooseAvatarSubtitle = "Elige una criatura marina."
    override val colorLabel = "Color"
    override val doneBtn = "Listo"
    override fun kindName(kind: AvatarKind) = when (kind) {
        AvatarKind.FISH -> "Pez"
        AvatarKind.SHARK -> "Tiburón"
        AvatarKind.WHALE -> "Ballena"
        AvatarKind.DOLPHIN -> "Delfín"
        AvatarKind.PUFFER -> "Pez globo"
        AvatarKind.STARFISH -> "Estrella"
        AvatarKind.MOBY -> "Moby Dick"
        AvatarKind.NESSIE -> "Nessie"
        AvatarKind.BLOBFISH -> "Pez borrón"
        AvatarKind.KOI -> "Carpa koi"
        AvatarKind.BACKFISCH -> "Pescado frito"
        AvatarKind.MEGALODON -> "Megalodón"
    }
    override fun colorName(color: AvatarColor) = when (color) {
        AvatarColor.SUN -> "Amarillo"
        AvatarColor.CORAL -> "Coral"
        AvatarColor.SEAFOAM -> "Espuma"
        AvatarColor.LAVENDER -> "Lavanda"
        AvatarColor.OCEAN -> "Azul océano"
        AvatarColor.SANDY -> "Arena"
    }

    override val mainMenu = "Menú principal"
    override val backBtn = "Atrás"
    override val settingsTitle = "Ajustes"
    override val publishedBy = "Editor"
    override val soundLabel = "Sonido"
    override val musicLabel = "Música"
    override val opponentsLabel = "Rivales"
    override fun lobbyWaiting(joined: Int, expected: Int) = "Esperando jugadores … ($joined/$expected)"
    override val errOnlineMultiUnavailable = "El multijugador en línea aún no está disponible."

    override val localTitle = "Juego local"
    override val localSubtitle = "Crea o únete en la misma red WiFi"
    override val hostGame = "Crear partida"
    override fun yourIp(ip: String) = "Tu IP: $ip"
    override val startServer = "Iniciar servidor"
    override val joinGame = "Unirse a partida"
    override val hostIpPlaceholder = "IP del anfitrión (p. ej. 192.168.0.42)"
    override val enterHostIp = "Introduce la IP del anfitrión"
    override val joinBtn = "Unirse"

    override val hostingDialogTitle = "Crear partida"
    override val shareIp = "Comparte esta IP con tu compañero:"
    override val waitingForCoPlayer = "Esperando a otro jugador …"
    override val cancelHosting = "Cancelar"
    override val cancelConnection = "Cancelar conexión"

    override val findGameTitle = "Buscar partida"
    override val anyGame = "Partida cualquiera"
    override val orPickCount = "… o elige el número de rivales"
    override val adFreeBtn = "Jugar sin anuncios"
    override val adFreeActive = "✅ Sin anuncios"
    override val watchAdUnlock = "Ver vídeo"
    override val buyUnlock = "Comprar"
    override val privacyOptionsBtn = "Opciones de privacidad"
    override val onlineSubtitle = "Conéctate automáticamente con un rival"
    override val searchingOpponent = "Buscando rival …"
    override val waitingForPlayer = "Esperando a otro jugador que esté buscando."
    override val cancelSearch = "Cancelar búsqueda"
    override val serverNotConfiguredTitle = "Servidor en línea no configurado"
    override val serverNotConfiguredBody =
        "Para que los jugadores se encuentren automáticamente por internet, debe haber " +
            "un servidor de relay en marcha. Introduce su dirección aquí."
    override val setServer = "Configurar servidor"
    override val changeServer = "Cambiar servidor"
    override val opponentFound = "¡Rival encontrado! Empezando …"

    override val serverAddressTitle = "Dirección del servidor"
    override val serverDialogBody =
        "Solo es necesario para pruebas o un servidor propio. Déjalo vacío = servidor estándar de la app."
    override fun defaultServerLabel(url: String) = "Predeterminado: $url"
    override val applyBtn = "Aplicar"
    override val useDefaultBtn = "Usar predeterminado"

    override val startingServer = "Iniciando servidor …"
    override fun connectingTo(ip: String) = "Conectando con $ip …"
    override val hostingCancelled = "Partida cancelada."
    override val searchCancelled = "Búsqueda cancelada."
    override val operationCancelled = "Operación cancelada."
    override val connectedWaiting = "¡Conectado! Esperando el inicio …"
    override val serverNotSetUp = "El servidor en línea aún no está configurado."
    override val defaultNamePlayer = "Jugador"
    override val defaultNameHost = "Anfitrión"
    override val defaultNameGuest = "Invitado"

    override val errHostingEnded = "Partida finalizada."
    override val errConnectionLost = "Se perdió la conexión."
    override val errServerConnLost = "Se perdió la conexión con el servidor."
    override val errInvalidServer = "Dirección de servidor no válida o inaccesible."
    override val errServerUnreachableRelay = "Servidor inaccesible — ¿está activo el servidor de relay?"
    override val errServerUnreachableIp = "Servidor inaccesible — ¿IP correcta?"
    override val errTimeout = "Tiempo agotado — el servidor no responde."
    override val errNoNetwork = "No hay red disponible."
    override val errNoRoute = "Servidor inaccesible en esta IP."
    override val errPermission = "Acceso denegado. ¿Tiene la app permisos de red?"
    override fun errGeneric(raw: String) = "Error: $raw"
    override val unknownIp = "Desconocida"

    override val you = "Tú"
    override val opponentDefault = "Rival"
    override fun gameStartedAs(name: String) = "🎬 Partida iniciada — juegas como $name"
    override fun youAskedGot(opponent: String, rank: String, n: Int) =
        "pediste $rank → $opponent te da $n carta(s) — turno extra."
    override fun youAskedGoFish(rank: String) = "pediste $rank → ¡Go Fish!"
    override fun drawnCard(card: String) = "   ↳ Robada: $card"
    override fun drawnCardHit(card: String) = "   ↳ Robada: $card  ✓ ¡Acierto — turno extra!"
    override val deckEmpty = "   ↳ El mazo está vacío."
    override fun oppAskedGot(rank: String, n: Int) = "pidió $rank → te quita $n carta(s)."
    override fun oppAskedGoFish(rank: String, wentFishing: Boolean) =
        "pidió $rank → ¡Go Fish!" + if (wentFishing) " Roba una carta." else ""
    override fun oppDrawnHit(rank: String) = "   ↳ Robada: $rank  ✓ ¡Acierto — turno extra!"
    override fun oppAskedGotAt(target: String, rank: String, n: Int) = "pidió $rank a $target → $n carta(s)"
    override fun oppAskedGoFishAt(target: String, rank: String) = "pidió $rank a $target → ¡Go Fish!"
    override val youObject = "ti"
    override fun playerLeftReshuffle(name: String) = "$name se fue — cartas de vuelta al mazo."
    override val toastChooseCard = "Elige primero una carta."
    override val toastChooseOpponent = "Elige primero un rival."
    override fun youBook(rank: String) = "¡completaste un cuarteto de $rank!"
    override fun oppBook(rank: String) = "¡completó un cuarteto de $rank!"
    override val gameOverLog = "🏁 Partida terminada."
    override val toastNoSuchCard = "Esa carta no existe."
    override fun toastMustHold(rank: String) = "¡Debes tener al menos una carta $rank!"
    override fun opponentLeft(opponent: String) = "$opponent ha abandonado la partida.\nLa sesión finaliza."
    override val connectionLostSession = "Se perdió la conexión con el otro jugador.\nLa sesión finaliza."

    override val deckBadge = "🎴 Mazo"
    override val yourBooks = "Tus cuartetos"
    override fun handAndBooks(cards: Int, books: Int) = "🃏 $cards cartas   📚 $books cuartetos"
    override val yourHand = "Tu mano"
    override val scrollHint = "↕ desliza"
    override val noCardsLeft = "Sin cartas."
    override val logEmpty = "🌊 Aún no ha pasado nada …"
    override val chooseCard = "Elige una carta"
    override fun askFor(rank: String) = "Pide $rank"
    override val turnYou = "⬆️  ¡Es tu turno: elige una carta!"
    override fun turnWaiting(opponent: String) = "⏳  Esperando a $opponent …"
    override fun turnGameOver(winner: String) = "🏆 ¡Fin de la partida — gana $winner!"
    override val exitTitle = "¿Salir de la partida?"
    override val exitBody = "¿Seguro que quieres salir? Esto finaliza la sesión."
    override val leaveBtn = "Salir"
    override val stayBtn = "Quedarse"
    override val sessionEndedTitle = "🌊 Sesión finalizada"
    override val toLobbyBtn = "Al menú"
    override val winTitle = "¡Victoria!"
    override val loseTitle = "Derrota"
    override val tieTitle = "Empate"
    override val winSubtitle = "¡Reuniste la mayoría de los cuartetos!"
    override fun loseSubtitle(winner: String) = "$winner ha ganado la partida."
    override val tieSubtitle = "Empate: ambos tienen la misma cantidad de cuartetos."
    override val toMainMenu = "Al menú principal"
    override val drawWord = "Empate"

    override val animDrawCard = "robas una carta"
    override val animDeckEmpty = "El mazo está vacío"
    override val animCaught = "¡pescado!"
    override val animGaveAway = "¡entregadas!"
    override val animBook = "📚  ¡CUARTETO!"
    override fun animBookComplete(rank: String) = "$rank completo"

    // ── Tutorial ──
    override val tutorialBtn = "Tutorial"
    override val welcomeTitle = "¡Bienvenido a GoFish!"
    override val welcomeQuestion = "¿Sabes cómo se juega?"
    override val welcomeYes = "Sí, ¡vamos a empezar!"
    override val welcomePlay = "Jugar tutorial"
    override val tutorialHintText = "¡Sin problema! Puedes empezar el tutorial cuando quieras desde los ajustes."
    override val tutorialHintOk = "Entendido"
    override val tutorialNext = "Siguiente"
    override val tutorialLeave = "Salir"
    override val tutorialPickCard = "Toca la carta resaltada."
    override val tutorialPressAsk = "Toca «Pedir» para preguntar."
    override val tutorialIntro = "¡Hola! Te enseño cómo funciona GoFish. Objetivo: reúne cuartetos: cuatro cartas del mismo valor. Gana quien tenga más cuartetos al final."
    override val tutorialAskIntro = "Es tu turno. Pide a tu rival un valor que tú también tengas. Empecemos con los 7."
    override val tutorialGoFishIntro = "Ahora pide un valor que tu rival probablemente no tenga: las reinas (Q)."
    override val tutorialOppTurn = "Ahora le toca a Fisherman. Te pide reyes (K) y te los quita. Así jugáis por turnos."
    override val tutorialFinish = "Lo has aprendido todo. ¡Ahora vence a tu rival!"
    override fun tutorialStole1(rank: String) = "Fisherman tenía un ${rank}, ¡te lo llevas! Como conseguiste una carta, juegas otra vez."
    override fun tutorialStole2(rank: String) = "¡Bien! Fisherman tenía dos ${rank}. Las dos pasan a tu mano y sigues tu turno."
    override fun tutorialStole3(rank: String) = "¡Vaya, tres ${rank} de golpe! Ahora tienes cuatro …"
    override fun tutorialBook(rank: String) = "… ¡y cuatro cartas iguales forman un cuarteto! Tu cuarteto de ${rank} se aparta. De eso se trata."
    override fun tutorialGoFish(rank: String) = "«¡Go Fish!» Fisherman no tenía ningún ${rank}, así que robas una carta del mazo. Si no coincide, tu turno termina."
}

// ════════════════════════════════════════════════════════════════════════════
//  ENGLISH
// ════════════════════════════════════════════════════════════════════════════

object EnTexts : Texts {
    override val tagline = "Reel in your catch!"
    override val findGame = "Find a game!"
    override val hostLocal = "Host locally"

    override val yourName = "Your name"
    override val namePlaceholder = "e.g. Captain Nemo"
    override fun hello(name: String) = "Hello, $name!"
    override val yourAvatar = "Your avatar"
    override fun avatarSummary(kind: String, color: String) = "$kind · $color"

    override val chooseAvatarTitle = "Choose an avatar"
    override val chooseAvatarSubtitle = "Pick a sea creature."
    override val colorLabel = "Color"
    override val doneBtn = "Done"
    override fun kindName(kind: AvatarKind) = when (kind) {
        AvatarKind.FISH -> "Fish"
        AvatarKind.SHARK -> "Shark"
        AvatarKind.WHALE -> "Whale"
        AvatarKind.DOLPHIN -> "Dolphin"
        AvatarKind.PUFFER -> "Pufferfish"
        AvatarKind.STARFISH -> "Starfish"
        AvatarKind.MOBY -> "Moby Dick"
        AvatarKind.NESSIE -> "Nessie"
        AvatarKind.BLOBFISH -> "Blobfish"
        AvatarKind.KOI -> "Koi"
        AvatarKind.BACKFISCH -> "Fried Fish"
        AvatarKind.MEGALODON -> "Megalodon"
    }
    override fun colorName(color: AvatarColor) = when (color) {
        AvatarColor.SUN -> "Sunny"
        AvatarColor.CORAL -> "Coral"
        AvatarColor.SEAFOAM -> "Seafoam"
        AvatarColor.LAVENDER -> "Lavender"
        AvatarColor.OCEAN -> "Ocean"
        AvatarColor.SANDY -> "Sandy"
    }

    override val mainMenu = "Main menu"
    override val backBtn = "Back"
    override val settingsTitle = "Settings"
    override val publishedBy = "Publisher"
    override val soundLabel = "Sound"
    override val musicLabel = "Music"
    override val opponentsLabel = "Opponents"
    override fun lobbyWaiting(joined: Int, expected: Int) = "Waiting for players … ($joined/$expected)"
    override val errOnlineMultiUnavailable = "Online multiplayer isn't available yet."

    override val localTitle = "Local play"
    override val localSubtitle = "Host or join on the same Wi-Fi"
    override val hostGame = "Host game"
    override fun yourIp(ip: String) = "Your IP: $ip"
    override val startServer = "Start server"
    override val joinGame = "Join game"
    override val hostIpPlaceholder = "Host IP (e.g. 192.168.0.42)"
    override val enterHostIp = "Please enter the host IP"
    override val joinBtn = "Join"

    override val hostingDialogTitle = "Host game"
    override val shareIp = "Share this IP with your opponent:"
    override val waitingForCoPlayer = "Waiting for another player …"
    override val cancelHosting = "Cancel hosting"
    override val cancelConnection = "Cancel connection"

    override val findGameTitle = "Find a game"
    override val anyGame = "Any game"
    override val orPickCount = "… or pick a number of opponents"
    override val adFreeBtn = "Go ad-free"
    override val adFreeActive = "✅ Ad-free"
    override val watchAdUnlock = "Watch video"
    override val buyUnlock = "Buy"
    override val privacyOptionsBtn = "Privacy options"
    override val onlineSubtitle = "Connect with an opponent automatically"
    override val searchingOpponent = "Looking for an opponent …"
    override val waitingForPlayer = "Waiting for another player who's searching."
    override val cancelSearch = "Cancel search"
    override val serverNotConfiguredTitle = "Online server not set up yet"
    override val serverNotConfiguredBody =
        "For players to find each other automatically over the internet, a relay server " +
            "must be running. Enter its address here."
    override val setServer = "Set server"
    override val changeServer = "Change server"
    override val opponentFound = "Opponent found! Starting …"

    override val serverAddressTitle = "Server address"
    override val serverDialogBody =
        "Only needed for testing or your own server. Leave empty = the app's default server."
    override fun defaultServerLabel(url: String) = "Default: $url"
    override val applyBtn = "Apply"
    override val useDefaultBtn = "Use default"

    override val startingServer = "Starting server …"
    override fun connectingTo(ip: String) = "Connecting to $ip …"
    override val hostingCancelled = "Hosting cancelled."
    override val searchCancelled = "Search cancelled."
    override val operationCancelled = "Operation cancelled."
    override val connectedWaiting = "Connected! Waiting for the game to start …"
    override val serverNotSetUp = "The online server isn't set up yet."
    override val defaultNamePlayer = "Player"
    override val defaultNameHost = "Host"
    override val defaultNameGuest = "Guest"

    override val errHostingEnded = "Hosting ended."
    override val errConnectionLost = "The connection was lost."
    override val errServerConnLost = "Lost connection to the server."
    override val errInvalidServer = "Server address invalid or unreachable."
    override val errServerUnreachableRelay = "Server unreachable — is the relay server running?"
    override val errServerUnreachableIp = "Server unreachable — is the IP correct?"
    override val errTimeout = "Timed out — the server isn't responding."
    override val errNoNetwork = "No network available."
    override val errNoRoute = "Server unreachable at this IP."
    override val errPermission = "Access denied. Does the app have network permission?"
    override fun errGeneric(raw: String) = "Error: $raw"
    override val unknownIp = "Unknown"

    override val you = "You"
    override val opponentDefault = "Opponent"
    override fun gameStartedAs(name: String) = "🎬 Game started — you're playing as $name"
    override fun youAskedGot(opponent: String, rank: String, n: Int) =
        "asked for $rank → $opponent gives you $n card(s) — go again."
    override fun youAskedGoFish(rank: String) = "asked for $rank → Go Fish!"
    override fun drawnCard(card: String) = "   ↳ Drawn: $card"
    override fun drawnCardHit(card: String) = "   ↳ Drawn: $card  ✓ Match — go again."
    override val deckEmpty = "   ↳ The deck is empty."
    override fun oppAskedGot(rank: String, n: Int) = "asked for $rank → takes $n card(s) from you."
    override fun oppAskedGoFish(rank: String, wentFishing: Boolean) =
        "asked for $rank → Go Fish!" + if (wentFishing) " Draws a card." else ""
    override fun oppDrawnHit(rank: String) = "   ↳ Drawn: $rank  ✓ Match — goes again."
    override fun oppAskedGotAt(target: String, rank: String, n: Int) = "asked $target for $rank → $n card(s)"
    override fun oppAskedGoFishAt(target: String, rank: String) = "asked $target for $rank → Go Fish!"
    override val youObject = "you"
    override fun playerLeftReshuffle(name: String) = "$name left — cards shuffled back into the deck."
    override val toastChooseCard = "Choose a card first."
    override val toastChooseOpponent = "Choose an opponent first."
    override fun youBook(rank: String) = "completed a book of ${rank}s!"
    override fun oppBook(rank: String) = "completed a book of ${rank}s!"
    override val gameOverLog = "🏁 Game over."
    override val toastNoSuchCard = "That card doesn't exist."
    override fun toastMustHold(rank: String) = "You must hold at least one $rank card!"
    override fun opponentLeft(opponent: String) = "$opponent left the game.\nThe session will end."
    override val connectionLostSession = "Lost connection to the other player.\nThe session will end."

    override val deckBadge = "🎴 Deck"
    override val yourBooks = "Your books"
    override fun handAndBooks(cards: Int, books: Int) = "🃏 $cards cards   📚 $books books"
    override val yourHand = "Your hand"
    override val scrollHint = "↕ scroll"
    override val noCardsLeft = "No cards left."
    override val logEmpty = "🌊 Nothing yet …"
    override val chooseCard = "Choose a card"
    override fun askFor(rank: String) = "Ask for $rank"
    override val turnYou = "⬆️  Your turn — pick a card!"
    override fun turnWaiting(opponent: String) = "⏳  Waiting for $opponent …"
    override fun turnGameOver(winner: String) = "🏆 Game over — $winner wins!"
    override val exitTitle = "Leave the game?"
    override val exitBody = "Are you sure you want to leave? This will end the session."
    override val leaveBtn = "Leave"
    override val stayBtn = "Stay"
    override val sessionEndedTitle = "🌊 Session ended"
    override val toLobbyBtn = "To lobby"
    override val winTitle = "Victory!"
    override val loseTitle = "Defeat"
    override val tieTitle = "Draw"
    override val winSubtitle = "You collected the most books!"
    override fun loseSubtitle(winner: String) = "$winner won the game."
    override val tieSubtitle = "It's a tie — both have the same number of books."
    override val toMainMenu = "To main menu"
    override val drawWord = "Draw"

    override val animDrawCard = "you draw a card"
    override val animDeckEmpty = "The deck is empty"
    override val animCaught = "caught!"
    override val animGaveAway = "handed over!"
    override val animBook = "📚  BOOK!"
    override fun animBookComplete(rank: String) = "$rank complete"

    // ── Tutorial ──
    override val tutorialBtn = "Tutorial"
    override val welcomeTitle = "Welcome to GoFish!"
    override val welcomeQuestion = "Do you know how to play?"
    override val welcomeYes = "Yes – let's get started"
    override val welcomePlay = "Play tutorial"
    override val tutorialHintText = "No problem! You can start the tutorial anytime from the settings menu."
    override val tutorialHintOk = "Got it"
    override val tutorialNext = "Next"
    override val tutorialLeave = "Leave"
    override val tutorialPickCard = "Tap the highlighted card."
    override val tutorialPressAsk = "Tap “Ask” to ask."
    override val tutorialIntro = "Hi! Let me show you how GoFish works. Goal: collect books – four cards of the same value. Whoever has the most books at the end wins."
    override val tutorialAskIntro = "It's your turn. Ask your opponent for a value you hold yourself. Let's start with the 7s."
    override val tutorialGoFishIntro = "Now ask for a value your opponent probably doesn't have – the Queens."
    override val tutorialOppTurn = "Now it's Fisherman's turn. He asks you for Kings and takes them from you. That's how you take turns."
    override val tutorialFinish = "You've learned everything. Now beat your opponent!"
    override fun tutorialStole1(rank: String) = "Fisherman had one ${rank} – you take it! Because you got a card, you go again."
    override fun tutorialStole2(rank: String) = "Nice! Fisherman had two ${rank}. Both come to your hand – and you stay on turn."
    override fun tutorialStole3(rank: String) = "Wow, three ${rank} at once! Now you have four of them …"
    override fun tutorialBook(rank: String) = "… and four matching cards make a book! Your book of ${rank}s is set aside. That's the whole point."
    override fun tutorialGoFish(rank: String) = "“Go Fish!” Fisherman had no ${rank}, so you draw a card from the deck. If it doesn't match, your turn ends."
}

// ════════════════════════════════════════════════════════════════════════════
//  FRANÇAIS
// ════════════════════════════════════════════════════════════════════════════

object FrTexts : Texts {
    override val tagline = "Attrape ta prise !"
    override val findGame = "Trouve une partie !"
    override val hostLocal = "Héberger en local"

    override val yourName = "Ton nom"
    override val namePlaceholder = "p. ex. Capitaine Nemo"
    override fun hello(name: String) = "Salut $name !"
    override val yourAvatar = "Ton avatar"
    override fun avatarSummary(kind: String, color: String) = "$kind · $color"

    override val chooseAvatarTitle = "Choisis un avatar"
    override val chooseAvatarSubtitle = "Choisis une créature marine."
    override val colorLabel = "Couleur"
    override val doneBtn = "Terminé"
    override fun kindName(kind: AvatarKind) = when (kind) {
        AvatarKind.FISH -> "Poisson"
        AvatarKind.SHARK -> "Requin"
        AvatarKind.WHALE -> "Baleine"
        AvatarKind.DOLPHIN -> "Dauphin"
        AvatarKind.PUFFER -> "Poisson-globe"
        AvatarKind.STARFISH -> "Étoile de mer"
        AvatarKind.MOBY -> "Moby Dick"
        AvatarKind.NESSIE -> "Nessie"
        AvatarKind.BLOBFISH -> "Blobfish"
        AvatarKind.KOI -> "Carpe koï"
        AvatarKind.BACKFISCH -> "Poisson frit"
        AvatarKind.MEGALODON -> "Mégalodon"
    }
    override fun colorName(color: AvatarColor) = when (color) {
        AvatarColor.SUN -> "Jaune soleil"
        AvatarColor.CORAL -> "Corail"
        AvatarColor.SEAFOAM -> "Écume"
        AvatarColor.LAVENDER -> "Lavande"
        AvatarColor.OCEAN -> "Bleu océan"
        AvatarColor.SANDY -> "Sable"
    }

    override val mainMenu = "Menu principal"
    override val backBtn = "Retour"
    override val settingsTitle = "Paramètres"
    override val publishedBy = "Éditeur"
    override val soundLabel = "Son"
    override val musicLabel = "Musique"
    override val opponentsLabel = "Adversaires"
    override fun lobbyWaiting(joined: Int, expected: Int) = "En attente de joueurs … ($joined/$expected)"
    override val errOnlineMultiUnavailable = "Le multijoueur en ligne n'est pas encore disponible."

    override val localTitle = "Jeu local"
    override val localSubtitle = "Héberge ou rejoins sur le même Wi-Fi"
    override val hostGame = "Héberger une partie"
    override fun yourIp(ip: String) = "Ton IP : $ip"
    override val startServer = "Démarrer le serveur"
    override val joinGame = "Rejoindre une partie"
    override val hostIpPlaceholder = "IP de l'hôte (p. ex. 192.168.0.42)"
    override val enterHostIp = "Saisis l'IP de l'hôte"
    override val joinBtn = "Rejoindre"

    override val hostingDialogTitle = "Héberger une partie"
    override val shareIp = "Partage cette IP avec ton adversaire :"
    override val waitingForCoPlayer = "En attente d'un autre joueur …"
    override val cancelHosting = "Annuler l'hébergement"
    override val cancelConnection = "Annuler la connexion"

    override val findGameTitle = "Trouver une partie"
    override val anyGame = "Partie quelconque"
    override val orPickCount = "… ou choisis le nombre d'adversaires"
    override val adFreeBtn = "Jouer sans pub"
    override val adFreeActive = "✅ Sans pub"
    override val watchAdUnlock = "Regarder la vidéo"
    override val buyUnlock = "Acheter"
    override val privacyOptionsBtn = "Options de confidentialité"
    override val onlineSubtitle = "Se connecter automatiquement à un adversaire"
    override val searchingOpponent = "Recherche d'un adversaire …"
    override val waitingForPlayer = "En attente d'un autre joueur en recherche."
    override val cancelSearch = "Annuler la recherche"
    override val serverNotConfiguredTitle = "Serveur en ligne pas encore configuré"
    override val serverNotConfiguredBody =
        "Pour que les joueurs se trouvent automatiquement sur internet, un serveur " +
            "relais doit être actif. Saisis son adresse ici."
    override val setServer = "Configurer le serveur"
    override val changeServer = "Changer de serveur"
    override val opponentFound = "Adversaire trouvé ! Démarrage …"

    override val serverAddressTitle = "Adresse du serveur"
    override val serverDialogBody =
        "Utile uniquement pour les tests ou ton propre serveur. Laisser vide = serveur par défaut de l'app."
    override fun defaultServerLabel(url: String) = "Par défaut : $url"
    override val applyBtn = "Appliquer"
    override val useDefaultBtn = "Utiliser par défaut"

    override val startingServer = "Démarrage du serveur …"
    override fun connectingTo(ip: String) = "Connexion à $ip …"
    override val hostingCancelled = "Hébergement annulé."
    override val searchCancelled = "Recherche annulée."
    override val operationCancelled = "Opération annulée."
    override val connectedWaiting = "Connecté ! En attente du début de la partie …"
    override val serverNotSetUp = "Le serveur en ligne n'est pas encore configuré."
    override val defaultNamePlayer = "Joueur"
    override val defaultNameHost = "Hôte"
    override val defaultNameGuest = "Invité"

    override val errHostingEnded = "Hébergement terminé."
    override val errConnectionLost = "La connexion a été perdue."
    override val errServerConnLost = "Connexion au serveur perdue."
    override val errInvalidServer = "Adresse du serveur invalide ou injoignable."
    override val errServerUnreachableRelay = "Serveur injoignable — le serveur relais est-il actif ?"
    override val errServerUnreachableIp = "Serveur injoignable — l'IP est-elle correcte ?"
    override val errTimeout = "Délai dépassé — le serveur ne répond pas."
    override val errNoNetwork = "Aucun réseau disponible."
    override val errNoRoute = "Serveur injoignable à cette IP."
    override val errPermission = "Accès refusé. L'app a-t-elle les droits réseau ?"
    override fun errGeneric(raw: String) = "Erreur : $raw"
    override val unknownIp = "Inconnue"

    override val you = "Toi"
    override val opponentDefault = "Adversaire"
    override fun gameStartedAs(name: String) = "🎬 Partie lancée — tu joues en tant que $name"
    override fun youAskedGot(opponent: String, rank: String, n: Int) =
        "as demandé $rank → $opponent te donne $n carte(s) — rejoue."
    override fun youAskedGoFish(rank: String) = "as demandé $rank → Go Fish !"
    override fun drawnCard(card: String) = "   ↳ Pioché : $card"
    override fun drawnCardHit(card: String) = "   ↳ Pioché : $card  ✓ Réussi — rejoue."
    override val deckEmpty = "   ↳ La pioche est vide."
    override fun oppAskedGot(rank: String, n: Int) = "a demandé $rank → te prend $n carte(s)."
    override fun oppAskedGoFish(rank: String, wentFishing: Boolean) =
        "a demandé $rank → Go Fish !" + if (wentFishing) " Pioche une carte." else ""
    override fun oppDrawnHit(rank: String) = "   ↳ Pioché : $rank  ✓ Réussi — rejoue."
    override fun oppAskedGotAt(target: String, rank: String, n: Int) = "a demandé $rank à $target → $n carte(s)"
    override fun oppAskedGoFishAt(target: String, rank: String) = "a demandé $rank à $target → Go Fish !"
    override val youObject = "toi"
    override fun playerLeftReshuffle(name: String) = "$name est parti — cartes remises dans la pioche."
    override val toastChooseCard = "Choisis d'abord une carte."
    override val toastChooseOpponent = "Choisis d'abord un adversaire."
    override fun youBook(rank: String) = "as complété un carré de $rank !"
    override fun oppBook(rank: String) = "a complété un carré de $rank !"
    override val gameOverLog = "🏁 Partie terminée."
    override val toastNoSuchCard = "Cette carte n'existe pas."
    override fun toastMustHold(rank: String) = "Tu dois avoir au moins une carte $rank !"
    override fun opponentLeft(opponent: String) = "$opponent a quitté la partie.\nLa session va se terminer."
    override val connectionLostSession = "Connexion avec l'autre joueur perdue.\nLa session va se terminer."

    override val deckBadge = "🎴 Pioche"
    override val yourBooks = "Tes carrés"
    override fun handAndBooks(cards: Int, books: Int) = "🃏 $cards cartes   📚 $books carrés"
    override val yourHand = "Ta main"
    override val scrollHint = "↕ défiler"
    override val noCardsLeft = "Plus de cartes."
    override val logEmpty = "🌊 Rien encore …"
    override val chooseCard = "Choisis une carte"
    override fun askFor(rank: String) = "Demander $rank"
    override val turnYou = "⬆️  À toi de jouer — choisis ta carte !"
    override fun turnWaiting(opponent: String) = "⏳  En attente de $opponent …"
    override fun turnGameOver(winner: String) = "🏆 Partie terminée — $winner gagne !"
    override val exitTitle = "Quitter la partie ?"
    override val exitBody = "Tu es sûr de vouloir quitter ? Cela mettra fin à la session."
    override val leaveBtn = "Quitter"
    override val stayBtn = "Rester"
    override val sessionEndedTitle = "🌊 Session terminée"
    override val toLobbyBtn = "Au salon"
    override val winTitle = "Victoire !"
    override val loseTitle = "Défaite"
    override val tieTitle = "Égalité"
    override val winSubtitle = "Tu as réuni le plus de carrés !"
    override fun loseSubtitle(winner: String) = "$winner a gagné la partie."
    override val tieSubtitle = "Égalité — vous avez le même nombre de carrés."
    override val toMainMenu = "Au menu principal"
    override val drawWord = "Égalité"

    override val animDrawCard = "tu pioches une carte"
    override val animDeckEmpty = "La pioche est vide"
    override val animCaught = "pêché !"
    override val animGaveAway = "données !"
    override val animBook = "📚  CARRÉ !"
    override fun animBookComplete(rank: String) = "$rank complété"

    // ── Tutorial ──
    override val tutorialBtn = "Tutoriel"
    override val welcomeTitle = "Bienvenue dans GoFish !"
    override val welcomeQuestion = "Sais-tu comment jouer ?"
    override val welcomeYes = "Oui – c'est parti"
    override val welcomePlay = "Jouer le tutoriel"
    override val tutorialHintText = "Pas de souci ! Tu peux lancer le tutoriel à tout moment depuis les réglages."
    override val tutorialHintOk = "Compris"
    override val tutorialNext = "Suivant"
    override val tutorialLeave = "Quitter"
    override val tutorialPickCard = "Touche la carte en surbrillance."
    override val tutorialPressAsk = "Touche « Demander » pour demander."
    override val tutorialIntro = "Salut ! Je te montre comment marche GoFish. But : réunir des carrés – quatre cartes de même valeur. Celui qui a le plus de carrés à la fin gagne."
    override val tutorialAskIntro = "C'est ton tour. Demande à ton adversaire une valeur que tu as toi-même. Commençons par les 7."
    override val tutorialGoFishIntro = "Demande maintenant une valeur que ton adversaire n'a sûrement pas – les dames (Q)."
    override val tutorialOppTurn = "C'est au tour de Fisherman. Il te demande des rois (K) et te les prend. C'est ainsi qu'on joue chacun son tour."
    override val tutorialFinish = "Tu as tout appris. À toi de battre ton adversaire !"
    override fun tutorialStole1(rank: String) = "Fisherman avait un ${rank} – tu le prends ! Comme tu as reçu une carte, tu rejoues."
    override fun tutorialStole2(rank: String) = "Bien joué ! Fisherman avait deux ${rank}. Les deux rejoignent ta main et tu gardes la main."
    override fun tutorialStole3(rank: String) = "Waouh, trois ${rank} d'un coup ! Tu en as maintenant quatre …"
    override fun tutorialBook(rank: String) = "… et quatre cartes identiques forment un carré ! Ton carré de ${rank} est mis de côté. C'est tout l'objectif."
    override fun tutorialGoFish(rank: String) = "« Go Fish ! » Fisherman n'avait aucun ${rank}, tu pioches donc une carte. Si elle ne correspond pas, ton tour se termine."
}

// ════════════════════════════════════════════════════════════════════════════
//  中文（普通话 / 简体）
// ════════════════════════════════════════════════════════════════════════════

object ZhTexts : Texts {
    override val tagline = "钓起你的渔获！"
    override val findGame = "寻找对局！"
    override val hostLocal = "本地创建"

    override val yourName = "你的名字"
    override val namePlaceholder = "例如 尼莫船长"
    override fun hello(name: String) = "你好，$name！"
    override val yourAvatar = "你的头像"
    override fun avatarSummary(kind: String, color: String) = "$kind · $color"

    override val chooseAvatarTitle = "选择头像"
    override val chooseAvatarSubtitle = "选一种海洋生物。"
    override val colorLabel = "颜色"
    override val doneBtn = "完成"
    override fun kindName(kind: AvatarKind) = when (kind) {
        AvatarKind.FISH -> "鱼"
        AvatarKind.SHARK -> "鲨鱼"
        AvatarKind.WHALE -> "鲸鱼"
        AvatarKind.DOLPHIN -> "海豚"
        AvatarKind.PUFFER -> "河豚"
        AvatarKind.STARFISH -> "海星"
        AvatarKind.MOBY -> "莫比·迪克"
        AvatarKind.NESSIE -> "尼斯湖水怪"
        AvatarKind.BLOBFISH -> "水滴鱼"
        AvatarKind.KOI -> "锦鲤"
        AvatarKind.BACKFISCH -> "炸鱼"
        AvatarKind.MEGALODON -> "巨齿鲨"
    }
    override fun colorName(color: AvatarColor) = when (color) {
        AvatarColor.SUN -> "阳光黄"
        AvatarColor.CORAL -> "珊瑚色"
        AvatarColor.SEAFOAM -> "海沫绿"
        AvatarColor.LAVENDER -> "薰衣草紫"
        AvatarColor.OCEAN -> "海洋蓝"
        AvatarColor.SANDY -> "沙滩色"
    }

    override val mainMenu = "主菜单"
    override val backBtn = "返回"
    override val settingsTitle = "设置"
    override val publishedBy = "发行方"
    override val soundLabel = "音效"
    override val musicLabel = "音乐"
    override val opponentsLabel = "对手"
    override fun lobbyWaiting(joined: Int, expected: Int) = "等待玩家… ($joined/$expected)"
    override val errOnlineMultiUnavailable = "在线多人游戏暂未开放。"

    override val localTitle = "本地游戏"
    override val localSubtitle = "在同一 Wi-Fi 下创建或加入"
    override val hostGame = "创建游戏"
    override fun yourIp(ip: String) = "你的 IP：$ip"
    override val startServer = "启动服务器"
    override val joinGame = "加入游戏"
    override val hostIpPlaceholder = "主机 IP（例如 192.168.0.42）"
    override val enterHostIp = "请输入主机 IP"
    override val joinBtn = "加入"

    override val hostingDialogTitle = "创建游戏"
    override val shareIp = "把这个 IP 分享给你的对手："
    override val waitingForCoPlayer = "等待其他玩家 …"
    override val cancelHosting = "取消创建"
    override val cancelConnection = "取消连接"

    override val findGameTitle = "寻找对局"
    override val anyGame = "任意对局"
    override val orPickCount = "…或选择对手数量"
    override val adFreeBtn = "无广告畅玩"
    override val adFreeActive = "✅ 无广告"
    override val watchAdUnlock = "观看视频"
    override val buyUnlock = "购买"
    override val privacyOptionsBtn = "隐私选项"
    override val onlineSubtitle = "自动与对手连接"
    override val searchingOpponent = "正在寻找对手 …"
    override val waitingForPlayer = "正在等待另一位也在寻找的玩家。"
    override val cancelSearch = "取消搜索"
    override val serverNotConfiguredTitle = "在线服务器尚未设置"
    override val serverNotConfiguredBody =
        "为了让玩家通过互联网自动匹配，必须有一台中继服务器在运行。请在此输入它的地址。"
    override val setServer = "设置服务器"
    override val changeServer = "更改服务器"
    override val opponentFound = "找到对手！正在开始 …"

    override val serverAddressTitle = "服务器地址"
    override val serverDialogBody =
        "仅在测试或使用自有服务器时需要。留空 = 应用的默认服务器。"
    override fun defaultServerLabel(url: String) = "默认：$url"
    override val applyBtn = "应用"
    override val useDefaultBtn = "使用默认"

    override val startingServer = "正在启动服务器 …"
    override fun connectingTo(ip: String) = "正在连接 $ip …"
    override val hostingCancelled = "已取消创建。"
    override val searchCancelled = "已取消搜索。"
    override val operationCancelled = "操作已取消。"
    override val connectedWaiting = "已连接！等待游戏开始 …"
    override val serverNotSetUp = "在线服务器尚未设置。"
    override val defaultNamePlayer = "玩家"
    override val defaultNameHost = "主机"
    override val defaultNameGuest = "访客"

    override val errHostingEnded = "创建已结束。"
    override val errConnectionLost = "连接已断开。"
    override val errServerConnLost = "与服务器的连接已断开。"
    override val errInvalidServer = "服务器地址无效或无法访问。"
    override val errServerUnreachableRelay = "无法访问服务器 — 中继服务器在运行吗？"
    override val errServerUnreachableIp = "无法访问服务器 — IP 输入正确吗？"
    override val errTimeout = "超时 — 服务器没有响应。"
    override val errNoNetwork = "没有可用的网络。"
    override val errNoRoute = "在该 IP 上无法访问服务器。"
    override val errPermission = "访问被拒绝。应用有网络权限吗？"
    override fun errGeneric(raw: String) = "错误：$raw"
    override val unknownIp = "未知"

    override val you = "你"
    override val opponentDefault = "对手"
    override fun gameStartedAs(name: String) = "🎬 游戏开始 — 你扮演 $name"
    override fun youAskedGot(opponent: String, rank: String, n: Int) =
        "要了 $rank → $opponent 给你 $n 张牌 — 再来一次。"
    override fun youAskedGoFish(rank: String) = "要了 $rank → Go Fish！"
    override fun drawnCard(card: String) = "   ↳ 抽到：$card"
    override fun drawnCardHit(card: String) = "   ↳ 抽到：$card  ✓ 命中 — 再来一次。"
    override val deckEmpty = "   ↳ 牌堆已空。"
    override fun oppAskedGot(rank: String, n: Int) = "要了 $rank → 从你这里拿走 $n 张牌。"
    override fun oppAskedGoFish(rank: String, wentFishing: Boolean) =
        "要了 $rank → Go Fish！" + if (wentFishing) " 抽一张牌。" else ""
    override fun oppDrawnHit(rank: String) = "   ↳ 抽到：$rank  ✓ 命中 — 再来一次。"
    override fun oppAskedGotAt(target: String, rank: String, n: Int) = "向 $target 要 $rank → $n 张牌"
    override fun oppAskedGoFishAt(target: String, rank: String) = "向 $target 要 $rank → Go Fish！"
    override val youObject = "你"
    override fun playerLeftReshuffle(name: String) = "$name 离开了 — 牌已洗回牌堆。"
    override val toastChooseCard = "请先选一张牌。"
    override val toastChooseOpponent = "请先选一个对手。"
    override fun youBook(rank: String) = "凑齐了一组 $rank！"
    override fun oppBook(rank: String) = "凑齐了一组 $rank！"
    override val gameOverLog = "🏁 游戏结束。"
    override val toastNoSuchCard = "没有这张牌。"
    override fun toastMustHold(rank: String) = "你至少要持有一张 $rank 牌！"
    override fun opponentLeft(opponent: String) = "$opponent 离开了游戏。\n本局将结束。"
    override val connectionLostSession = "与对方玩家的连接已断开。\n本局将结束。"

    override val deckBadge = "🎴 牌堆"
    override val yourBooks = "你的牌组"
    override fun handAndBooks(cards: Int, books: Int) = "🃏 $cards 张牌   📚 $books 组"
    override val yourHand = "你的手牌"
    override val scrollHint = "↕ 滚动"
    override val noCardsLeft = "没有牌了。"
    override val logEmpty = "🌊 还什么都没发生 …"
    override val chooseCard = "选择一张牌"
    override fun askFor(rank: String) = "要 $rank"
    override val turnYou = "⬆️  轮到你了 — 选一张牌！"
    override fun turnWaiting(opponent: String) = "⏳  等待 $opponent …"
    override fun turnGameOver(winner: String) = "🏆 游戏结束 — $winner 获胜！"
    override val exitTitle = "离开游戏？"
    override val exitBody = "确定要离开吗？这会结束本局。"
    override val leaveBtn = "离开"
    override val stayBtn = "留下"
    override val sessionEndedTitle = "🌊 对局已结束"
    override val toLobbyBtn = "返回大厅"
    override val winTitle = "胜利！"
    override val loseTitle = "失败"
    override val tieTitle = "平局"
    override val winSubtitle = "你收集了最多的牌组！"
    override fun loseSubtitle(winner: String) = "$winner 赢得了游戏。"
    override val tieSubtitle = "平局 — 双方的牌组数量相同。"
    override val toMainMenu = "返回主菜单"
    override val drawWord = "平局"

    override val animDrawCard = "你抽一张牌"
    override val animDeckEmpty = "牌堆已空"
    override val animCaught = "钓到了！"
    override val animGaveAway = "被拿走！"
    override val animBook = "📚  成组！"
    override fun animBookComplete(rank: String) = "$rank 凑齐"

    // ── Tutorial ──
    override val tutorialBtn = "教程"
    override val welcomeTitle = "欢迎来到 GoFish！"
    override val welcomeQuestion = "你知道怎么玩吗？"
    override val welcomeYes = "知道 – 开始吧"
    override val welcomePlay = "玩教程"
    override val tutorialHintText = "没问题！你随时可以在设置里启动教程。"
    override val tutorialHintOk = "知道了"
    override val tutorialNext = "下一步"
    override val tutorialLeave = "退出"
    override val tutorialPickCard = "点击高亮的牌。"
    override val tutorialPressAsk = "点击“询问”来要牌。"
    override val tutorialIntro = "你好！我来教你玩 GoFish。目标：收集“组”——四张相同点数的牌。最后拥有最多组的人获胜。"
    override val tutorialAskIntro = "轮到你了。向对手要一个你自己手上也有的点数。先从 7 开始。"
    override val tutorialGoFishIntro = "现在要一个对手很可能没有的点数——Q。"
    override val tutorialOppTurn = "现在轮到 Fisherman。他向你要 K，并把它们拿走。你们就这样轮流出牌。"
    override val tutorialFinish = "你已经全部学会了。去打败你的对手吧！"
    override fun tutorialStole1(rank: String) = "Fisherman 有一张 ${rank}——归你了！因为你拿到了牌，可以再来一次。"
    override fun tutorialStole2(rank: String) = "漂亮！Fisherman 有两张 ${rank}，都到你手上了，你继续出牌。"
    override fun tutorialStole3(rank: String) = "哇，一次三张 ${rank}！你现在有四张了……"
    override fun tutorialBook(rank: String) = "……四张相同的牌就组成一组！你的 ${rank} 组被放到一边。这正是目标。"
    override fun tutorialGoFish(rank: String) = "“Go Fish！”Fisherman 没有 ${rank}，于是你从牌堆摸一张。如果不匹配，你的回合就结束。"
}

// ════════════════════════════════════════════════════════════════════════════
//  TAGALOG
// ════════════════════════════════════════════════════════════════════════════

object TlTexts : Texts {
    override val tagline = "Hilahin ang iyong huli!"
    override val findGame = "Maghanap ng laro!"
    override val hostLocal = "Mag-host nang lokal"

    override val yourName = "Pangalan mo"
    override val namePlaceholder = "hal. Kapitan Nemo"
    override fun hello(name: String) = "Kumusta, $name!"
    override val yourAvatar = "Avatar mo"
    override fun avatarSummary(kind: String, color: String) = "$kind · $color"

    override val chooseAvatarTitle = "Pumili ng avatar"
    override val chooseAvatarSubtitle = "Pumili ng nilalang sa dagat."
    override val colorLabel = "Kulay"
    override val doneBtn = "Tapos"
    override fun kindName(kind: AvatarKind) = when (kind) {
        AvatarKind.FISH -> "Isda"
        AvatarKind.SHARK -> "Pating"
        AvatarKind.WHALE -> "Balyena"
        AvatarKind.DOLPHIN -> "Dolphin"
        AvatarKind.PUFFER -> "Butete"
        AvatarKind.STARFISH -> "Bituing-dagat"
        AvatarKind.MOBY -> "Moby Dick"
        AvatarKind.NESSIE -> "Nessie"
        AvatarKind.BLOBFISH -> "Blobfish"
        AvatarKind.KOI -> "Koi"
        AvatarKind.BACKFISCH -> "Pritong isda"
        AvatarKind.MEGALODON -> "Megalodon"
    }
    override fun colorName(color: AvatarColor) = when (color) {
        AvatarColor.SUN -> "Dilaw na araw"
        AvatarColor.CORAL -> "Koral"
        AvatarColor.SEAFOAM -> "Bula ng dagat"
        AvatarColor.LAVENDER -> "Lavender"
        AvatarColor.OCEAN -> "Asul na karagatan"
        AvatarColor.SANDY -> "Buhangin"
    }

    override val mainMenu = "Pangunahing menu"
    override val backBtn = "Bumalik"
    override val settingsTitle = "Mga setting"
    override val publishedBy = "Naglathala"
    override val soundLabel = "Tunog"
    override val musicLabel = "Musika"
    override val opponentsLabel = "Mga kalaban"
    override fun lobbyWaiting(joined: Int, expected: Int) = "Naghihintay ng mga manlalaro … ($joined/$expected)"
    override val errOnlineMultiUnavailable = "Hindi pa available ang online multiplayer."

    override val localTitle = "Lokal na laro"
    override val localSubtitle = "Mag-host o sumali sa parehong Wi-Fi"
    override val hostGame = "Mag-host ng laro"
    override fun yourIp(ip: String) = "IP mo: $ip"
    override val startServer = "Simulan ang server"
    override val joinGame = "Sumali sa laro"
    override val hostIpPlaceholder = "IP ng host (hal. 192.168.0.42)"
    override val enterHostIp = "Pakilagay ang IP ng host"
    override val joinBtn = "Sumali"

    override val hostingDialogTitle = "Mag-host ng laro"
    override val shareIp = "Ibahagi ang IP na ito sa kalaban mo:"
    override val waitingForCoPlayer = "Naghihintay ng ibang manlalaro …"
    override val cancelHosting = "Kanselahin ang pag-host"
    override val cancelConnection = "Kanselahin ang koneksyon"

    override val findGameTitle = "Maghanap ng laro"
    override val anyGame = "Kahit anong laro"
    override val orPickCount = "… o pumili ng bilang ng kalaban"
    override val adFreeBtn = "Maglaro nang walang ads"
    override val adFreeActive = "✅ Walang ads"
    override val watchAdUnlock = "Manood ng video"
    override val buyUnlock = "Bumili"
    override val privacyOptionsBtn = "Mga opsyon sa privacy"
    override val onlineSubtitle = "Awtomatikong kumonekta sa isang kalaban"
    override val searchingOpponent = "Naghahanap ng kalaban …"
    override val waitingForPlayer = "Naghihintay ng ibang manlalarong naghahanap din."
    override val cancelSearch = "Kanselahin ang paghahanap"
    override val serverNotConfiguredTitle = "Hindi pa naka-set up ang online server"
    override val serverNotConfiguredBody =
        "Para awtomatikong magkahanapan ang mga manlalaro sa internet, kailangang may " +
            "tumatakbong relay server. Ilagay ang address nito rito."
    override val setServer = "Itakda ang server"
    override val changeServer = "Baguhin ang server"
    override val opponentFound = "May nahanap na kalaban! Sinisimulan na …"

    override val serverAddressTitle = "Address ng server"
    override val serverDialogBody =
        "Kailangan lang para sa pagsubok o sa sarili mong server. Iwang blangko = default na server ng app."
    override fun defaultServerLabel(url: String) = "Default: $url"
    override val applyBtn = "Ilapat"
    override val useDefaultBtn = "Gamitin ang default"

    override val startingServer = "Sinisimulan ang server …"
    override fun connectingTo(ip: String) = "Kumokonekta sa $ip …"
    override val hostingCancelled = "Kinansela ang pag-host."
    override val searchCancelled = "Kinansela ang paghahanap."
    override val operationCancelled = "Kinansela ang operasyon."
    override val connectedWaiting = "Nakakonekta na! Naghihintay magsimula ang laro …"
    override val serverNotSetUp = "Hindi pa naka-set up ang online server."
    override val defaultNamePlayer = "Manlalaro"
    override val defaultNameHost = "Host"
    override val defaultNameGuest = "Bisita"

    override val errHostingEnded = "Tapos na ang pag-host."
    override val errConnectionLost = "Nawala ang koneksyon."
    override val errServerConnLost = "Nawala ang koneksyon sa server."
    override val errInvalidServer = "Di-wasto o di-maabot ang address ng server."
    override val errServerUnreachableRelay = "Di-maabot ang server — tumatakbo ba ang relay server?"
    override val errServerUnreachableIp = "Di-maabot ang server — tama ba ang IP?"
    override val errTimeout = "Nag-time out — hindi tumutugon ang server."
    override val errNoNetwork = "Walang available na network."
    override val errNoRoute = "Di-maabot ang server sa IP na ito."
    override val errPermission = "Tinanggihan ang access. May permiso ba ang app sa network?"
    override fun errGeneric(raw: String) = "Error: $raw"
    override val unknownIp = "Hindi alam"

    override val you = "Ikaw"
    override val opponentDefault = "Kalaban"
    override fun gameStartedAs(name: String) = "🎬 Nagsimula ang laro — naglalaro ka bilang $name"
    override fun youAskedGot(opponent: String, rank: String, n: Int) =
        "humingi ng $rank → binigyan ka ni $opponent ng $n kard — ulit ka."
    override fun youAskedGoFish(rank: String) = "humingi ng $rank → Go Fish!"
    override fun drawnCard(card: String) = "   ↳ Kinuha: $card"
    override fun drawnCardHit(card: String) = "   ↳ Kinuha: $card  ✓ Tama — ulit ka."
    override val deckEmpty = "   ↳ Wala nang laman ang baraha."
    override fun oppAskedGot(rank: String, n: Int) = "humingi ng $rank → kinuha ang $n kard mo."
    override fun oppAskedGoFish(rank: String, wentFishing: Boolean) =
        "humingi ng $rank → Go Fish!" + if (wentFishing) " Kumuha ng isang kard." else ""
    override fun oppDrawnHit(rank: String) = "   ↳ Kinuha: $rank  ✓ Tama — ulit siya."
    override fun oppAskedGotAt(target: String, rank: String, n: Int) = "humingi ng $rank kay $target → $n kard"
    override fun oppAskedGoFishAt(target: String, rank: String) = "humingi ng $rank kay $target → Go Fish!"
    override val youObject = "iyo"
    override fun playerLeftReshuffle(name: String) = "Umalis si $name — ibinalik ang mga kard sa baraha."
    override val toastChooseCard = "Pumili muna ng kard."
    override val toastChooseOpponent = "Pumili muna ng kalaban."
    override fun youBook(rank: String) = "nakakumpleto ng set ng $rank!"
    override fun oppBook(rank: String) = "nakakumpleto ng set ng $rank!"
    override val gameOverLog = "🏁 Tapos na ang laro."
    override val toastNoSuchCard = "Walang ganoong kard."
    override fun toastMustHold(rank: String) = "Dapat mayroon kang kahit isang $rank na kard!"
    override fun opponentLeft(opponent: String) = "Umalis si $opponent sa laro.\nMagtatapos na ang session."
    override val connectionLostSession = "Nawala ang koneksyon sa kalaban.\nMagtatapos na ang session."

    override val deckBadge = "🎴 Baraha"
    override val yourBooks = "Mga set mo"
    override fun handAndBooks(cards: Int, books: Int) = "🃏 $cards kard   📚 $books set"
    override val yourHand = "Kamay mo"
    override val scrollHint = "↕ mag-scroll"
    override val noCardsLeft = "Wala nang kard."
    override val logEmpty = "🌊 Wala pa …"
    override val chooseCard = "Pumili ng kard"
    override fun askFor(rank: String) = "Humingi ng $rank"
    override val turnYou = "⬆️  Ikaw na — pumili ng kard!"
    override fun turnWaiting(opponent: String) = "⏳  Naghihintay kay $opponent …"
    override fun turnGameOver(winner: String) = "🏆 Tapos na ang laro — nanalo si $winner!"
    override val exitTitle = "Aalis sa laro?"
    override val exitBody = "Sigurado ka bang aalis? Magtatapos nito ang session."
    override val leaveBtn = "Umalis"
    override val stayBtn = "Manatili"
    override val sessionEndedTitle = "🌊 Tapos na ang session"
    override val toLobbyBtn = "Sa lobby"
    override val winTitle = "Panalo!"
    override val loseTitle = "Talo"
    override val tieTitle = "Tabla"
    override val winSubtitle = "Nakakuha ka ng pinakamaraming set!"
    override fun loseSubtitle(winner: String) = "Nanalo si $winner sa laro."
    override val tieSubtitle = "Tabla — pareho ang dami ng set ninyo."
    override val toMainMenu = "Sa pangunahing menu"
    override val drawWord = "Tabla"

    override val animDrawCard = "kumuha ka ng kard"
    override val animDeckEmpty = "Wala nang laman ang baraha"
    override val animCaught = "nahuli!"
    override val animGaveAway = "naibigay!"
    override val animBook = "📚  SET!"
    override fun animBookComplete(rank: String) = "Kumpleto ang $rank"

    // ── Tutorial ──
    override val tutorialBtn = "Tutorial"
    override val welcomeTitle = "Maligayang pagdating sa GoFish!"
    override val welcomeQuestion = "Alam mo ba kung paano maglaro?"
    override val welcomeYes = "Oo – tara na"
    override val welcomePlay = "Laruin ang tutorial"
    override val tutorialHintText = "Walang problema! Puwede mong simulan ang tutorial anumang oras mula sa mga setting."
    override val tutorialHintOk = "Sige"
    override val tutorialNext = "Susunod"
    override val tutorialLeave = "Umalis"
    override val tutorialPickCard = "I-tap ang naka-highlight na baraha."
    override val tutorialPressAsk = "I-tap ang “Magtanong” para magtanong."
    override val tutorialIntro = "Kumusta! Ituturo ko kung paano laruin ang GoFish. Layunin: mangolekta ng mga set – apat na barahang magkapareho ng halaga. Mananalo ang may pinakamaraming set sa dulo."
    override val tutorialAskIntro = "Ikaw na. Hingin sa kalaban ang halaga na hawak mo rin. Magsimula tayo sa mga 7."
    override val tutorialGoFishIntro = "Ngayon, humingi ng halagang malamang na wala sa kalaban – ang mga reyna (Q)."
    override val tutorialOppTurn = "Turn na ni Fisherman. Hihingin niya sa iyo ang mga hari (K) at kukunin ang mga ito. Ganito kayo naghahalinhinan."
    override val tutorialFinish = "Natutunan mo na ang lahat. Talunin mo na ang kalaban mo!"
    override fun tutorialStole1(rank: String) = "May isang ${rank} si Fisherman – sa iyo na! Dahil nakakuha ka ng baraha, ikaw ulit."
    override fun tutorialStole2(rank: String) = "Galing! May dalawang ${rank} si Fisherman. Pareho silang mapupunta sa iyo at tuloy ang turn mo."
    override fun tutorialStole3(rank: String) = "Wow, tatlong ${rank} agad! Mayroon ka nang apat …"
    override fun tutorialBook(rank: String) = "… at ang apat na magkaparehong baraha ay isang set! Ang set mo ng ${rank} ay isinasantabi. Iyan ang layunin."
    override fun tutorialGoFish(rank: String) = "“Go Fish!” Walang ${rank} si Fisherman, kaya kumuha ka ng baraha sa deck. Kung hindi tugma, tapos na ang turn mo."
}
