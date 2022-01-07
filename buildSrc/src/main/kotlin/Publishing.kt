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
            setProperty("build", "-1")
            setProperty("version", "alpha")
        }
    }


    var group: String = "com.vad"
    var artifactId: String = "sign"
    var gprBaseUrl = "https://maven.pkg.github.com"
    var gprRepoOwner = "LiemVo"
    var gprRepoId = "sign-android"

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
        else -> -1
    }

    var version  = if (System.getenv().containsKey("VERSION")) {
        System.getenv("VERSION")
    } else {
        localProperties.getProperty("version").trim()
    }
}