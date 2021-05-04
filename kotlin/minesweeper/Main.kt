package minesweeper

data class Cell(
    val i: Int,
    val j: Int,
    var isMine: Boolean = false,
    var isMarked: Boolean = false,
    var isExplored: Boolean = false,
    var countOfMinesAround: Int = 0
)

enum class Status {
    WON, LOST, IN_PROGRESS
}

class Game {
    private val size = 9
    private var mineCount = 0
    private var minesPlaced = false
    private val cells = Array(size) { i -> Array(size) { j -> Cell(i, j) } }
    private var status = Status.IN_PROGRESS

    init {
        println("How many mines do you want on the field?")
        mineCount = readLine()!!.toInt()

        println(this)

        while (status == Status.IN_PROGRESS) {
            println("Set/unset mines marks or claim a cell as free:")
            val input = readLine()!!.split(" ")
            if (input.size == 3) {
                val (column, row, moveType) = input
                makeMove(column.toInt(), row.toInt(), moveType)
            } else {
                println("The move should consist of: column row moveType")
            }
        }
    }

    private fun neighbours(cell: Cell): List<Cell> {
        val neighbours = mutableListOf<Cell>()

        val deltas = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1),
        )

        for (delta in deltas) {
            val i = cell.i + delta.first
            val j = cell.j + delta.second

            if (i in 0 until size && j in 0 until size) {
                neighbours.add(cells[i][j])
            }
        }

        return neighbours.toList()
    }

    private fun safeCells(): List<Cell> {
        return cells.flatten().filter { !it.isMine }
    }

    private fun mineCells(): List<Cell> {
        return cells.flatten().filter { it.isMine }
    }

    private fun placeMines(cellToExclude: Cell) {
        repeat(mineCount) {
            safeCells().filter { it != cellToExclude }.random().isMine = true
        }

        for (cell in mineCells()) {
            for (neighbour in neighbours(cell)) {
                neighbour.countOfMinesAround++
            }
        }

        minesPlaced = true
    }

    private fun isWon(): Boolean {
        val allMineCellsAreMarked = mineCells().all { it.isMarked }
        val noSafeCellIsMarked = safeCells().all { !it.isMarked }
        val allSafeCellsAreExplored = safeCells().all { it.isExplored }
        return allMineCellsAreMarked && noSafeCellIsMarked || allSafeCellsAreExplored
    }

    private fun isLost(): Boolean {
        return mineCells().any { it.isExplored }
    }

    private fun determineStatus() {
        if (status == Status.IN_PROGRESS) {
            if (isWon()) {
                status = Status.WON
            } else if (isLost()) {
                status = Status.LOST
            }
        }
    }

    private fun makeMove(column: Int, row: Int, moveType: String) {
        val i = row - 1
        val j = column - 1

        when (moveType) {
            "free" -> explore(cells[i][j])
            "mine" -> mine(cells[i][j])
            else -> println("Unknown move type.")
        }

        determineStatus()

        println(this)

        when (status) {
            Status.WON -> println("Congratulations! You found all the mines!")
            Status.LOST -> println("You stepped on a mine and failed!")
            else -> Unit
        }
    }

    private fun mine(cell: Cell) {
        cell.isMarked = !cell.isMarked
    }

    private fun explore(cell: Cell) {
        // the first cell explored cannot be a mine
        if (!minesPlaced) {
            placeMines(cell)
        }

        cell.isExplored = true
        if (cell.isMine) {
            status = Status.LOST
        } else {
            val neighbours = neighbours(cell)
            // if the cell does not have mines around it, explore all of the unexplored neighbours
            if (neighbours.all { !it.isMine }) {
                for (neighbour in neighbours.filter { !it.isExplored }) {
                    explore(neighbour)
                }
            }
        }
    }

    override fun toString(): String {
        val output = Array(size + 3) { Array(size + 3) { ' ' } }

        for (i in 1..9) {
            output[0][1 + i] = i.toString().first()
            output[1 + i][0] = i.toString().first()
        }

        for (i in 0..output.lastIndex) {
            output[1][i] = '—'
            output[output.lastIndex][i] = '—'
        }

        for (i in 0..output.lastIndex) {
            output[i][1] = '│'
            output[i][output.lastIndex] = '│'
        }

        for (i in 0 until size) {
            for (j in 0 until size) {
                val cell = cells[i][j]

                output[2 + i][2 + j] = when {
                    cell.isMine -> {
                        if (status == Status.LOST) {
                            'X'
                        } else {
                            if (cell.isMarked) {
                                '*'
                            } else {
                                '.'
                            }
                        }
                    }
                    cell.isExplored -> {
                        if (cell.countOfMinesAround == 0) {
                            '/'
                        } else {
                            cell.countOfMinesAround.toString().first()
                        }
                    }
                    cell.isMarked -> {
                        '*'
                    }
                    else -> '.'
                }
            }
        }

        return output.joinToString("\n") { it.joinToString("") }
    }
}

fun main() {
    Game()
}
