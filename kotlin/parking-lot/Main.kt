package parking

data class Spot(val number: Int, var car: Car? = null)

data class Car(val registration: String, val color: String)

fun main() {
    var parking: Array<Spot>? = null

    while (true) {
        val command = readLine()!!

        when {
            command.startsWith("create") -> {
                val size = command.split(" ")[1].toInt()
                parking = Array(size) { i -> Spot(i + 1) }
                println("Created a parking lot with $size spots.")
            }
            command == "exit" -> {
                break
            }
            else -> {
                if (parking == null) {
                    println("Sorry, a parking lot has not been created.")
                } else {
                    when {
                        command.startsWith("park") -> {
                            var emptySpotIdx = -1

                            for (i in parking.indices) {
                                if (parking[i].car == null) {
                                    emptySpotIdx = i
                                    break
                                }
                            }

                            if (emptySpotIdx == -1) {
                                println("Sorry, the parking lot is full.")
                            } else {
                                val params = command.split(" ")
                                val car = Car(params[1], params[2])
                                parking[emptySpotIdx].car = car
                                println("${car.color} car parked in spot ${emptySpotIdx + 1}.")
                            }
                        }
                        command.startsWith("leave") -> {
                            val number = command.split(" ")[1].toInt()
                            parking[number - 1].car = null
                            println("Spot $number is free.")
                        }
                        command.startsWith("reg_by_color") -> {
                            val color = command.split(" ")[1]
                            val cars = parking
                                .mapNotNull { it.car }
                                .filter { it.color.equals(color, ignoreCase = true) }

                            if (cars.isEmpty()) {
                                println("No cars with color $color were found.")
                            } else {
                                println(cars.joinToString { it.registration })
                            }
                        }
                        command.startsWith("spot_by_color") -> {
                            val color = command.split(" ")[1]
                            val spots = parking.filter {
                                it.car != null && it.car!!.color.equals(
                                    color,
                                    ignoreCase = true
                                )
                            }

                            if (spots.isEmpty()) {
                                println("No cars with color $color were found.")
                            } else {
                                println(spots.joinToString { it.number.toString() })
                            }
                        }
                        command.startsWith("spot_by_reg") -> {
                            val registration = command.split(" ")[1]
                            val spot =
                                parking.find { it.car != null && it.car!!.registration == registration }

                            if (spot == null) {
                                println("No cars with registration number $registration were found.")
                            } else {
                                println(spot.number)
                            }
                        }
                        command == "status" -> {
                            if (parking.all { it.car == null }) {
                                println("Parking lot is empty.")
                            } else {
                                for (i in parking.indices) {
                                    val car = parking[i].car
                                    if (car != null) {
                                        println("${i + 1} ${car.registration} ${car.color}")
                                    }
                                }
                            }
                        }
                        else -> {
                            println("Unknown command.")
                        }
                    }
                }
            }
        }
    }
}
