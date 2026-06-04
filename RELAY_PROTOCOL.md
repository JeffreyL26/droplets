# Relay-Server-Protokoll (N-Spieler) — Spezifikation

Diese Datei beschreibt den **Vertrag zwischen App und Relay-Server** für Online-
Spiele mit **2–4 Spielern**. Der Relay-Server selbst liegt nicht in diesem Repo;
er muss diese Spezifikation umsetzen. Der Android-Client (`OnlineGameClient`,
`RelayProtocol`) ist bereits passend implementiert.

Transport: ein WebSocket pro Client zum Pfad `/ws`. Alle Nachrichten sind JSON-
Textframes. Das Relay kennt nur die Steuer- und Routing-Nachrichten unten; alle
übrigen Frames sind App-Spielprotokoll (`JOIN`/`ASK`/`GAME_START`/`ASK_RESULT`/…)
und werden vom Relay nur **weitergeleitet**, nicht interpretiert.

---

## 1. Matchmaking

### Client → Relay: `FIND`
```jsonc
{ "type": "FIND", "size": 3 }     // size = gewünschte Gesamt-Spielerzahl (2–4)
```
Das Relay sammelt wartende Clients **nach gewünschter `size`** und bildet einen
Raum, sobald `size` Clients mit derselben `size` warten. (size 2 nur mit size 2,
size 3 nur mit size 3, usw.)

### Relay → Client: `MATCHED` (an jeden Spieler im vollen Raum)
```jsonc
{ "type": "MATCHED", "role": "HOST"|"GUEST", "playerIndex": 0, "roomSize": 3 }
```
- Genau **ein** Spieler bekommt `role:"HOST"` mit `playerIndex: 0`.
- Die übrigen bekommen `role:"GUEST"` mit `playerIndex: 1..roomSize-1` (eindeutig).
- `roomSize` = tatsächliche Spielerzahl im Raum (= angefragte `size`).
- `playerIndex` ist zugleich die **Spieler-ID** im Spiel (pid).

Wichtig: Der **Client bricht ab** (klare Fehlermeldung), wenn er `size > 2`
angefragt hat, aber `roomSize < size` zurückkommt — d. h. das Relay den N-Spieler-
Raum nicht bilden konnte. Ein altes, rein paarweises Relay (ohne `roomSize`)
führt damit NICHT zu einem still herabgestuften Spiel.

---

## 2. Routing nach dem Match

Der HOST führt die gesamte Spiellogik (`GameAuthority`) in-process. Er erzeugt pro
Spieler **eigene** Nachrichten (perspektivisches `ASK_RESULT` usw.). Deshalb muss
das Relay **gerichtet** zustellen.

### 2-Spieler-Raum (`roomSize == 2`) — Legacy, 1:1
Wie bisher: das Relay leitet jeden Frame **unverändert** an den jeweils anderen
Peer weiter. Keine Briefumschläge. (So bleibt der bestehende 2-Spieler-Betrieb
auf dem aktuellen Relay unverändert lauffähig.)

### N-Spieler-Raum (`roomSize > 2`) — adressiert
- **HOST → Relay** (an einen bestimmten Gast):
  ```jsonc
  { "type": "TO", "to": 2, "msg": { /* Spielnachricht */ } }
  ```
  Das Relay stellt **`msg`** (ausgepackt) an `playerIndex == to` zu.
- **Gast → Relay**: der Gast sendet die Spielnachricht **roh** (`JOIN`/`ASK`).
  Das Relay verpackt sie und stellt sie dem HOST zu:
  ```jsonc
  { "type": "FROM", "from": 2, "msg": { /* rohe Spielnachricht des Gastes */ } }
  ```
  `from` = `playerIndex` des Absenders (vom Relay autoritativ gesetzt).
- **Gast empfängt** stets **rohe** Spielnachrichten (das Relay packt `TO` aus).

Kurz: Gäste reden immer nur mit dem HOST und kennen keine Briefumschläge; nur der
HOST sendet `TO` und empfängt `FROM`.

---

## 3. Verlassen

### Relay → verbleibende Spieler: `PEER_LEFT`
```jsonc
{ "type": "PEER_LEFT", "playerIndex": 2 }   // wer den Raum verlassen hat
```
- HOST: ruft intern `removePlayer(playerIndex)` → mischt dessen Karten zurück und
  informiert die übrigen (`PLAYER_LEFT`-Spielnachricht via `TO`), bzw. beendet die
  Sitzung, wenn ≤1 aktiver Spieler übrig ist (`OPPONENT_LEFT`).
- GUEST: reagiert nur, wenn der HOST (`playerIndex == 0`) gegangen ist → Sitzung
  endet. Verlässt ein anderer Gast, erfährt der Gast das über die normale
  `PLAYER_LEFT`-Spielnachricht des HOST.

Für `roomSize == 2` darf `playerIndex` entfallen (Legacy).

---

## 4. Server-Pflichten (Zusammenfassung)
1. `FIND {size}` puffern und Räume **gleicher** `size` bilden.
2. Bei vollem Raum `MATCHED {role, playerIndex, roomSize}` an alle senden
   (genau 1 HOST mit Index 0).
3. `roomSize == 2`: Frames 1:1 weiterleiten (Legacy).
4. `roomSize > 2`: `TO {to,msg}` vom HOST → `msg` an Spieler[to]; rohe Gast-Frames
   → `FROM {from,msg}` an den HOST.
5. Trennung eines Spielers: `PEER_LEFT {playerIndex}` an die übrigen; Raum schließen,
   sobald <2 übrig.

---

## 5. Client-Status (bereits umgesetzt)
- `RelayProtocol.kt`: Konstanten `FIND/MATCHED/PEER_LEFT/TO/FROM` + Feldnamen.
- `OnlineGameClient.kt`: sendet `FIND {size}`; liest `role/playerIndex/roomSize`;
  HOST nutzt `TO`-Umschläge (roomSize>2) bzw. 1:1 (roomSize==2) und verarbeitet
  `FROM`; GUEST unverändert (roh); `PEER_LEFT {playerIndex}`-Handling; Abbruch mit
  klarer Meldung, falls das Relay die gewünschte Spielerzahl nicht bildet.
