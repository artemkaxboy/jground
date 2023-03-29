package com.artemkaxboy.kground

import java.util.TreeMap
import kotlin.system.measureTimeMillis

/**
 * You have planned some train traveling one year in advance. The days of the year in which you will travel are given
 * as an integer array days. Each day is an integer from 1 to 365.
 *
 * Train tickets are sold in three different ways:
 *
 * a 1-day pass is sold for costs[0] dollars,
 * a 7-day pass is sold for costs[1] dollars, and
 * a 30-day pass is sold for costs[2] dollars.
 * The passes allow that many days of consecutive travel.
 *
 * For example, if we get a 7-day pass on day 2, then we can travel for 7 days: 2, 3, 4, 5, 6, 7, and 8.
 * Return the minimum number of dollars you need to travel every day in the given list of days.
 */
class Leet983 {

    private val map = TreeMap<Int, Int>()

    fun mincostTickets(days: IntArray, costs: IntArray): Int {

        (listOf(0) + days.asList()).zipWithNext().forEach { (prev, it) -> calcFor(prev, it, costs) }
        println(map)
        return findLowestValueAfterKey(days.last())
    }

    private fun calcFor(prevDay: Int, day: Int, costs: IntArray) {
        val offset = findLowestValueAfterKey(prevDay)

        map[day + 1] = minOfNullableMax(map[day + 1], offset + costs[0])
        map[day + 7] = minOfNullableMax(map[day + 7], offset + costs[1])
        map[day + 30] = minOfNullableMax(map[day + 30], offset + costs[2])
    }

    private fun minOfNullableMax(a: Int?, b: Int) = minOf(a ?: Int.MAX_VALUE, b)

    private fun findLowestValueAfterKey(keyGt: Int): Int {
        return minOrNull(map.filterKeys { key -> key > keyGt }.values) ?: 0
    }

    private fun minOrNull(iterable: Iterable<Int>): Int? {
        val iterator = iterable.iterator()
        if (!iterator.hasNext()) return null

        var min = Int.MAX_VALUE
        while (iterator.hasNext()) {
            val candidate = iterator.next()
            if (candidate < min) min = candidate
        }
        return min
    }

}

fun main() {
    measureTimeMillis {
//        println(Leet983().mincostTickets(intArrayOf(1, 4, 6, 7, 8, 20), intArrayOf(2, 7, 15))) // 11
        println(Leet983().mincostTickets(intArrayOf(1, 4, 6, 7, 8, 20), intArrayOf(7, 2, 15))) // 6
//        println(Leet983().mincostTickets(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 30, 31), intArrayOf(2, 7, 15))) // 17
//        println(
//            Leet983().mincostTickets(
//                intArrayOf(1, 4, 6, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 27, 28),
//                intArrayOf(3, 13, 45)
//            )
//        ) // 44
//        println(Leet983().mincostTickets(intArrayOf(1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 24, 25, 27, 28, 29, 30, 31, 34, 37, 38, 39, 41, 43, 44, 45, 47, 48, 49, 54, 57, 60, 62, 63, 66, 69, 70, 72, 74, 76, 78, 80, 81, 82, 83, 84, 85, 88, 89, 91, 93, 94, 97, 99), intArrayOf(9, 38, 134))) // ??
//        println(Leet983().mincostTickets(intArrayOf(2, 3, 5, 6, 7, 8, 9, 10, 11, 17, 18, 19, 23, 26, 27, 29, 31, 32, 33, 34, 35, 36, 38, 39, 40, 41, 42, 43, 44, 45, 47, 51, 54, 55, 57, 58, 64, 65, 67, 68, 72, 73, 74, 75, 77, 78, 81, 86, 87, 88, 89, 91, 93, 94, 95, 96, 98, 99), intArrayOf(5, 24, 85))) // ??


    }.also { println("Time: ${it}ms") }
}
