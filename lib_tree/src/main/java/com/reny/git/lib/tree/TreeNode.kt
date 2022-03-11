package com.reny.git.lib.tree

abstract class TreeNode : ITreeNode{

    var parent: TreeNode? = null

    var isExpand = false //当前状态是否展开
    var isSelect = false //当前是否选中

    var index = 0 // “同层级”索引

    private var rootNodes: ArrayList<TreeNode>? = null
    private var allNodes: ArrayList<TreeNode>? = null

    // 通常根Node来调用这个方法 构建整颗树
    override fun getRootList(): ArrayList<TreeNode> {
        if(rootNodes == null){
            rootNodes = ArrayList()
        }
        if(allNodes == null){
            getAllList() //虽然getRootList()只获取根List 但是依然要遍历整个树 需要确定好父子关系
        }
        return rootNodes!!
    }

    override fun getAllList(): ArrayList<TreeNode> {
        if(allNodes == null){
            allNodes = getListByNode(this, true)
        }
        return allNodes!!
    }

    private fun getListByNode(node: TreeNode, isRoot: Boolean = false): ArrayList<TreeNode>{
        if(rootNodes == null){
            rootNodes = ArrayList()
        }

        val list: ArrayList<TreeNode> = ArrayList()
        if(leafInFront()){
            addLeafChildren(list, node, isRoot)
        }

        //当前层级下 索引位置
        val levelIndex = list.size

        node.getChildrenTree()?.forEachIndexed { index, treeNode ->
            firstMakeBean(treeNode) //给使用者提供的

            //根节点parent=null
            treeNode.parent = if(isRoot) null else node
            treeNode.index = levelIndex + index

            list.add(treeNode)

            if(isRoot){
                rootNodes?.add(treeNode)
            }

            list.addAll(getListByNode(treeNode))
        }

        if(!leafInFront()){
            addLeafChildren(list, node, isRoot)
        }

        return list
    }

    private fun addLeafChildren(list: ArrayList<TreeNode>, node: TreeNode, isRoot: Boolean = false){
        node.getChildrenLeaf()?.forEachIndexed { index, treeNode ->
            firstMakeBean(treeNode) //给使用者提供的

            //根节点parent=null
            treeNode.parent = if(isRoot) null else node
            treeNode.index = index

            //getChildren 表示的非树结构数据 不用继续遍历

            list.add(treeNode)

            if(isRoot){
                rootNodes?.add(treeNode)
            }
        }
    }

    //重写这个方法 第一次遍历到实体时  可以让用户设置一些初始值 避免用户多次遍历
    override fun firstMakeBean(node: TreeNode) {
        // 例  if(node is XXX) node.isDownload = true
    }

    //控制展开和收缩
    override fun onClickNode(
        listNode: ArrayList<TreeNode>,
        position: Int,
        callBack: ((TreeNode) -> Unit)?
    ) {
        if(listNode.size > position){
            val clickNode = listNode[position]

            if(!clickNode.isLeaf){
                if(clickNode.isExpand){//如果是展开  要把当前节点下整个树收缩（需要递归）
                    collapse(listNode, clickNode)
                }else{
                    expand(listNode, position)//这里展开认为仅仅是手动点击展开 只展开点击的那一级 子级需要继续手动点击
                }
            }

            callBack?.invoke(clickNode)
        }
    }

    //展开
    private fun expand(listNode: ArrayList<TreeNode>, position: Int){
        val node = listNode[position]
        node.isExpand = true

        var addPosition = position
        if(leafInFront()){
            node.getChildrenLeaf()?.forEach { treeNode ->
                addPosition += 1
                treeNode.isExpand = true
                listNode.add(addPosition, treeNode)
            }
        }

        node.getChildrenTree()?.forEach { treeNode ->
            addPosition += 1
            //treeNode.isExpand = true //这里要再次手动点击
            listNode.add(addPosition, treeNode)
        }

        if(!leafInFront()){
            node.getChildrenLeaf()?.forEach { treeNode ->
                addPosition += 1
                treeNode.isExpand = true
                listNode.add(addPosition, treeNode)
            }
        }
    }
    //收缩
    private fun collapse(listNode: ArrayList<TreeNode>, node: TreeNode){
        node.isExpand = false
        if(leafInFront()){
            node.getChildrenLeaf()?.forEach {
                it.isExpand = false
                listNode.remove(it)
            }
        }

        node.getChildrenTree()?.forEach {
            if(it.isExpand) {
                collapse(listNode, it)
            }
            listNode.remove(it)
        }

        if(!leafInFront()){
            node.getChildrenLeaf()?.forEach {
                it.isExpand = false
                listNode.remove(it)
            }
        }
    }


    //播放视频时 要动态选中正在播放的条目 需要自动展开一个不存在的Node  需要用到parentOf childOf 来找到自己的树 暂未实现 需要的时候再写
    override fun parentOf(dest: TreeNode?): Boolean {
        dest?.parent?.let {
            return this == it
        }
        return false
    }

    override fun childOf(dest: TreeNode?): Boolean {
        return dest == parent
    }


    private var level = 0 //当前节点的层级
    override fun getLevel(): Int {
        if (level == 0) { //如果是 0 的话就递归获取
            level = if (parent == null) 1 else parent!!.getLevel() + 1
            return level
        }
        return level
    }

    //默认先添加叶子
    override fun leafInFront(): Boolean = true

    val isRoot: Boolean
        get() = parent == null

    val isLeaf: Boolean
        get() = (getChildrenLeaf()?.size ?: 0) + (getChildrenTree()?.size ?: 0) <= 0
}