package com.artemkaxboy.kground

private class Leet6 {
    fun convert(s: String, numRows: Int): String {

        val loopLength = maxOf(1, (numRows - 1) * 2)
        val builders = (0 until numRows).map { StringBuffer() }
        for (i in s.indices) {
            val order = i % loopLength
            val zOrder = if (order >= numRows) loopLength - order else order
            builders[zOrder].append(s[i])
        }
        val buffer = StringBuffer(s.length)
        builders.forEach { buffer.append(it.toString()) }
        return buffer.toString()
    }
}

fun main() {
    val testCase1 = "PAYPALISHIRING" to 3
    doWork(testCase1)

    val testCase2 = "PAYPALISHIRING" to 4
    doWork(testCase2)

    val testCase3 = "A" to 1
    doWork(testCase3)
}

private fun doWork(data: Pair<String, Int>) {
    val solution = Leet6()

    val result = solution.convert(data.first, data.second)

    println("Data: $data")
    println("Result: $result\n")
}
