package machine

import java.lang.Exception

enum class Coffee(val water: Int, val milk: Int, val beans: Int, val cost: Int) {
    ESPRESSO(250, 0, 16, 4),
    LATTE(350, 75, 20, 7),
    CAPPUCCINO(200, 100, 12, 6);
}

class CoffeeMachine {
    var water = 400
    var milk = 540
    var beans = 120
    var cups = 9
    var money = 550

    fun action(name: String): Boolean {
        when (name) {
            "buy" -> buy()
            "fill" -> fill()
            "take" -> take()
            "remaining" -> printState()
            "exit" -> return false
        }
        return true
    }

    private fun printState() {
        println(
            """
            The coffee machine has:
            $water of water
            $milk of milk
            $beans of coffee beans
            $cups of disposable cups
            $$money of money
        """.trimIndent()
        )
    }

    private fun buy() {
        print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")

        val coffee = when (readLine()!!) {
            "1" -> Coffee.ESPRESSO
            "2" -> Coffee.LATTE
            "3" -> Coffee.CAPPUCCINO
            "back" -> return
            else -> throw Exception("Unknown choice.")
        }

        val stockAfter = listOf(
            Pair(water - coffee.water, "water"),
            Pair(milk - coffee.milk, "milk"),
            Pair(beans - coffee.beans, "coffee beans"),
            Pair(cups - 1, "cups"),
        )

        if (stockAfter.all { it.first >= 0 }) {
            println("I have enough resources, making you a coffee!")

            water -= coffee.water
            milk -= coffee.milk
            beans -= coffee.beans
            cups--
            money += coffee.cost
        } else {
            println("Sorry, not enough ${stockAfter.first { it.first < 0 }.second}!")
        }

    }

    private fun fill() {
        print("Write how many ml of water do you want to add: ")
        water += readLine()!!.toInt()

        print("Write how many ml of milk do you want to add: ")
        milk += readLine()!!.toInt()

        print("Write how many grams of coffee beans do you want to add: ")
        beans += readLine()!!.toInt()

        print("Write how many disposable cups of coffee do you want to add: ")
        cups += readLine()!!.toInt()
    }

    private fun take() {
        println("I gave you $$money")
        money = 0
    }

}

fun main() {
    val machine = CoffeeMachine()
    while (true) {
        print("Write action (buy, fill, take, remaining, exit): ")
        if (!machine.action(readLine()!!)) {
            break
        }
    }
}
