package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Transposes the matrix.
 *
 * @param m the matrix to transpose.
 * @return the transposed matrix.
 */
fun transpose(m: Array<Array<Double>>): Array<Array<Double>> {
    val transposed = Array(m[0].size) { Array(m.size) { 0.0 } }

    for (i in 0..m.lastIndex) {
        for (j in 0..m[0].lastIndex) {
            transposed[j][i] = m[i][j]
        }
    }

    return transposed
}

fun getImageEnergies(img: BufferedImage): Array<Array<Double>> {
    val width = img.width
    val height = img.height
    val energies = Array(height) { Array(width) { 0.0 } }

    for (y in 0 until height) {
        for (x in 0 until width) {

            val pX = when (x) {
                0 -> 1
                width - 1 -> width - 2
                else -> x
            }

            val pY = when (y) {
                0 -> 1
                height - 1 -> height - 2
                else -> y
            }

            val left = Color(img.getRGB(pX - 1, y))
            val right = Color(img.getRGB(pX + 1, y))
            val bottom = Color(img.getRGB(x, pY + 1))
            val top = Color(img.getRGB(x, pY - 1))

            val rX = (left.red - right.red).toDouble()
            val gX = (left.green - right.green).toDouble()
            val bX = (left.blue - right.blue).toDouble()
            val xGradient = rX.pow(2.0) + gX.pow(2.0) + bX.pow(2.0)

            val rY = (top.red - bottom.red).toDouble()
            val gY = (top.green - bottom.green).toDouble()
            val bY = (top.blue - bottom.blue).toDouble()
            val yGradient = rY.pow(2.0) + gY.pow(2.0) + bY.pow(2.0)

            val energy = sqrt(xGradient + yGradient)

            energies[y][x] = energy
        }
    }
    return energies
}

fun findVerticalSeam(energies: Array<Array<Double>>): List<Int> {
    val sums = energies.map { it.clone() }

    // the top row has nothing above it, so the energies are the same as the source
    // for each pixel in the rest of the rows, the energy is its own energy
    // plus the minimal of the three energies above

    for (i in 1..energies.lastIndex) {
        for (j in 0..energies[0].lastIndex) {
            val rowAbove = i - 1
            val energiesAbove = mutableListOf(sums[rowAbove][j])

            if (j > 0) {
                energiesAbove.add(sums[rowAbove][j - 1])
            }

            if (j < energies[0].lastIndex) {
                energiesAbove.add(sums[rowAbove][j + 1])
            }

            sums[i][j] += energiesAbove.minOrNull()!!
        }
    }


    // for the lowest energies we have at the end, work back up th minimals to recover
    // the seam with minimal energy
    var colIdxOfLowest = sums.last().withIndex().minByOrNull { (_, value) -> value }!!.index

    val seamColumnsFromBottom = mutableListOf(colIdxOfLowest)

    for (i in sums.lastIndex downTo 1) {
        val rowAbove = i - 1

        val energiesAboveWithIndex = mutableListOf(Pair(colIdxOfLowest, sums[rowAbove][colIdxOfLowest]))

        if (colIdxOfLowest > 0) {
            energiesAboveWithIndex.add(Pair(colIdxOfLowest - 1, sums[rowAbove][colIdxOfLowest - 1]))
        }

        if (colIdxOfLowest < sums[0].lastIndex) {
            energiesAboveWithIndex.add(Pair(colIdxOfLowest + 1, sums[rowAbove][colIdxOfLowest + 1]))
        }

        colIdxOfLowest = energiesAboveWithIndex.minByOrNull { (_, b) -> b }!!.first


        seamColumnsFromBottom.add(colIdxOfLowest)
    }

    return seamColumnsFromBottom.reversed().toList()
}

fun findHorizontalSeam(energies: Array<Array<Double>>): List<Int> {
    return findVerticalSeam(transpose(energies))
}

fun carveVerticalSeam(img: BufferedImage): BufferedImage {
    val seam = findVerticalSeam(getImageEnergies(img))

    for (i in 0..seam.lastIndex) {
        for (k in seam[i] until img.width - 1) {
            img.setRGB(k, i, img.getRGB(k + 1, i))
        }
    }
    return img.getSubimage(0, 0, img.width - 1, img.height)
}

fun carveHorizontalSeam(img: BufferedImage): BufferedImage {
    val seam = findHorizontalSeam(getImageEnergies(img))

    for (j in 0..seam.lastIndex) {
        for (k in seam[j] until img.height - 1) {
            img.setRGB(j, k, img.getRGB(j, k + 1))
        }
    }

    return img.getSubimage(0, 0, img.width, img.height - 1)
}

fun main(args: Array<String>) {
    val inputName = args[args.indexOf("-in") + 1]
    val outputName = args[args.indexOf("-out") + 1]
    val reduceWidthBy = args[args.indexOf("-width") + 1].toInt()
    val reduceHeightBy = args[args.indexOf("-height") + 1].toInt()

    try {
        var img: BufferedImage = ImageIO.read(File(inputName))

        repeat(reduceWidthBy) {
            img = carveVerticalSeam(img)
        }

        repeat(reduceHeightBy) {
            img = carveHorizontalSeam(img)
        }

        ImageIO.write(img, "png", File(outputName))
    } catch (e: IOException) {
        println(e.message)
    }
}
