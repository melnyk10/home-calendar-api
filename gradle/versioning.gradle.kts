import java.util.Properties

fun readVersion(): Pair<Int, Int> {
    val v = project.version.toString()
    require(Regex("""^\d+\.\d+$""").matches(v)) {
        "Version must be MAJOR.MINOR (got '$v')"
    }
    val (maj, min) = v.split(".").map { it.toInt() }
    return maj to min
}

fun writeVersion(newVersion: String) {
    val propsFile = rootProject.file("gradle.properties")
    val props = Properties().apply { propsFile.inputStream().use { load(it) } }
    props["version"] = newVersion
    propsFile.outputStream().use { props.store(it, null) }
    println("🔖 Version bumped to $newVersion")
}

tasks.register("bumpMinorAndTag") {
    group = "release"
    description = "Increase MINOR version (X.Y → X.Y+1) and tag"
    doLast {
        val (major, minor) = readVersion()
        val newVersion = "${major}.${minor + 1}"
        writeVersion(newVersion)

        exec { commandLine("git", "add", "gradle.properties") }
        exec { commandLine("git", "commit", "-m", "chore(release): bump to $newVersion") }
        exec { commandLine("git", "tag", "v$newVersion") }
    }
}

tasks.register("bumpMajorAndTag") {
    group = "release"
    description = "Increase MAJOR version (X.Y → X+1.0) and tag"
    doLast {
        val (major, _) = readVersion()
        val newVersion = "${major + 1}.0"
        writeVersion(newVersion)

        exec { commandLine("git", "add", "gradle.properties") }
        exec { commandLine("git", "commit", "-m", "chore(release): bump to $newVersion") }
        exec { commandLine("git", "tag", "v$newVersion") }
    }
}