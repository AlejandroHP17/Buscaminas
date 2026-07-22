# Buscaminas

Juego de buscaminas embebible para [MiniJuegos](https://github.com/AlejandroHP17/MiniJuegos). Vive en su propio repo: library publicable (AAR) + app shell para pruebas independientes.

## Artefacto Maven

| Coordenada | Valor |
|------------|--------|
| Group | `pelkidev.com.mx.minijuegos` |
| Artifact | `buscaminas` |
| Versión (CI) | `1.0.0-SNAPSHOT` |

Depende de: `pelkidev.com.mx.minijuegos:sdk:1.0.0`

## Documentación

- [Estructura del proyecto](docs/ESTRUCTURA.md)
- Tutorial del ecosistema: [TUTORIAL.md](https://github.com/AlejandroHP17/MiniJuegos/blob/main/docs/TUTORIAL.md)

## Módulos

| Módulo | Tipo | Rol |
|--------|------|-----|
| `:buscaminas` | `android-library` | Motor, UI Compose, `BuscaminasGameModule` → AAR |
| `:app` | `android-application` | Shell de prueba (`applicationId` = `pelkidev.com.mx.buscaminas`) |

## Desarrollo local (standalone)

```bash
./gradlew :app:installDebug
```

La Activity llama a `BuscaminasGameModule.Entry(onExit = ::finish)`.

## Publicar AAR

### Maven Local (iteración rápida sin CI)

```bash
./gradlew :buscaminas:publishToMavenLocal
```

### GitHub Packages (lo que consume MiniJuegos vía CI)

Push a `main` dispara el workflow. Requiere secret `GH_PACKAGES_TOKEN` (PAT con `read:packages` / `write:packages`).

Manual:

```bash
export GITHUB_ACTOR=AlejandroHP17
export GH_PACKAGES_TOKEN=ghp_xxx
./gradlew :buscaminas:publishReleasePublicationToGitHubPackagesRepository
```

## Consumo desde MiniJuegos

```kotlin
implementation("pelkidev.com.mx.minijuegos:buscaminas:1.0.0-SNAPSHOT")
// y registro:
BuscaminasGameModule  // object público del AAR
```

## Requisitos CI / Android Studio remoto

Credenciales en `~/.gradle/gradle.properties` o env:

```properties
gpr.user=AlejandroHP17
gpr.token=ghp_xxx
```

## Capas internas del library

- `domain/` — motor, modelos, contratos de repositorio
- `data/` — persistencia de partida
- `presentation/` — ViewModel + pantallas Compose
- `BuscaminasGameModule.kt` — implementación de `GameModule`
