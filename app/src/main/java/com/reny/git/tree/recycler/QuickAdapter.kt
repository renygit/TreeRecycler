package com.reny.git.tree.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

abstract class QuickAdapter<T>(
    private val layoutId: Int,
    data: ArrayList<T>? = null
) : RecyclerView.Adapter<BaseViewHolder>() {

    var data: ArrayList<T> = data ?: arrayListOf()
        internal set

    private var mOnItemClickListener: ((adapter: QuickAdapter<*>, view: View, position: Int) -> Unit)? = null
    private var mOnItemLongClickListener: ((adapter: QuickAdapter<*>, view: View, position: Int) -> Boolean)? = null
    private var mOnItemChildClickListener: ((adapter: QuickAdapter<*>, view: View, position: Int) -> Unit)? = null
    private var mOnItemChildLongClickListener: ((adapter: QuickAdapter<*>, view: View, position: Int) -> Boolean)? = null

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        if (layoutId == 0) {
            throw RuntimeException("${this.javaClass.simpleName}: layoutId == 0")
        }
        return layoutId
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        convert(holder, getItem(position), position)
        bindViewClickListener(holder, getItemViewType(position), position)
    }

    open fun getItem(@IntRange(from = 0) position: Int): T? {
        return if(position < data.size) data[position] else null
    }

    protected abstract fun convert(holder: BaseViewHolder, item: T?, position: Int)


    open fun clear(){
        this.data.clear()
        notifyDataSetChanged()
    }

    open fun setNewData(data: ArrayList<T>?) {
        if (data == this.data) {
            return
        }
        this.data = data ?: arrayListOf()
        notifyDataSetChanged()
    }

    open fun addDatas(newData: ArrayList<T>, isRefresh: Boolean){
        if(isRefresh){
            setNewData(newData)
        }else{
            addData(newData)
        }
    }

    protected fun compatibilityDataSizeChanged(size: Int) {
        if (this.data.size == size) {
            notifyDataSetChanged()
        }
    }

    /**
     * add one new data in to certain location
     * ????????????????????????????????????
     *
     * @param position
     */
    open fun addData(@IntRange(from = 0) position: Int, data: T) {
        this.data.add(position, data)
        notifyItemInserted(position)
        compatibilityDataSizeChanged(1)
    }

    /**
     * add one new data
     * ?????????????????????
     */
    open fun addData(@NonNull data: T) {
        this.data.add(data)
        notifyItemInserted(this.data.size)
        compatibilityDataSizeChanged(1)
    }

    /**
     * add new data in to certain location
     * ???????????????????????????
     *
     * @param position the insert position
     * @param newData  the new data collection
     */
    open fun addData(@IntRange(from = 0) position: Int, newData: Collection<T>) {
        this.data.addAll(position, newData)
        notifyItemRangeInserted(position, newData.size)
        compatibilityDataSizeChanged(newData.size)
    }

    open fun addData(@NonNull newData: Collection<T>) {
        this.data.addAll(newData)
        notifyItemRangeInserted(this.data.size - newData.size, newData.size)
        compatibilityDataSizeChanged(newData.size)
    }

    /**
     * remove the item associated with the specified position of adapter
     * ???????????????????????????
     *
     * @param position
     */
    open fun remove(@IntRange(from = 0) position: Int) {
        if (position >= data.size) {
            return
        }
        this.data.removeAt(position)
        val internalPosition = position
        notifyItemRemoved(internalPosition)
        compatibilityDataSizeChanged(0)
        notifyItemRangeChanged(internalPosition, this.data.size - internalPosition)
    }

    open fun remove(data: T?) {
        val index = this.data.indexOf(data)
        if (index == -1) {
            return
        }
        remove(index)
    }

    /**
     * change data
     * ????????????????????????
     */
    open fun setData(@IntRange(from = 0) index: Int, data: T) {
        if (index >= this.data.size) {
            return
        }
        this.data[index] = data
        notifyItemChanged(index)
    }

    /**
     * use data to replace all item in mData. this method is different [setNewData],
     * it doesn't change the [BaseQuickAdapter.data] reference
     *
     * @param newData data collection
     */
    open fun replaceData(newData: Collection<T>) {
        // ????????????????????????????????????
        if (newData != this.data) {
            this.data.clear()
            this.data.addAll(newData)
        }
        notifyDataSetChanged()
    }



    protected open fun setOnItemClick(v: View, position: Int) {
        mOnItemClickListener?.let { it(this, v, position) }
        onItemClickListener?.onItemClick(this, v, position)
    }
    protected open fun setOnItemLongClick(v: View, position: Int): Boolean {
        return mOnItemLongClickListener?.let { it(this, v, position) } ?: false
    }

    //????????? addChildClickViewIds
    protected open fun setOnItemChildClick(v: View, position: Int) {
        mOnItemChildClickListener?.let { it(this, v, position) }
    }

    //????????? addChildLongClickViewIds
    protected open fun setOnItemChildLongClick(v: View, position: Int): Boolean {
        return mOnItemChildLongClickListener?.let { it(this, v, position) } ?: false
    }


    /**
     * ?????? item ????????????
     * @param viewHolder VH
     */
    protected open fun bindViewClickListener(viewHolder: BaseViewHolder, viewType: Int, position: Int) {
        if(mOnItemClickListener != null || onItemClickListener != null) {
            viewHolder.itemView.setOnClickListener { v ->
                //val position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                setOnItemClick(v, position)
            }
        }
        mOnItemLongClickListener?.let {
            viewHolder.itemView.setOnLongClickListener { v ->
                //var position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnLongClickListener false
                }
                setOnItemLongClick(v, position)
            }
        }

        mOnItemChildClickListener?.let {
            for (id in getChildClickViewIds()) {
                viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                    if (!childView.isClickable) {
                        childView.isClickable = true
                    }
                    childView.setOnClickListener { v ->
                        //var position = viewHolder.adapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnClickListener
                        }
                        setOnItemChildClick(v, position)
                    }
                }
            }
        }
        mOnItemChildLongClickListener?.let {
            for (id in getChildLongClickViewIds()) {
                viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                    if (!childView.isLongClickable) {
                        childView.isLongClickable = true
                    }
                    childView.setOnLongClickListener { v ->
                        //var position = viewHolder.adapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnLongClickListener false
                        }
                        setOnItemChildLongClick(v, position)
                    }
                }
            }
        }
    }


    private val childClickViewIds = LinkedHashSet<Int>()
    fun getChildClickViewIds(): LinkedHashSet<Int> {
        return childClickViewIds
    }
    fun addChildClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childClickViewIds.add(viewId)
        }
    }


    private val childLongClickViewIds = LinkedHashSet<Int>()
    fun getChildLongClickViewIds(): LinkedHashSet<Int> {
        return childLongClickViewIds
    }
    fun addChildLongClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childLongClickViewIds.add(viewId)
        }
    }

    fun setOnItemClickListener(listener: (adapter: QuickAdapter<*>, view: View, position: Int) -> Unit) {
        this.mOnItemClickListener = listener
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (adapter: QuickAdapter<*>, view: View, position: Int) -> Boolean) {
        this.mOnItemLongClickListener = listener
    }

    fun setOnItemChildClickListener(listener: (adapter: QuickAdapter<*>, view: View, position: Int) -> Unit) {
        this.mOnItemChildClickListener = listener
    }

    fun setOnItemChildLongClickListener(listener: (adapter: QuickAdapter<*>, view: View, position: Int) -> Boolean) {
        this.mOnItemChildLongClickListener = listener
    }


    interface OnItemClickListener {
        fun onItemClick(adapter: QuickAdapter<*>, view: View, position: Int)
    }

}