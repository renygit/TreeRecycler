package com.reny.git.tree.bean

import com.reny.git.lib.tree.TreeNode

data class ChapterX(
    val id: Int,//因为courseName可能相同 remove时可能出错 需要一个id来区分不同的bean  id不应该有相同的（不应该存在2个完全一样的bean）
    val courseName: String,
    val childList: List<ChapterX>?,
    val examList: List<Exam>?,
): TreeNodePlus(){

    override fun getChildrenLeaf(): List<out TreeNode>? = examList

    override fun getChildrenTree(): List<out TreeNode>? = childList

}