package cryptography

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

fun byteToBinaryArray(byte: Byte): Array<Boolean> {
    val bits = Array(8) { false }
    var b = byte
    var cursor = 0

    while (b >= 1) {
        if (b % 2 == 1) {
            bits[7 - cursor] = true
        }
        b = (b / 2).toByte()
        cursor++
    }

    return bits
}

fun byteArrayToBinaryArray(bytes: ByteArray): Array<Boolean> {
    return bytes.flatMap { byte: Byte -> byteToBinaryArray(byte).asIterable() }.toTypedArray()
}

fun xorEncrypt(message: ByteArray, password: ByteArray): ByteArray {
    val result = ByteArray(message.size)

    for (i in message.indices) {
        result[i] = (message[i].toInt() xor password[i % password.size].toInt()).toByte()
    }

    return result
}

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (val input = readLine()!!) {
            "exit" -> {
                println("Bye!")
                return
            }
            "hide" -> {
                println("Input image file:")
                val inputFile = readLine()!!
                println("Output image file:")
                val outputFile = readLine()!!
                println("Message to hide:")
                val message = readLine()!!
                println("Password:")
                val password = readLine()!!

                try {
                    val img: BufferedImage = ImageIO.read(File(inputFile))

                    val encryptedMessage =
                        xorEncrypt(message.encodeToByteArray(), password.encodeToByteArray())

                    val bytes = encryptedMessage.toMutableList()
                    bytes.addAll(listOf(0, 0, 3))

                    val bits = byteArrayToBinaryArray(bytes.toByteArray())

                    val pixelCount = img.height * img.width

                    if (bits.size > pixelCount) {
                        println("The input image is not large enough to hold this message.")
                        continue
                    }

                    var cursor = 0

                    loop@ for (i in 0 until img.height) {
                        for (j in 0 until img.width) {
                            if (cursor == bits.size) {
                                break@loop
                            }

                            val rgbPixel = img.getRGB(j, i)
                            val color = Color(rgbPixel)

                            val bit: Int = if (bits[cursor]) 1 else 0
                            val blue: Int = ((color.blue shr 1) shl 1) or bit

                            img.setRGB(j, i, Color(color.red, color.green, blue).rgb)

                            cursor++
                        }
                    }

                    ImageIO.write(img, "png", File(outputFile))
                    println("Message saved in $outputFile image.")
                } catch (e: IOException) {
                    println(e.message)
                }
            }
            "show" -> {
                println("Input image file:")
                val inputFile = readLine()!!
                println("Password:")
                val password = readLine()!!

                try {
                    val img: BufferedImage = ImageIO.read(File(inputFile))

                    val bits = BitSet(8)
                    val bytes: MutableList<Byte> = mutableListOf()

                    var cursor = 0

                    loop@ for (i in 0 until img.height) {
                        for (j in 0 until img.width) {
                            val rgbPixel = img.getRGB(j, i)
                            val color = Color(rgbPixel)
                            val blue = color.blue
                            val lastBit = blue and 1

                            if (lastBit == 1) {
                                bits.set(7 - cursor)
                            } else {
                                bits.clear(7 - cursor)
                            }
                            cursor++

                            if (cursor == 8) {
                                val byteArray = bits.toByteArray()

                                if (byteArray.isEmpty()) {
                                    bytes.add(0)
                                } else {
                                    bytes.add(byteArray[0])
                                }

                                if (
                                    bytes.size >= 3 &&
                                    bytes[bytes.lastIndex - 2].toInt() == 0 &&
                                    bytes[bytes.lastIndex - 1].toInt() == 0 &&
                                    bytes[bytes.lastIndex].toInt() == 3
                                ) {
                                    break@loop
                                }

                                cursor = 0
                            }
                        }
                    }
                    println("Message:")
                    println(
                        xorEncrypt(
                            bytes.subList(0, bytes.size - 3).toByteArray(),
                            password.encodeToByteArray()
                        ).toString(Charsets.UTF_8)
                    )
                } catch (e: IOException) {
                    println(e.message)
                }
            }
            else -> {
                println("Wrong task: $input")
            }
        }
    }
}
