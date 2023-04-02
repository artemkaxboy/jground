package com.artemkaxboy.kground

private class Leet190 {

    fun reverseBits(n: Int): Int {

        val rString = Integer.toUnsignedString(n, 2)
            .padStart(32, '0').reversed()
        println(rString)
        return rString.toLong(2).toInt()
    }
}

fun main() {
    val testCase1 = "00000010100101000001111010011100" // 964176192
    doWork(testCase1)

    val testCase2 = "11111111111111111111111111111101" // 3221225471
    doWork(testCase2)

}

private fun doWork(data: String) {
    val solution = Leet190()

    val prepared = data.toLong(2).toInt()
    val result = solution.reverseBits(prepared)

    println("Data: $data")
    println("Result: $result\n")
}
