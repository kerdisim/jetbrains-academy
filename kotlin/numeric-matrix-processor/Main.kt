package processor

import java.util.*
import kotlin.math.pow

/**
 * Prints the matrix in a readable way.
 * @param matrix the matrix.
 */
fun printResult(matrix: Array<Array<Double>>) {
    println("The result is:")
    println(matrix.joinToString("\n") { it.joinToString(" ") })
}

/**
 * Multiplies each element of the matrix by the constant.
 * @param a the matrix.
 * @param c the constant.
 */
fun multiplyMatrixByAConstant(a: Array<Array<Double>>, c: Double) {
    for (i in a.indices) {
        for (j in a[0].indices) {
            a[i][j] = c * a[i][j]
        }
    }
}

/**
 * Removes the i-th row and j-th column from the matrix.
 * @param a the matrix.
 * @return The matrix with i-th row and j-th column removed.
 */
fun removeRowAndColumn(a: Array<Array<Double>>, i: Int, j: Int): Array<Array<Double>> {
    val rows = mutableListOf<Array<Double>>()

    for (r in a.indices) {
        if (r != i) {
            val newRow = mutableListOf<Double>()

            for (c in a.indices) {
                if (c != j) {
                    newRow.add(a[r][c])
                }
            }
            rows.add(newRow.toTypedArray())
        }
    }
    return rows.toTypedArray()
}

/**
 * Calculates the determinant of the matrix.
 * @param a the matrix.
 */
fun determinant(a: Array<Array<Double>>): Double {
    return when (a.size) {
        1 -> {
            a[0][0]
        }
        else -> {
            var sum = 0.0

            for (j in a.indices) {
                sum += (-1.0).pow(j.toDouble()) * a[0][j] * determinant(removeRowAndColumn(a, 0, j))
            }
            sum
        }
    }
}

/**
 * Calculates the (i, j) cofactor of the matrix.
 * @param a the matrix.
 * @param i the row number of the matrix.
 * @param j the column number of the matrix.
 * @return The cofactor.
 */
fun cofactor(a: Array<Array<Double>>, i: Int, j: Int): Double {
    return (-1.0).pow(i.toDouble() + j.toDouble()) * determinant(removeRowAndColumn(a, i, j))
}

/**
 * Calculates the inverse of a matrix.
 * @param a the matrix.
 * @return the inverted matrix or null if the inverse does not exist.
 */
fun inverse(a: Array<Array<Double>>): Array<Array<Double>>? {
    val det = determinant(a)

    if (det == 0.0) {
        println("This matrix doesn't have an inverse.")
        return null
    } else {
        val result = Array(a.size) { Array(a.size) { 0.0 } }

        for (i in a.indices) {
            for (j in a.indices) {
                result[j][i] = cofactor(a, i, j)
            }
        }

        multiplyMatrixByAConstant(result, 1 / det)

        return result
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    while (true) {
        println(
            """
                1. Add matrices
                2. Multiply matrix by a constant
                3. Multiply matrices
                4. Transpose matrix
                5. Calculate a determinant
                6. Inverse matrix
                0. Exit
            """.trimIndent()
        )

        print("Your choice: ")

        when (readLine()!!) {
            "0" -> break
            "1" -> {
                // add matrices
                print("Enter size of first matrix: ")
                val nA = scanner.nextInt()
                val mA = scanner.nextInt()

                println("Enter first matrix:")
                val a = Array(nA) { Array(mA) { scanner.nextDouble() } }

                print("Enter size of second matrix: ")
                val nB = scanner.nextInt()
                val mB = scanner.nextInt()

                println("Enter second matrix:")
                val b = Array(nB) { Array(mB) { scanner.nextDouble() } }

                if (nA != nB || mA != mB) {
                    println("The operation cannot be performed.")
                    continue
                }

                for (i in 0 until nA) {
                    for (j in 0 until mA) {
                        a[i][j] += b[i][j]
                    }
                }

                printResult(a)
            }
            "2" -> {
                // multiply matrix by constant
                print("Enter size of matrix: ")
                val nA = scanner.nextInt()
                val mA = scanner.nextInt()

                println("Enter matrix:")
                val a = Array(nA) { Array(mA) { scanner.nextDouble() } }

                print("Enter constant: ")
                val c = scanner.nextDouble()

                multiplyMatrixByAConstant(a, c)

                printResult(a)
            }
            "3" -> {
                // multiply matrices
                print("Enter size of first matrix: ")
                val rowsA = scanner.nextInt()
                val colsA = scanner.nextInt()

                println("Enter first matrix:")
                val a = Array(rowsA) { Array(colsA) { scanner.nextDouble() } }

                print("Enter size of second matrix: ")
                val rowsB = scanner.nextInt()
                val colsB = scanner.nextInt()

                println("Enter second matrix:")
                val b = Array(rowsB) { Array(colsB) { scanner.nextDouble() } }

                if (colsA != rowsB) {
                    println("The operation cannot be performed.")
                    continue
                }

                val result = Array(rowsA) { Array(colsB) { 0.0 } }

                for (i in 0 until rowsA) {
                    for (j in 0 until colsB) {
                        var sum = 0.0

                        for (k in 0 until colsA) {
                            sum += a[i][k] * b[k][j]
                        }

                        result[i][j] = sum
                    }
                }

                printResult(result)
            }
            "4" -> {
                println(
                    """
                        1. Main diagonal
                        2. Side diagonal
                        3. Vertical line
                        4. Horizontal line
                    """.trimIndent()
                )
                print("Your choice: ")
                val choice = readLine()!!

                print("Enter matrix size: ")
                val nA = scanner.nextInt()
                val mA = scanner.nextInt()

                println("Enter matrix:")
                val a = Array(nA) { Array(mA) { scanner.nextDouble() } }

                val r = Array(mA) { Array(nA) { 0.0 } }

                when (choice) {
                    "1" -> {
                        // transpose by main diagonal
                        for (i in 0 until nA) {
                            for (j in 0 until mA) {
                                r[j][i] = a[i][j]
                            }
                        }
                    }
                    "2" -> {
                        // transpose by side diagonal
                        for (i in 0 until nA) {
                            for (j in 0 until mA) {
                                r[mA - j - 1][nA - i - 1] = a[i][j]
                            }
                        }
                    }
                    "3" -> {
                        // transpose by vertical line
                        for (i in 0 until nA) {
                            for (j in 0 until mA) {
                                r[i][mA - j - 1] = a[i][j]
                            }
                        }
                    }
                    "4" -> {
                        // transpose by horizontal line
                        for (i in 0 until nA) {
                            for (j in 0 until mA) {
                                r[nA - i - 1][j] = a[i][j]
                            }
                        }
                    }
                }

                printResult(r)
            }
            "5" -> {
                print("Enter matrix size: ")
                val nA = scanner.nextInt()
                val mA = scanner.nextInt()

                println("Enter matrix:")
                val a = Array(nA) { Array(mA) { scanner.nextDouble() } }

                println(determinant(a))
            }
            "6" -> {
                print("Enter matrix size: ")
                val nA = scanner.nextInt()
                val mA = scanner.nextInt()

                println("Enter matrix:")
                val a = Array(nA) { Array(mA) { scanner.nextDouble() } }

                val inverted = inverse(a)

                if (inverted != null) {
                    printResult(inverted)
                }
            }
        }
    }
}
