package calculator

import java.lang.Exception
import java.math.BigInteger

class CalculatorException(message: String) : Exception(message)

class Stack<T> {
    private val elements: MutableList<T> = mutableListOf()
    fun isEmpty() = elements.isEmpty()
    fun push(element: T) = elements.add(element)
    fun pop(): T? = elements.removeLastOrNull()
    fun peek(): T? = elements.lastOrNull()
}

val variables: MutableMap<String, BigInteger> = mutableMapOf()
val operatorPrecedences: Map<Char, Int> = mapOf(
    '*' to 1,
    '/' to 1,
    '+' to 0,
    '-' to 0
)
val operators = operatorPrecedences.keys

fun simplifyOperatorSequences(s: String): String {
    var result = s
    var isSequence = false
    var start = -1

    var cursor = 0

    while (cursor != result.length) {
        if (result[cursor] == '-' || result[cursor] == '+') {
            if (!isSequence) {
                // start of a new sequence
                start = cursor
                isSequence = true
            }
        } else {
            if (isSequence) {
                // end of a sequence
                val sequenceLength = cursor - start
                val isSequenceOfMinus = result[cursor - 1] == '-'

                result = if (isSequenceOfMinus && sequenceLength % 2 == 0) {
                    // replace the whole sequence with '+'
                    result.replaceRange(start, cursor, "+")
                } else {
                    result.removeRange(start + 1, cursor)
                }
                // move the cursor back by the count of removed characters
                cursor = start + 1
                isSequence = false
            }
        }
        cursor++
    }

    return result
}

fun isValidVariableName(s: String): Boolean {
    return s.all { it in 'a'..'z' || it in 'A'..'Z' }
}

fun convertExpressionFromInfixToPostfix(s: String): List<String> {
    val output: MutableList<String> = mutableListOf()
    val operatorStack: Stack<Char> = Stack()
    var idx = 0

    while (idx <= s.lastIndex) {
        // read a token
        val c = s[idx]
        idx++

        if (c.isWhitespace()) {
            continue
        }

        if (c in operators) {
            // if the token is an operator
            val incomingOperatorPrecedence = operatorPrecedences[c]!!
            var operatorOnTop = operatorStack.peek()

            if (
                operatorStack.isEmpty() ||
                operatorStack.peek() == '(' ||
                incomingOperatorPrecedence > operatorPrecedences[operatorOnTop]!!
            ) {
                // if the stack is empty or contains left parenthesis on top
                // push the incoming operator onto the stack
                // if the incoming operator has higher precedence than the top of the stack,
                // push it on the stack
                operatorStack.push(c)
            } else {
                // if the incoming operator has lower or equal precedence than or to the top of the stack,
                // pop the stack and add operators to the output an operator that has a smaller precedence
                // or a left parenthesis on the top of the stack is reached

                while (operatorOnTop != null && operatorOnTop != '(') {

                    if (incomingOperatorPrecedence <= operatorPrecedences[operatorOnTop]!!) {
                        output.add(operatorStack.pop().toString())
                        operatorOnTop = operatorStack.peek()
                    } else {
                        break
                    }
                }

                // push the current operator onto the stack
                operatorStack.push(c)
            }

        } else if (c == '(') {
            // if the token is a left bracket push it onto the stack
            operatorStack.push('(')
        } else if (c == ')') {
            // if the token is a right bracket
            // while there isn't a left bracket at the top of the stack
            // pop operators from the stack onto the output queue
            while (operatorStack.peek() != '(') {
                output.add(operatorStack.pop().toString())
                if (operatorStack.isEmpty()) {
                    throw CalculatorException("Missing opening bracket.")
                }
            }
            // pop the left bracket from the stack
            operatorStack.pop()
        } else if (isValidVariableName(c.toString())) {
            idx--
            var name = ""
            var char = c

            while (isValidVariableName(char.toString())) {
                name += char
                idx++
                if (idx > s.lastIndex) {
                    break
                }
                char = s[idx]
            }
            output.add(name)
        } else {
            idx--
            // if the token is a number, then add it to the output queue
            var number = ""
            var digit = c

            while (digit.isDigit()) {
                number += digit
                idx++
                if (idx > s.lastIndex) {
                    break
                }
                digit = s[idx]
            }

            output.add(number)
        }
    }

    // while there are operators on the stack, pop them to the queue
    while (!operatorStack.isEmpty()) {
        val operator = operatorStack.pop().toString()
        if (operator == "(") {
            throw CalculatorException("Unclosed opening bracket.")
        }
        output.add(operator)
    }

    return output
}

