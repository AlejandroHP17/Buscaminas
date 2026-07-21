pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val githubOwner = "AlejandroHP17"
val gprUser: Provider<String> = providers.gradleProperty("gpr.user")
    .orElse(providers.environmentVariable("GITHUB_ACTOR"))
    .orElse(githubOwner)
val gprToken: Provider<String> = providers.gradleProperty("gpr.token")
    .orElse(providers.environmentVariable("GH_PACKAGES_TOKEN"))
    .orElse(providers.environmentVariable("GITHUB_TOKEN"))
    .orElse("")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            name = "GitHubPackagesMiniJuegosSdk"
            url = uri("https://maven.pkg.github.com/$githubOwner/MiniJuegosSdk")
            credentials {
                username = gprUser.get()
                password = gprToken.get()
            }
        }
    }
}

rootProject.name = "Buscaminas"
include(":app")
include(":buscaminas")
