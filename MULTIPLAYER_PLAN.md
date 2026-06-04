# Mehrspieler-Plan: 2–4 Spieler (Go Fish)

Status: **final, in Umsetzung.** Reihenfolge: lokal zuerst, dann Online.
Da hier nicht kompiliert werden kann, folgt je Stufe ein Bauen-Fixen in Android Studio.

Fortschritt:
- ✅ **Animation** „eigene Karten fliegen zum Gegner" (separat erledigt).
- ✅ **Stufe 1 — Engine** (additiv; 2-Spieler-Pfad bleibt grün).
- ✅ **Stufe 2 — Protokoll + Authority** (additiv: `players[]`, `targetId`, `expectedPlayers`, `PLAYER_LEFT`; alte Felder bleiben).
- ✅ **Stufe 3 — Lokaler Transport** (`GameServer.start(N)`, `sendAsk(rank, targetId)`; additiv).
- ✅ **Stufe 4 — Lobby-UI** (Gegnerzahl-Auswahl: online vor der Suche, lokal in der Hosten-Karte; `LOBBY`-Fortschritt; i18n). Online-Suche noch 1v1 bis Stufe 6.
- ✅ **Stufe 5 — Spiel-UI** (N-Spieler-State, Mehr-Gegner-Panels mit Auswahl, Frage-Ablauf + Fehlermeldungen, `PLAYER_LEFT`/Ausgrauen, N-Spieler-Log/Status/Sieger-Popup; i18n).
- ✅ **Stufe 6 — Online (Client)**: `OnlineGameClient` N-Spieler-fähig + abwärtskompatibel (2-Spieler 1:1 wie bisher, 3–4 via adressiertem Routing); `FIND {size}`; saubere Fehlermeldung, wenn das Relay (noch) keinen N-Spieler-Raum bildet. **Relay-Server-Vertrag: `RELAY_PROTOCOL.md`** — Server-Umsetzung steht noch aus (extern).

---

## 1. Ziel & Umfang

- **2–4 Spieler** (1–3 Gegner). Standard bleibt **1 Gegner**.
- **Fokus zuerst: 3-Spieler-UI** (2 Gegner) vollständig spielbar; 4-Spieler-Layout
  wird vorbereitet, aber später ausgebaut/getestet.
- **Gegnerzahl-Auswahl (1–3) in beiden Lobbys** (lokal + online), Standard 1.
- **Online ist in Scope** (Render-Relay unterstützt laut Nutzer N Spieler). Das
  Relay-Protokoll wird beim Bau der Transport-/Online-Schicht gegen den echten
  Server verifiziert (siehe §7).

---

## 2. Festgelegte Entscheidungen

1. **LAN-Beitritt:** Host wählt Gegnerzahl; Gäste verbinden per Host-IP. Host-Dialog
   zeigt „X/N verbunden", Spiel startet automatisch, sobald voll.
2. **Spieler verlässt laufendes Spiel (3+):** Spiel **läuft weiter**. Handkarten des
   Verlassenden werden **zurück ins Deck gemischt**, eine **lokalisierte Info-Meldung**
   geht an alle Spieler, der Spieler wird in der UI **ausgegraut**. Bleibt nur **ein**
   Gegner übrig, entfällt die Gegner-Auswahl (Ziel automatisch).
3. **Zugreihenfolge:** fest nach Beitritt (0 → 1 → 2 → …), im Uhrzeigersinn.
4. **„Nicht 2× dieselbe Person":** Die zuletzt von einem Spieler gefragte Person darf
   von ihm **nicht direkt erneut** gefragt werden — **auch zugübergreifend**. Gilt nur,
   solange es mehr als einen möglichen Gegner gibt (sonst wäre Spielen unmöglich).
5. **Gegner-gegen-Gegner-Animationen** (du bist nur Zuschauer): nur Log-Eintrag, keine
   Flug-Animation. Flug-Animationen nur, wenn DU beteiligt bist.
6. **Spielerzahl:** max. **4 Spieler / 3 Gegner**. Startkarten: **2→8, 3→7, 4→5**.

---

## 3. Stufenplan

### Stufe 1 — Engine (`game/GameEngine.kt`, `model/Deck.kt`) ✅ UMGESETZT
- Additiv: 2–4 Spieler, `processAsk(asker, **targetId**, rank)` + 2-arg-Overload;
  `canAsk(asker, targetId, rank)` + 2-arg-Overload; `othersOf`, `activePlayers`,
  Zugrotation `nextActivePlayerAfter` (Uhrzeigersinn), `startingHandSize(n)`,
  `removePlayer` (Hand → `Deck.addAndShuffle`), `lastAskedTarget` (zugübergreifend),
  Spielende/Sieger für N. `AskResult` um `targetId` erweitert. **2-Spieler unverändert.**

### Stufe 2 — Protokoll + Authority (`network/Protocol.kt`, `GameAuthority.kt`)
- **`GAME_START`** je Spieler: `yourId`, `yourHand`, `yourTurn`, `currentPlayerId`,
  `deckSize`, **`players`** = `[{id,name,avatarKind,avatarColor,handSize,books}]`.
