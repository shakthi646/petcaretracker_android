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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PetCareTracker"
include(":app")
include(":ksp-core-library")

val user = System.getenv("USER") ?: ""
if(user.isNotBlank())
{
    project(":ksp-core-library").projectDir = File("/Users/${user}/MyHome/Workspace/Android/KspCoreFramework/coreLibrary")
}
