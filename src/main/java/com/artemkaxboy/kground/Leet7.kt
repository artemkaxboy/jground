package com.artemkaxboy.kground

private class Leet7 {
    fun reverse(x: Int): Int {

        val sign = if (x < 0) -1 else 1
        return kotlin.math.abs(x).toString().reversed().toIntOrNull()?.let { it * sign } ?: 0
    }
}

fun main() {
    val testCase1 = 123 // 321
    doWork(testCase1)

    val testCase2 = -123 // -321
    doWork(testCase2)

    val testCase3 = 120 // 21
    doWork(testCase3)

    val myCase1 = 1999999999 // 0
    doWork(myCase1)

    val myCase2 = -1999999999 // 0
    doWork(myCase2)
}

private fun doWork(data: Int) {
    val solution = Leet7()

    val result = solution.reverse(data)

    println("Data: $data")
    println("Result: $result\n")
}
