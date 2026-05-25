# ── Build-Stage ───────────────────────────────────────────────────────────
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle installDist --no-daemon

# ── Runtime-Stage ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/src/build/install/gofish-relay /app
# Hosting-Plattformen (Railway/Render/Fly) setzen $PORT; lokal Default 8080.
EXPOSE 8080
CMD ["/app/bin/gofish-relay"]
