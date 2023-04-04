package com.artemkaxboy.kground

/**
 * Example:
 * var ti = TreeNode(5)
 * var v = ti.`val`
 * Definition for a binary tree node.
 * class TreeNode(var `val`: Int) {
 *     var left: TreeNode? = null
 *     var right: TreeNode? = null
 * }
 */
private class Leet814 {
    fun pruneTree(root: TreeNode?): TreeNode? {
        return deleteZerosOnly(root)
    }

    private fun deleteZerosOnly(root: TreeNode?): TreeNode? {
        root ?: return null

        // println("val = ${root.`val`}")
        if (root.left != null)
            root.left = deleteZerosOnly(root.left)

        if (root.right != null)
            root.right = deleteZerosOnly(root.right)

        if (root.left == null && root.right == null && root.`val` == 0)
            return null//.also { println("return null") }

        return root//.also { println("return ${root.`val`}") }
    }
}

private class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}
