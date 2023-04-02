package com.artemkaxboy.kground

import com.artemkaxboy.kground.LeetUtils.stringToIntArray

private class Leet4 {
    fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
        val size1 = nums1.size
        val size2 = nums2.size
        val size = size1 + size2
        val mergedList = ArrayList<Int>(size)
        var i1 = 0
        var i2 = 0

        for (i in 0 until size) {
            val num1 = if (i1 >= size1) Int.MAX_VALUE else nums1[i1]
            val num2 = if (i2 >= size2) Int.MAX_VALUE else nums2[i2]
            val next = if (num1 > num2) {
                i2++
                num2
            } else {
                i1++
                num1
            }

            mergedList.add(next)
        }

        return if (size % 2 == 0) {
            val first = (size / 2) - 1
            val second = first + 1
            return (mergedList[first] + mergedList[second]).toDouble() / 2
        } else {
            mergedList[size / 2].toDouble()
        }
    }
}

fun main() {
    val case1 = "[1,3]" to "[2]" // 2
//    doWork(case1)

    val myCase = "[1,2,3,4,5,6,7]" to "[999,1000,1001]" // 5.5
    doWork(myCase)
}

private fun doWork(data: Pair<String, String>) {
    val solution = Leet4()

    val nums1 = stringToIntArray(data.first)
    val nums2 = stringToIntArray(data.second)

    val result = solution.findMedianSortedArrays(nums1, nums2)
    println("Data: $data")
    println("Result: $result\n")
}
