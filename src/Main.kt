package machine
import kotlin.system.exitProcess


class Machine {
    enum class CoffeeType(val id: Int, val water: Float, val milk: Float,
                          val coffeeBeans: Float, val cups: Int,
                          val price: Double) {
        ESPRESSO(1, 250f, 0f, 16f, 1, 4.0),
        LATTE(2, 350f, 75f, 20f, 1, 7.0),
        CAPPUCCINO(3, 200f, 100f, 12f, 1, 6.0),
        NULL(-1, 0f, 0f, 0f, 0, 0.0);

        companion object {
            fun findById(id: Int): CoffeeType {
                for (enum in values()) {
                    if (id == enum.id) return enum
                }
                return NULL
            }
        }
    }

    enum class State {
        CHOOSE_ACTION_STATE, CHOOSE_COFFEE_STATE,
        FILL_WATER, FILL_MILK, FILL_COFFEE_BEANS, FILL_CUPS
    }

    private var currentWater = 400f
    private var currentMilk = 540f
    private var currentBeans = 120f
    private var numberOfCups = 9
    private var balance = 550.0
    private var actionsStack = mutableMapOf<State, (String) -> Unit>()
    private var currentState = State.CHOOSE_ACTION_STATE

    init {
        actionsStack[State.CHOOSE_ACTION_STATE] = {
            when(it) {
                "buy" -> {
                    currentState = State.CHOOSE_COFFEE_STATE
                    askForCoffee()
                }
                "fill" -> {
                    currentState = State.FILL_WATER
                    askForWater()
                }
                "remaining" -> {
                    printRemaining()
                    askForAction()
                }
                "take" -> {
                    takeMoney()
                    askForAction()
                }
                "exit" -> {
                    exitProcess(0)
                }
            }
        }
        actionsStack[State.CHOOSE_COFFEE_STATE] = {
            when {
                it == "back" -> {
                    currentState = State.CHOOSE_ACTION_STATE
                    askForAction()
                }
                it.toIntOrNull() != null -> {
                    val coffee = CoffeeType.findById(it.toInt())
                    if (coffee != CoffeeType.NULL && canBuyCoffee(coffee)){
                        buyCoffee(coffee)
                        currentState = State.CHOOSE_ACTION_STATE
                        askForAction()
                    } else {
                        currentState = State.CHOOSE_ACTION_STATE
                        askForAction()
                    }

                }
            }
        }
        actionsStack[State.FILL_WATER] = {
            if(it.toFloatOrNull() != null) {
                currentWater += it.toFloat()
                currentState = State.FILL_MILK
                askForMilk()
            }
        }
        actionsStack[State.FILL_MILK] = {
            if(it.toFloatOrNull() != null) {
                currentMilk += it.toFloat()
                currentState = State.FILL_COFFEE_BEANS
                askForCoffeeBeans()
            }
        }
        actionsStack[State.FILL_COFFEE_BEANS] = {
            if(it.toFloatOrNull() != null) {
                currentBeans += it.toFloat()
                currentState = State.FILL_CUPS
                askForCups()
            }
        }
        actionsStack[State.FILL_CUPS] = {
            if(it.toIntOrNull() != null) {
                numberOfCups += it.toInt()
                currentState = State.CHOOSE_ACTION_STATE
                askForAction()
            }
        }

        askForAction()
    }

    fun input(arg: String) {
        actionsStack[currentState]?.invoke(arg)
    }

    private fun printRemaining() {
        println("The coffee machine has:\n" +
                "${currentWater.toInt()} of water\n" +
                "${currentMilk.toInt()} of milk\n" +
                "${currentBeans.toInt()} of coffee beans\n" +
                "$numberOfCups of disposable cups\n" +
                "${balance.toInt()} of money\n")
    }

    private fun askForAction() {
        println("Write action (buy, fill, take, remaining, exit): ")
    }

    private fun askForCoffee() {
        println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
    }

    private fun buyCoffee(coffee: CoffeeType) {
        currentWater -= coffee.water
        currentMilk -= coffee.milk
        currentBeans -= coffee.coffeeBeans
        numberOfCups -= coffee.cups
        balance += coffee.price
        println("I have enough resources, making you a coffee!")
    }

    private fun canBuyCoffee(coffee: CoffeeType): Boolean {
        return when {
            currentWater - coffee.water < 0 -> {
                println("Sorry, not enough water!")
                return false
            }
            currentMilk - coffee.milk < 0 -> {
                println("Sorry, not enough milk!")
                return false
            }
            currentBeans - coffee.coffeeBeans < 0 -> {
                println("Sorry, not enough coffee beans!")
                return false
            }
            numberOfCups - coffee.cups < 0 -> {
                println("Sorry, not enough disposable cups!")
                return false
            }
            else -> true
        }
    }

    private fun takeMoney() {
        println("I gave you \$${balance.toInt()}")
        balance = 0.0
    }

    private fun askForWater() {
        println("Write how many ml of water do you want to add:")
    }

    private fun askForMilk() {
        println("Write how many ml of milk do you want to add:")
    }

    private fun askForCoffeeBeans() {
        println("Write how many grams of coffee beans do you want to add:")
    }

    private fun askForCups() {
        println("Write how many disposable cups of coffee do you want to add:")
    }


}

fun main() {
    val machine = Machine()
    while (true) {
        machine.input(readLine()!!)
    }
}
