# Go Fish! — Relay-Server

Kleiner, spiel-agnostischer Matchmaking-/Relay-Server für den Online-Modus der
Go Fish!-App. Er paart zwei wartende Spieler und reicht danach alle Nachrichten
unverändert zwischen ihnen durch. Die gesamte Spiellogik bleibt in der App —
einer der beiden gepaarten Clients übernimmt die **HOST**-Rolle und führt die
Spiel-Autorität (`GameEngine`) in-process aus.

- **Sprache/Stack:** Kotlin + [Ktor](https://ktor.io) (Netty, WebSockets)
- **Endpoint:** `GET /ws` (WebSocket)
- **Port:** Umgebungsvariable `PORT` (Default `8080`)

## Protokoll (kurz)

1. Client verbindet sich auf `/ws` und sendet `{"type":"FIND"}`.
2. Der erste Wartende wird **HOST**, der nächste **GUEST**. Beide erhalten:
   `{"type":"MATCHED","role":"HOST"}` bzw. `{"type":"MATCHED","role":"GUEST"}`.
3. Danach werden alle Text-Frames 1:1 an den gepaarten Gegner weitergeleitet
   (das ist dann das normale Spielprotokoll: `JOIN`, `ASK`, `GAME_START`, …).
4. Trennt sich ein Spieler, erhält der andere `{"type":"PEER_LEFT"}`.

> Die Konstanten müssen mit `RelayProtocol` in der App übereinstimmen
> (`app/src/main/java/com/jbastudio/gofish/network/RelayProtocol.kt`).

## Lokal starten

Voraussetzung: JDK 17+ und Gradle (oder die Gradle-Distribution per `gradle`).

```bash
cd server
gradle run          # startet auf http://localhost:8080  → ws://localhost:8080/ws
```

In der App auf dem **Online-Bildschirm** als Server-Adresse eintragen:

- Emulator → `ws://10.0.2.2:8080/ws`
- Gerät im selben WLAN → `ws://<PC-IP>:8080/ws`

(Cleartext `ws://` ist in der App per `usesCleartextTraffic` für Tests erlaubt.)

## Als Container bauen & starten

```bash
cd server
docker build -t gofish-relay .
docker run -p 8080:8080 gofish-relay
```

## Deployen (Internet)

Damit sich Spieler an verschiedenen Orten finden, muss der Server öffentlich
erreichbar sein. Alle folgenden Plattformen setzen automatisch `$PORT` und
beenden TLS für dich (du bekommst eine `https://…`-URL → daraus wird `wss://…`):

### Railway / Render / Fly.io (Docker)
1. Dieses `server/`-Verzeichnis als eigenes Repo bzw. Service anlegen.
2. Plattform den `Dockerfile`-Build nutzen lassen.
3. Nach dem Deploy die öffentliche URL nehmen und in der App als
   `wss://<deine-domain>/ws` eintragen.

### Ohne Docker (Buildpack)
- `gradle installDist` erzeugt ein startbares Programm unter
  `build/install/gofish-relay/bin/gofish-relay`.

## Hinweise

- Der Server hält pro Wartendem genau einen offenen Slot; sobald ein zweiter
  Spieler sucht, werden beide gepaart. Mehrere Paare gleichzeitig sind möglich.
- Es gibt bewusst **keine** Persistenz/Authentifizierung — es ist ein reiner
  Durchleite-Server für 1-gegen-1-Partien.
- WebSocket-Pings (alle 15 s) halten Verbindungen durch NAT/Proxys offen.