- **`ASK`**: `{ type, rank, **targetId** }`.
- **`ASK_RESULT`** an **alle** Spieler (eigene Sicht): `rank, askerId, targetId,
  askerIsYou, targetIsYou, gotCards, cardCount, wentFishing, drawnMatched,
  drawnCard(nur Asker), newBooks, yourHand, yourBooks, players[], currentPlayerId,
  yourTurn, gameOver(+Buchstände)`.
- **`PLAYER_LEFT`** (neu/ersetzt OPPONENT_LEFT): `playerId` + aktualisierter State;
  Authority ruft `engine.removePlayer(id)` und broadcastet eine lokalisierbare Meldung.
- Authority: bis `expectedPlayers` beitreten lassen; `broadcastResult` über alle;
  `playerAsk(askerId, targetId, rank)`.

### Stufe 3 — Lokaler Transport (`GameServer.kt`, `GameClient.kt`, `GameSession.kt`)
- `GameServer.start(expectedPlayers)` akzeptiert N Verbindungen (statt fix 2).
- `sendAsk(rank, targetId)`.

### Stufe 4 — Lobby-UI (`MainActivity.kt`)
- Auswahl 1–3 Gegner, Standard 1, in **beiden** Lobbys.
  - Lokal: Wert → `GameServer.start(count+1)`; Host-Dialog „X/N verbunden".
  - Online: Auswahl aktiv (Render-Relay unterstützt N) — Verifikation in Stufe 6.
- i18n (6 Sprachen): Auswahl-Label, „X/N verbunden".

### Stufe 5 — Spiel-UI (`GameActivity.kt`)
- `players`-State (id/name/avatar/handSize/books, aktiv?), `myId`, `currentPlayerId`,
  `selectedTargetId`, `selectedRank`.
- Gegner-Panel: 1 Gegner = wie bisher; 2 Gegner = Fläche in zwei antippbare Hitboxen
  teilen (Auswahl = Ziel, hervorgehoben); 3 Gegner = später.
- Frage-Ablauf (3+): Gegner UND Karte wählen → „Fragen". Fehlermeldung bei fehlender
  Auswahl. Zuletzt gefragten Gegner sperren (Regel §2.4). Verlassene Gegner ausgrauen.
  Bei 1 verbleibenden Gegner keine Auswahl nötig.
- Zug-Anzeige „Warten auf [Name]" (kann anderer Gegner sein). Lokalisierte
  „Spieler X hat verlassen, Karten zurück ins Deck"-Meldung.
- Animationen: STEAL/GIVE Richtung passendes Gegner-Panel; Gegner-vs-Gegner nur Log.
- i18n (6 Sprachen): „Gegner wählen", Fehlermeldungen, Verlassen-Meldung.

### Stufe 6 — Online (`OnlineGameClient.kt`, `RelayProtocol.kt`)
- N-Spieler-Räume über das Relay; GUEST-PIDs je Index (1,2,3), mehrere JOINs.
- **Relay-Protokoll gegen den echten Render-Server verifizieren** (FIND/MATCHED/Routing
  für >2). Falls der Server doch nur paart, hier nachsteuern.

---

## 4. Nachrichtenformate (Zielzustand)

```jsonc
// GAME_START (an Spieler p)
{ "type":"GAME_START", "yourId":0, "yourHand":[...], "yourTurn":true,
  "currentPlayerId":0, "deckSize":31,
  "players":[ {"id":0,"name":"A","avatarKind":"FISH","avatarColor":"SUN","handSize":7,"books":[]}, ... ] }

// ASK (Client -> Authority)
{ "type":"ASK", "rank":"K", "targetId":2 }

// ASK_RESULT (an jeden Spieler, eigene Sicht)
{ "type":"ASK_RESULT", "rank":"K", "askerId":0, "targetId":2,
  "askerIsYou":false, "targetIsYou":true, "gotCards":true, "cardCount":2,
  "wentFishing":false, "drawnMatched":false, "yourHand":[...], "yourBooks":[...],
  "players":[ {"id":0,"handSize":9,"books":[...]}, ... ],
  "currentPlayerId":0, "yourTurn":false, "gameOver":false }

// PLAYER_LEFT
{ "type":"PLAYER_LEFT", "playerId":1, "players":[...], "currentPlayerId":2, "deckSize":34 }
```

---

## 5. Risiken
- Großer, mehrschichtiger Umbau; je Stufe Build-Check in Android Studio nötig.
- Protokoll-Änderung ist nicht abwärtskompatibel → alle Clients brauchen dieselbe
  App-Version (bei LAN unkritisch; online ggf. relevant).
- 2-Spieler-Pfad (Default 1 Gegner) darf nicht regredieren — bei jeder Stufe prüfen.
- Online hängt am Verhalten des externen Render-Relays (Stufe 6 verifiziert das).

---

## 6. Hinweis
Diese Datei ist ein Arbeitsdokument im Repo-Root und kann am Ende entfernt werden.