fun calculateResultFromPostfixNotation(tokens: List<String>): String {
    val stack: Stack<String> = Stack()

    for (token in tokens) {
        when {
            token.all { it.isDigit() } -> {
                // if the token is a number, push it onto the stack
                stack.push(token)
            }
            isValidVariableName(token) -> {
                // if the token is the name of a variable, push its value onto the stack
                val value = variables[token] ?: throw CalculatorException("Unknown variable")
                stack.push(value.toString())
            }
            operators.contains(token.first()) -> {
                // if the token is an operator, then pop twice to get two numbers
                // and perform the operation
                val a = stack.pop() ?: throw CalculatorException("value expected")
                val b = stack.pop() ?: throw CalculatorException("value expected")

                val bigIntA = a.toBigInteger()
                val bigIntB = b.toBigInteger()

                val result = when (token) {
                    "+" -> bigIntB + bigIntA
                    "-" -> bigIntB - bigIntA
                    "*" -> bigIntB * bigIntA
                    "/" -> b.toBigDecimal() / a.toBigDecimal()
                    else -> throw CalculatorException("Unknown operator")
                }
                // push the result on to the stack
                stack.push(result.toString())
            }
        }
    }

    // when the expression ends, the number on the top of the stack is the final result
    return stack.pop() ?: throw CalculatorException("result expected")
}

fun main() {
    while (true) {
        val input = readLine()!!
        when {
            input == "" -> {
                continue
            }
            input.first() == '/' -> {
                when (input) {
                    "/exit" -> {
                        println("Bye!")
                        return
                    }
                    "/help" -> {
                        println(
                            """
                            The program calculates expressions like these: 4 + 6 - 8, 2 - 3 - 4, and so on.
                            It supports both unary, binary minus operators and several same operators following each other.
                            Even number of minuses gives a plus, and the odd number of minuses gives a minus!
                        """.trimIndent()
                        )
                    }
                    else -> println("Unknown command")
                }
            }
            "=" in input -> {
                if (input.count { it == '=' } > 1) {
                    println("Invalid assignment")
                    continue
                }

                val (identifier, assignment) = input.split("=").map { token -> token.trim() }

                if (isValidVariableName(identifier)) {

                    if (isValidVariableName(assignment)) {
                        val value = variables[assignment]

                        if (value != null) {
                            variables[identifier] = value
                        } else {
                            println("Unknown variable")
                        }
                    } else {
                        try {
                            variables[identifier] = assignment.toBigInteger()
                        } catch (e: NumberFormatException) {
                            println("Invalid assignment")
                        }
                    }
                } else {
                    println("Invalid identifier")
                }
            }
            isValidVariableName(input) -> {
                val value = variables[input]
                if (value == null) {
                    println("Unknown variable")
                } else {
                    println(value)
                }
            }
            else -> {
                if ("**" in input || "//" in input) {
                    println("Invalid expression")
                    continue
                }

                try {
                    // if the input is a single number, output it directly
                    println(input.toInt())
                } catch (e: java.lang.NumberFormatException) {
                    try {
                        println(
                            calculateResultFromPostfixNotation(
                                convertExpressionFromInfixToPostfix(
                                    simplifyOperatorSequences(
                                        input
                                    )
                                )
                            )
                        )
                    } catch (e: CalculatorException) {
                        println("Invalid expression")
                    }
                }
            }
        }
    }
}
