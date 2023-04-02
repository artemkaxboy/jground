package com.artemkaxboy.kground

private class Leet5 {
    fun longestPalindrome(s: String): String {
        var longestFound: Pair<Int, Int> = 0 to 0

        for (firstLetterOffset in s.indices) {
            findLongestForSubstring(s, firstLetterOffset, longestFound.second + 1)
                ?.let { longestFound = it }
        }
        return s.drop(longestFound.first).take(longestFound.second)
    }

    private fun findLongestForSubstring(s: String, offset: Int, minPalindromeLength: Int): Pair<Int, Int>? {
        for (lastPossibleLetter in s.length downTo offset + minPalindromeLength) {
            val length = lastPossibleLetter - offset
            if (isPalindrome(s, offset, length))
                return Pair(offset, length)
        }
        return null
    }

    private fun isPalindrome(string: String, offset: Int, length: Int): Boolean {
        val lengthToCheck = length / 2
        for (i in 0 until lengthToCheck) {
            if (string[offset + i] != string[offset + length - 1 - i]) return false
        }
        return true
    }
}

fun main() {
    val testCase1 = "babad"
    doWork(testCase1)

    val testCase2 = "cbbd"
    doWork(testCase2)

    val myCase = "ababa"
    doWork(myCase)

    val myCase2 = "ababacdcdcdc"
    doWork(myCase2)
}

private fun doWork(data: String) {
    val solution = Leet5()

    val result = solution.longestPalindrome(data)

    println("Data: $data")
    println("Result: $result\n")
}
