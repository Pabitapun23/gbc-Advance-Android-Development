pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            // Do not change the username below. It should always be "mapbox" (not your username).
            credentials.username = "mapbox"
            // Replace this with your secret token
            // In production (real life), you should never expose your secret token
            // Instead, follow instructions on the Mapbox website for how to hide this in a secure way
            credentials.password = "sk.eyJ1IjoiamRvZTI1IiwiYSI6ImNscHJvejlxajAxb3cybHBodjduNzk3dmYifQ.KWmaBAQlyZQH9OAGXbm-bQ"


            authentication.create<BasicAuthentication>("basic")
        }

    }
}

rootProject.name = "LocationAndMapDemo"
include(":app")
 