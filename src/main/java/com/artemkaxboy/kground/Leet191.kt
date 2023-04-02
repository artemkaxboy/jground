package com.artemkaxboy.kground

private class Leet191 {

    fun hammingWeight(n: Int): Int {

        return Integer.toUnsignedString(n, 2).count { it == '1' }
    }
}

fun main() {
    val testCase1 = "00000000000000000000000000001011" // 3
    doWork(testCase1)

    val testCase2 = "00000000000000000000000010000000" // 1
    doWork(testCase2)

    val testCase3 = "11111111111111111111111111111101" // 31
    doWork(testCase3)

}

private fun doWork(data: String) {
    val solution = Leet191()

    val prepared = data.toLong(2).toInt()
    val result = solution.hammingWeight(prepared)

    println("Data: $data")
    println("Result: $result\n")
}
