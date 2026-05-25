package com.jbastudio.gofish.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import com.jbastudio.gofish.ui.components.AvatarColor
import com.jbastudio.gofish.ui.components.AvatarKind

/** Verfügbare Sprachen. Standard ist Deutsch. */
enum class Language(val tag: String) { DE("de"), ES("es"), EN("en") }

fun languageFromTag(tag: String?): Language =
    Language.entries.firstOrNull { it.tag == tag } ?: Language.DE

/** Liefert die Texte für eine Sprache. */
fun textsFor(language: Language): Texts = when (language) {
    Language.DE -> DeTexts
    Language.ES -> EsTexts
    Language.EN -> EnTexts
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
    val animBook: String
    fun animBookComplete(rank: String): String
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
    override val animBook = "📚  BUCH!"
    override fun animBookComplete(rank: String) = "${rank}er komplett"
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
    override val animBook = "📚  ¡CUARTETO!"
    override fun animBookComplete(rank: String) = "$rank completo"
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
    override val animBook = "📚  BOOK!"
    override fun animBookComplete(rank: String) = "$rank complete"
}
