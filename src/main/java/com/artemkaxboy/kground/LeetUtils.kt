package com.artemkaxboy.kground

object LeetUtils {

    fun stringToIntArray(string: String): IntArray {
        return string.trim('[', ']').split(",").map { it.toInt() }.toIntArray()
    }

    fun stringToCharArray(string: String): CharArray {
        return string.trim('[', ']').split(",").map { it[0] }.toCharArray()
    }
}
