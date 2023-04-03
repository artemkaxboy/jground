package com.artemkaxboy.kground

private class Leet11 {
    fun myAtoi(s: String): Int {
        var index = 0
        var negative = false
        val length = s.length

        while (index < length && s[index] == ' ') {
            index++
        }
        if (index >= length)
            return 0

        if (s[index] == '-') {
            negative = true
            index++
        } else if (s[index] == '+') {
            index++
        }

        var acc = 0L
        var digits = 0
        while (index < length && s[index].toInt() >= 0x30 && s[index].toInt() <= 0x39) {
            acc *= 10
            acc += s[index].toInt() - 0x30
            index++
            if (acc == 0L) continue
            digits++
            if (digits > 10) break
        }

        val signedAcc = if (negative) -acc else acc
        val result = if (signedAcc < Int.MIN_VALUE) {
            Int.MIN_VALUE
        } else if (signedAcc > Int.MAX_VALUE) {
            Int.MAX_VALUE
        } else {
            signedAcc.toInt()
        }

        println("signedAcc: $signedAcc")
        println("result: $result")
        println("negative: $negative")

        return result
    }
}

fun main() {
    val testCase1 = "-+12" // "" " " "9223372036854775808" "  0000000000012345678"
    doWork(testCase1)
}

private fun doWork(data: String) {
    val solution = Leet11()

    val result = solution.myAtoi(data)

    println("Data: $data")
    println("Result: $result\n")
}
