package com.artemkaxboy.kground

private class Leet1 {
    fun twoSum(nums: IntArray, target: Int): IntArray {
        for (i in nums.indices) {
            getPairForSum(nums, i, target)
                ?.let { return intArrayOf(i, it) }
        }
        return intArrayOf()
    }

    private fun getPairForSum(nums: IntArray, currentIndex: Int, target: Int): Int? {
        val first = nums[currentIndex]
        for (i in (currentIndex + 1) until nums.size) {
            if (target - first == nums[i]) return i
        }
        return null
    }
}

fun main() {
    val case1 = "[2,7,11,15]" to 9  // 0,1
    val case2 = "[3,2,4]" to 6      // 1,2
    val case3 = "[3,3]" to 6        // 0,1
//    doWork(case1)
    doWork(case2)
//    doWork(case3)
}

private fun doWork(case: Pair<String, Int>) {
    val solution = Leet1()

    val (numsString, target) = case
    val nums = numsString.trim('[', ']').split(",").map { it.toInt() }.toIntArray()
    val result = solution.twoSum(nums, target).joinToString()
    println("Result = $result\n")
}
