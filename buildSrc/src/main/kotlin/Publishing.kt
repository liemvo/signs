import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

object Maven {
    var localProperties = Properties().apply {
        try {
            load(FileInputStream("buildSrc/local.properties"))
        } catch (ex: FileNotFoundException) {
            setProperty("user", "unspecified")
            setProperty("key", "unspecified")
            setProperty("build", "1")
            setProperty("version", "0.7")
        }
    }


    var group: String = "com.github.liemvo"
    var artifactId: String = "signs"
    var gprBaseUrl = "https://maven.pkg.github.com"
    var gprRepoOwner = "liemvo"
    var gprRepoId = "signs"

    var gprUser = if (localProperties.containsKey("user")) {
        localProperties.getProperty("user").trim()
    } else {
        "unspecified"
    }

    var gprKey= when {
        localProperties.containsKey("key") -> localProperties.getProperty("key").trim()
        else -> "unspecified"
    }

    var build:Int = when {
        localProperties.containsKey("build") -> localProperties.getProperty("build").trim().toInt()
        else -> 1
    }

    var version  = if (localProperties.contains("version")) {
        localProperties.getProperty("version").trim()
    } else {
        "0.7"
    }
}
