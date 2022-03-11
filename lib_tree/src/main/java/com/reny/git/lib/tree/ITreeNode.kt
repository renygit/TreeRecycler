package com.reny.git.lib.tree

/**
 * 描述：
 * 作者：reny
 * 时间：2022/3/10 11:01
 */
interface ITreeNode {

    fun getRootList(): ArrayList<TreeNode>
    fun getAllList(): ArrayList<TreeNode>
    fun firstMakeBean(node: TreeNode) //第一次遍历到实体时  可以让用户设置一些初始值 避免用户多次遍历

    //同级目录下可能有 叶子节点和树 同时存在
    fun getChildrenLeaf(): List<out TreeNode>? //获取非树结构的孩子
    fun getChildrenTree(): List<out TreeNode>? //获取树结构的孩子

    //主动点击列表中一个Node 展开或收缩（增添数据）
    fun onClickNode(listNode: ArrayList<TreeNode>, position: Int, callBack:((TreeNode)->Unit)? = null)

    fun parentOf(dest: TreeNode?): Boolean //判断当前节点是否是dest的父亲节点
    fun childOf(dest: TreeNode?): Boolean //判断当前节点是否是dest的孩子节点

    fun leafInFront(): Boolean //如果同级中既有叶子又有树 是否先添加叶子
    fun getLevel(): Int

}