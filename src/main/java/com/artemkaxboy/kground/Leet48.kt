package com.artemkaxboy.kground

private class Leet48 {
    fun rotate(matrix: Array<IntArray>) {
        val size = matrix.size
        for (layerNumber in 0 until size / 2) {
            rotateLayer(matrix, layerNumber)
        }
    }

    fun rotateLayer(matrix: Array<IntArray>, layerNumber: Int) {
        val limit = matrix.size - 1 - layerNumber

        for (element in layerNumber until limit) {
            var holder: Int? = null
            var coord = Coord(element, layerNumber)
            for (i in 0..4) {
                val (newCoord, newHolder) = rotateElement(matrix, coord, holder)
                holder = newHolder
                coord = newCoord
            }
        }
    }

    fun rotateElement(matrix: Array<IntArray>, element: Coord, holder: Int?): Pair<Coord, Int?> {

        val newHolder = matrix[element.y][element.x]
        holder?.let { matrix[element.y][element.x] = it }
        return element.rotate(matrix.size) to newHolder
    }

    data class Coord(val x: Int, val y: Int) {

        fun rotate(size: Int): Coord {
            return Coord(size - 1 - y, x)
        }
    }
}

fun main() {
    val testCase1 = "[[1,2,3],[4,5,6],[7,8,9]]"
    doWork(testCase1)

    val myCase = "[[1,2,3,4],[1,2,3,4],[1,2,3,4],[1,2,3,4]]"
    doWork(myCase)

    val myCase2 = "[[1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9]," +
            "[1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9]]"
    doWork(myCase2)
}

private fun doWork(data: String) {
    val solution = Leet48()

    val prepared = data.split("],[").map { LeetUtils.stringToIntArray(it) }.toTypedArray()

    println("Data: \n${prepared.joinToString("\n") { it.joinToString() }}")
    solution.rotate(prepared)
    println("Data: \n${prepared.joinToString("\n") { it.joinToString() }}")
}
