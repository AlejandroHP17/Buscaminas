# Estructura — Buscaminas

## Rol en el ecosistema

```mermaid
flowchart TB
  sdk[MiniJuegosSdk GameModule]
  lib[":buscaminas library AAR"]
  shell[":app shell de prueba"]
  host[MiniJuegos]

  sdk --> lib
  shell --> lib
  lib -->|GitHub Packages| host
```

## Árbol del proyecto

```
Buscaminas/
├── .github/workflows/ci.yml
├── app/                         # APK de prueba
│   └── src/main/java/.../app/MainActivity.kt
├── buscaminas/                  # Library → AAR
│   ├── build.gradle.kts         # maven-publish + dependencias
│   └── src/main/java/pelkidev/com/mx/buscaminas/
│       ├── BuscaminasGameModule.kt
│       ├── domain/
│       ├── data/
│       ├── presentation/
│       └── ui/theme/
├── settings.gradle.kts
└── docs/
```

## Dualidad library + shell

```mermaid
flowchart LR
  subgraph repo [Repo Buscaminas]
    AAR[":buscaminas\ncom.android.library"]
    APK[":app\ncom.android.application"]
    APK -->|implementation project| AAR
  end
  Host[MiniJuegos] -->|Maven AAR| AAR
```

| | `:buscaminas` | `:app` |
|--|---------------|--------|
| Salida | AAR | APK |
| `applicationId` | no | `pelkidev.com.mx.buscaminas` |
| Entry | `BuscaminasGameModule` | `MainActivity` → `Entry` |
| Recursos | prefijo `buscaminas_` | launcher, tema de app |

**Por qué no publicar el módulo `app`:** un `application` produce APK, no un artefacto embebible idiomático. El host necesita un library.

## Capas del library

```mermaid
flowchart TB
  entry[BuscaminasGameModule.Entry]
  ui[presentation Compose]
  vm[MinesweeperViewModel]
  domain[domain MinesweeperEngine]
  data[data GameSaveRepository]

  entry --> ui
  ui --> vm
  vm --> domain
  vm --> data
```

- **domain**: reglas puras del tablero (testeable sin Android).
- **data**: guardar / restaurar partida.
- **presentation**: UI y estado observable.
- **Entry**: único punto que conoce el host; aplica `BuscaminasTheme` y pasa `onExit`.

## Entrypoint hacia el host

```mermaid
sequenceDiagram
  participant Host as MiniJuegos
  participant Mod as BuscaminasGameModule
  participant Screen as MinesweeperScreen

  Host->>Mod: Entry onExit
  Mod->>Screen: MinesweeperScreen onExit
  Note over Screen: Juego completo
  Screen->>Host: onExit callback
```

## Publicación

```mermaid
sequenceDiagram
  participant Dev
  participant CI as GitHub_Actions
  participant GPR as Packages_Buscaminas
  participant Sdk as Packages_SDK

  Dev->>CI: push main
  CI->>Sdk: resolve sdk:1.0.0
  CI->>CI: test + assemble
  CI->>GPR: publish buscaminas SNAPSHOT
```

Coordenadas:

- Publish URL: `https://maven.pkg.github.com/AlejandroHP17/Buscaminas`
- Artifact: `pelkidev.com.mx.minijuegos:buscaminas:1.0.0-SNAPSHOT`

## Alineación técnica

- `minSdk 29`, Compose BOM alineado con host/SDK
- `resourcePrefix = "buscaminas_"` evita colisiones de `R`
- `api(sdk)` para que el host vea `GameModule` transitivamente (el host también declara el SDK de forma explícita)
