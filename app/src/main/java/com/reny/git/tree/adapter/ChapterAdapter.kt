package com.reny.git.tree.adapter

import android.view.View
import android.view.ViewGroup
import com.reny.git.lib.tree.TreeNode
import com.reny.git.tree.R
import com.reny.git.tree.bean.ChapterX
import com.reny.git.tree.bean.Exam
import com.reny.git.tree.recycler.BaseViewHolder
import com.reny.git.tree.recycler.QuickAdapter

/**
 * 描述：
 * 作者：reny
 * 时间：2022/3/10 14:52
 */
class ChapterAdapter: QuickAdapter<TreeNode>(R.layout.item_chapter) {

    override fun convert(holder: BaseViewHolder, item: TreeNode?, position: Int) {
        item?.run {
            holder.getView<View>(R.id.ll_root).let {
                val lp: ViewGroup.MarginLayoutParams = it.layoutParams as ViewGroup.MarginLayoutParams
                lp.marginStart = 20 * (this.getLevel() - 1)
            }

            if(this is ChapterX){
                //判断了只有顶层的树才显示序号  根据需求自行决定
                holder.setText(R.id.tv, "${if(isRoot) "${(index + 1)}." else ""}${courseName}")
            }
            if(this is Exam){
                holder.setText(R.id.tv, "${index + 1}.${examName}")
            }

            holder.setVisible(R.id.iv_arrow, this is ChapterX)
            holder.setImageResource(R.id.iv_arrow, if(isExpand) R.mipmap.ic_arrow_up else R.mipmap.ic_arrow_down)
        }
    }

}