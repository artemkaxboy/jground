package com.artemkaxboy.kground

private class Leet2 {

    fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
        val holder = ListNode(0)
        var e1 = l1
        var e2 = l2
        var prevSum: ListNode? = holder
        var overflow = false

        do {
            val l1Val = e1?.`val`
            val l2Val = e2?.`val`
            val sum = (l1Val ?: 0) + (l2Val ?: 0) + if (overflow) 1 else 0
            overflow = sum > 9
            val resultSum = if (overflow) sum - 10 else sum

            prevSum?.next = ListNode(resultSum)
            prevSum = prevSum?.next

            e1 = e1?.next
            e2 = e2?.next
        } while (e1 != null || e2 != null || overflow)

        return holder.next
    }

}

private data class ListNode(
    val `val`: Int,
    var next: ListNode? = null
)

fun main() {
    doWork()
}

private fun doWork() {
    val solution = Leet2()

    val l1 = ListNode(2, ListNode(4, ListNode(3)))
    val l2 = ListNode(5, ListNode(6, ListNode(4)))
    val result = solution.addTwoNumbers(l1, l2)

    println("Result: $result\n")
}
