package com.artemkaxboy.kground

import java.util.concurrent.LinkedTransferQueue

private class Leet3 {
    val chars = LinkedTransferQueue<Char>()

    fun lengthOfLongestSubstring(s: String): Int {
        var length = 0
        var maxLength = 0
        for (i in s) {
            if (chars.contains(i)) {
                var removed: Char
                do {
                    removed = chars.remove()
                } while (removed != i)
                chars.add(i)
                length = chars.size
            } else {
                chars.add(i)
                length++
                if (length > maxLength) maxLength = length
            }
        }
        return maxLength
    }
}

fun main() {
    val case1 = "abcabcbb"
    doWork(case1)
    val case2 = "bbbbb"
    doWork(case2)
    val case3 = "pwwkew"
    doWork(case3)

    val myCase1 = "abcaa"
    doWork(myCase1)

    val wrongAnswer1 = "aab" // 2
    doWork(wrongAnswer1)
}

private fun doWork(data: String) {
    val solution = Leet3()
    val result = solution.lengthOfLongestSubstring(data)
    println("Data: $data")
    println("Result: $result\n")
}
