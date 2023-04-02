package com.artemkaxboy.kground

object LeetUtils {

    fun stringToIntArray(string: String): IntArray {
        return string.trim('[', ']').split(",").map { it.toInt() }.toIntArray()
    }
}
