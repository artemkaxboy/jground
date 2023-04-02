package com.artemkaxboy.kground

import java.util.TreeMap

private class Leet435 {

    val overlaps = TreeMap<Int, Int>()

    fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
        for (k in intervals.indices) {
            isOverlapPrevious(k, intervals)
            println(overlaps)
        }
        return 0
    }

    fun isOverlapPrevious(intervalNumber: Int, intervals: Array<IntArray>) {
        val overlappedIntervals = TreeMap<Int, Int>()
        for (i in 0 until intervalNumber) {
            getOverlappedRange(intervals[intervalNumber], intervals[i])
                ?.let {
                    overlappedIntervals[it.first] = maxOf(overlappedIntervals[it.first] ?: 0, it.second - it.first)
                }
        }
        val mergedOverlappedIntervals = mergeIntervals(overlappedIntervals)
        appendOverlaps(mergedOverlappedIntervals)
    }

    fun getOverlappedRange(range1: IntArray, range2: IntArray): Pair<Int, Int>? {
        val r1s = range1[0]
        val r1e = range1[1] - 1
        val r2s = range2[0]
        val r2e = range2[1] - 1
        if (r1s <= r2e && r1e > r2s) return Pair(maxOf(r1s, r2s), minOf(r1e, r2e))
        if (r2s <= r1e && r2e > r1s) return Pair(maxOf(r1s, r2s), minOf(r1e, r2e))
        return null
    }

    fun appendOverlaps(overlaps: Map<Int, Int>) {
        for (entry in overlaps) {
            appendOverlaps(entry.key, entry.value)
        }
    }

    fun appendOverlaps(start: Int, size: Int) {
        val end = start + size

        val startedValue = overlaps.floorEntry(start).let { (it?.value ?: 0) + 1 }
        overlaps[start] = startedValue

        overlaps.tailMap(start + 1)
            .let { if (start + 1 < end) it.headMap(end) else it }
            .forEach { k, v -> overlaps[k] = v + 1 }

        overlaps.floorEntry(end + 1)?.let { overlaps[end + 1] = it.value - 1 }
    }

    /** start, size */
    fun mergeIntervals(intervals: TreeMap<Int, Int>): TreeMap<Int, Int> {
        val size = intervals.size
        if (size <= 1) return intervals
        var lastEnd = Int.MIN_VALUE
        var lastKey = 0
        val newMap = TreeMap<Int, Int>()
        for (entry in intervals.entries) {
            if (entry.key < lastEnd) newMap[lastKey] = entry.value + newMap[lastKey]!! + (lastKey - entry.key)
            lastEnd = entry.key + entry.value - 1
            lastKey = entry.key
            newMap[entry.key] = entry.value
        }
        return newMap
    }

    fun isOverlapped(range1: IntRange, range2: IntRange): Boolean {
        return range1.first < range2.last && range1.last > range2.first
    }
}

fun main() {
    Leet435().eraseOverlapIntervals(
        arrayOf(
            intArrayOf(1, 10),
            intArrayOf(2, 5),
            intArrayOf(4, 11),
            intArrayOf(1, 4),
        )
    ) // 1
//2, 5    Leet435().eraseOverlapIntervals(
//        arrayOf(
//            intArrayOf(1, 2),
//            intArrayOf(2, 3),
//            intArrayOf(3, 4),
//            intArrayOf(1, 3),
//        )
//    ) // 1
//    Leet435().eraseOverlapIntervals(
//        arrayOf(
//            intArrayOf(1, 2),
//            intArrayOf(1, 2),
//            intArrayOf(1, 2),
//        )
//    ) // 2
//    Leet435().eraseOverlapIntervals(
//        arrayOf(
//            intArrayOf(1, 2),
//            intArrayOf(2, 3),
//        )
//    ) // 0


}
