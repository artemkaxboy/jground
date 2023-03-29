package com.artemkaxboy.kground

class Solution {

    fun minPathSum(grid: Array<IntArray>): Int {
        val lines = grid.size
        val cols = grid[0].size

        (0..lines - 1).forEach { line ->
            (0..cols - 1).forEach { col ->
                grid[line][col] = calc(
                    grid.getOrNull(line - 1)?.getOrNull(col), grid.getOrNull(line)?.getOrNull(col - 1),
                    grid[line][col]
                )
            }
        }

        return grid[lines - 1][cols - 1]
    }

    fun calc(left: Int?, top: Int?, element: Int): Int {
        if (left == null && top == null) return element
        return minOf(left ?: Int.MAX_VALUE, top ?: Int.MAX_VALUE) + element
    }


    fun minPathSumOld(grid: Array<IntArray>): Int {
        val result = (listOf(null) + grid.asList()).reduce { a, b -> calcRow(a, b!!) }
            ?.last()
            ?: 0

        return result
    }

    fun calcRow(preRow: IntArray?, row: IntArray): IntArray {
        preRow ?: return calcFirstRow(row)

        val list = mutableListOf(preRow[0] + row[0])
        row.reduceIndexed { index, acc, i ->
            (minOf(acc + preRow[index - 1], preRow[index]) + i).also { sum ->
                list.add(sum)
            }
        }
        return list.also { println(it) }.toIntArray()
    }

    fun calcFirstRow(row: IntArray): IntArray {
        val list = mutableListOf(row[0])
        row.reduce { acc, i -> (acc + i).also { sum -> list.add(sum) } }
        return list.also { println(it) }.toIntArray()
    }
}

fun main() {
    val input = arrayOf(
        intArrayOf(1, 2, 5),
        intArrayOf(3, 2, 1),
    )
    val output = Solution().minPathSum(input)
    println(output)
}
