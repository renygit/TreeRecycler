package com.reny.git.tree.bean

import com.reny.git.lib.tree.TreeNode

data class Exam(
    val id: Int,
    val examName: String,
): TreeNodePlus(){

    override fun getChildrenLeaf(): List<out TreeNode>? = null

    override fun getChildrenTree(): List<out TreeNode>? = null

}