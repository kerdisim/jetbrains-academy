package svcs

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

private fun bytesToHex(hash: ByteArray): String {
    val hexString = StringBuilder(2 * hash.size)
    for (i in hash.indices) {
        val hex = Integer.toHexString(0xff and hash[i].toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString()
}

fun main(args: Array<String>) {
    val help = """
                  These are SVCS commands:
                  config     Get and set a username.
                  add        Add a file to the index.
                  log        Show commit logs.
                  commit     Save changes.
                  checkout   Restore a file.
               """.trimIndent()

    if (args.isEmpty()) {
        println(help)
        return
    }

    val vcsDirectory = "vcs"
    Files.createDirectories(Paths.get(vcsDirectory))
    val configFile = File("$vcsDirectory/config.txt")
    val indexFile = File("$vcsDirectory/index.txt")
    val logFile = File("$vcsDirectory/log.txt")

    val digest = MessageDigest.getInstance("SHA3-256")

    when (args[0]) {
        "--help" -> {
            println(help)
        }
        "config" -> {
            if (args.size > 1) {
                // set username
                val username = args[1]
                configFile.writeText(username)
                println("The username is $username.")
            } else {
                // get username
                if (configFile.exists()) {
                    val username = configFile.readText()
                    println("The username is $username.")
                } else {
                    println("Please, tell me who you are.")
                }
            }
        }
        "add" -> {
            if (args.size > 1) {
                // add file to the index
                val filename = args[1]
                if (File(filename).exists()) {
                    indexFile.appendText("$filename\n")
                    println("The File '$filename' is tracked.")
                } else {
                    println("Can't found '$filename'.")
                }
            } else {
                if (indexFile.exists()) {
                    println("Tracked files:")
                    indexFile.forEachLine { println(it) }
                } else {
                    println("Add a file to the index.")
                }
            }
        }
        "log" -> {
            if (logFile.exists()) {
                logFile.readLines().asReversed().forEach { line ->
                    val (commitId, author, message) = line.split(";")
                    println("commit $commitId")
                    println("Author: $author")
                    println(message)
                    println()
                }
            } else {
                println("No commits yet.")
            }
        }
        "commit" -> {
            if (args.size == 1) {
                println("Message was not passed.")
                return
            }

            val author = configFile.readText()
            val message = args[1].trim('"')

            val builder = StringBuilder()
            indexFile.forEachLine { filename ->
                builder.append(File(filename).readText())
            }
            val trackedFilesHash = bytesToHex(digest.digest(builder.toString().toByteArray()))

            val lastCommitId = if (logFile.exists()) {
                logFile.readLines().last().split(";")[0]
            } else {
                null
            }

            if (trackedFilesHash == lastCommitId) {
                println("Nothing to commit.")
                return
            }

            indexFile.forEachLine { filename ->
                File(filename).copyTo(File("$vcsDirectory/commits/$trackedFilesHash/$filename"))
            }

            logFile.appendText("$trackedFilesHash;$author;$message\n")
            println("Changes are committed.")
        }
        "checkout" -> {
            if (args.size == 1) {
                println("Commit id was not passed.")
                return
            }

            val commitId = args[1]

            if (logFile.readLines().any { line -> line.split(";")[0] == commitId }) {
                File("$vcsDirectory/commits/$commitId/")
                    .walk()
                    .filter { it.isFile }
                    .forEach { it.copyTo(File(it.name), true) }

                println("Switched to commit $commitId.")
            } else {
                println("Commit does not exist.")
            }
        }
        else -> {
            println("'${args[0]}' is not a SVCS command.'")
        }
    }
}
