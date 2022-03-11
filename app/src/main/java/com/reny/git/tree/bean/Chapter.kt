package com.reny.git.tree.bean

import com.reny.git.lib.tree.TreeNode

data class Chapter(
    val chapters: List<ChapterX>?,
    val examList: List<Exam>?,
): TreeNodePlus(){

    override fun getChildrenLeaf(): List<out TreeNode>? = examList

    override fun getChildrenTree(): List<out TreeNode>? = chapters

}