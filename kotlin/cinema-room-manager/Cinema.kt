fun main() {
    println("Enter the number of rows:")
    val rows: Int = readLine()!!.toInt()

    println("Enter the number of seats in each row:")
    val cols: Int = readLine()!!.toInt()

    val totalSeats = rows * cols
    val cinema: Array<Array<String>> = Array(rows) { Array(cols) { "S" } }
    var numberOfPurchasedTickets = 0
    var currentIncome = 0

    val totalIncome: Int = if (totalSeats <= 60) {
        // If the total number of seats in the screen room is not more than 60,
        // then the price of each ticket is 10 dollars.
        totalSeats * 10

    } else {
        // In a larger room the, the tickets are 10 dollars for the front half of the rows
        // and 8 dollars for the back half.
        if (rows % 2 == 1) {
            ((rows / 2 + 1) * 8 + rows / 2 * 10) * cols
        } else {
            (rows / 2) * (8 + 10) * cols
        }
    }

    while (true) {
        println("1. Show the seats")
        println("2. Buy a ticket")
        println("3. Statistics")
        println("0. Exit")

        val command: String = readLine()!!

        if (command == "0") {
            break
        } else if (command == "1") {
            // print seating arrangement
            println("Cinema:")

            for (i in 0..cinema.size) {
                for (j in 0..cinema[0].size) {
                    if (i == 0 && j == 0) {
                        print(' ')
                    } else if (i == 0) {
                        print(j)
                    } else if (j == 0) {
                        print(i)
                    } else {
                        print(cinema[i - 1][j - 1])
                    }
                    if (j < cinema[0].size) {
                        print(' ')
                    }
                }
                print('\n')
            }
        } else if (command == "2") {
            var row: Int
            var col: Int

            while (true) {
                println("Enter a row number:")
                row = readLine()!!.toInt()

                println("Enter a seat number in that row:")
                col = readLine()!!.toInt()

                if (row < 1 || row > rows || col < 1 || col > cols) {
                    println("Wrong input!")
                } else if (cinema[row - 1][col - 1] == "B") {
                    println("That ticket has already been purchased!")
                } else {
                    break
                }
            }

            // mark the chosen seat by the B symbol
            cinema[row - 1][col - 1] = "B"

            numberOfPurchasedTickets++

            val ticketPrice: Int = if (totalSeats <= 60) {
                10
            } else {
                if (row > rows / 2) {
                    8
                } else {
                    10
                }
            }

            currentIncome += ticketPrice

            println("Ticket price: $$ticketPrice")

        } else if (command == "3") {
            // print statistics
            val percentage = String.format("%.2f", numberOfPurchasedTickets.toDouble() / totalSeats.toDouble() * 100.0)

            println("Number of purchased tickets: $numberOfPurchasedTickets")
            println("Percentage: $percentage%")
            println("Current income: $$currentIncome")
            println("Total income: $$totalIncome")
        } else {
            println("Unknown command.")
        }
    }
}
