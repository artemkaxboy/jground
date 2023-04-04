package com.artemkaxboy.kground

private class Leet36 {

    fun isValidSudoku(board: Array<CharArray>): Boolean {
        for(i in 0 until 9) {
            if(isLineCorrect(board, i) && isColCorrect(board, i)
                && isSquareCorrect(board, i)) {
                continue
            }
            return false
        }
        return true
    }

    private fun isColCorrect(board: Array<CharArray>, col: Int): Boolean {
        val set = HashSet<Char>()
        for (i in 0 until 9) {
            val ch = board[i][col]
            if (ch == '.') continue
            if (!set.add(ch)) return false
        }
        return true
    }

    private fun isSquareCorrect(board: Array<CharArray>, square: Int): Boolean {
        val set = HashSet<Char>()
        val xOffset = (square % 3) * 3
        val yOffset = square / 3 * 3
        for (i in 0 until 9) {
            val x = i % 3
            val y = i / 3
            val ch = board[yOffset + y][xOffset + x]
            if (ch == '.') continue
            if (!set.add(ch)) return false
        }
        return true
    }

    private fun isLineCorrect(board: Array<CharArray>, line: Int): Boolean {
        val set = HashSet<Char>()
        for (i in 0 until 9) {
            val ch = board[line][i]
            if (ch == '.') continue
            if (!set.add(ch)) return false
        }
        return true
    }
}

fun main() {
    val testCase1 = "[[\"5\",\"3\",\".\",\".\",\"7\",\".\",\".\",\".\",\".\"],[\"6\",\".\",\".\",\"1\",\"9\",\"5\",\".\",\".\",\".\"],[\".\",\"9\",\"8\",\".\",\".\",\".\",\".\",\"6\",\".\"],[\"8\",\".\",\".\",\".\",\"6\",\".\",\".\",\".\",\"3\"],[\"4\",\".\",\".\",\"8\",\".\",\"3\",\".\",\".\",\"1\"],[\"7\",\".\",\".\",\".\",\"2\",\".\",\".\",\".\",\"6\"],[\".\",\"6\",\".\",\".\",\".\",\".\",\"2\",\"8\",\".\"],[\".\",\".\",\".\",\"4\",\"1\",\"9\",\".\",\".\",\"5\"],[\".\",\".\",\".\",\".\",\"8\",\".\",\".\",\"7\",\"9\"]]"
    doWork(testCase1)

    val testCase2 = "[[\"8\",\"3\",\".\",\".\",\"7\",\".\",\".\",\".\",\".\"],[\"6\",\".\",\".\",\"1\",\"9\",\"5\",\".\",\".\",\".\"],[\".\",\"9\",\"8\",\".\",\".\",\".\",\".\",\"6\",\".\"],[\"8\",\".\",\".\",\".\",\"6\",\".\",\".\",\".\",\"3\"],[\"4\",\".\",\".\",\"8\",\".\",\"3\",\".\",\".\",\"1\"],[\"7\",\".\",\".\",\".\",\"2\",\".\",\".\",\".\",\"6\"],[\".\",\"6\",\".\",\".\",\".\",\".\",\"2\",\"8\",\".\"],[\".\",\".\",\".\",\"4\",\"1\",\"9\",\".\",\".\",\"5\"],[\".\",\".\",\".\",\".\",\"8\",\".\",\".\",\"7\",\"9\"]]"
    doWork(testCase2)
}

private fun doWork(data: String) {
    val solution = Leet36()

    val prepared = data.split("],[").map { LeetUtils.stringToCharArray(it) }.toTypedArray()
    val result = solution.isValidSudoku(prepared)

    println("Data: $data")
    println("Result: $result\n")
}
