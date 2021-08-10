package thapl.com.fudis.ui.base

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import thapl.com.fudis.GlideRequests
import thapl.com.fudis.domain.model.ListItem

abstract class BaseListAdapter<VM : BaseViewModel, T : ListItem, VH : BaseHolder<VM, T>>(
    private val glide: GlideRequests?,
    private val viewModel: VM,
    private val click: (T, Any?) -> Unit,
    private val getHolder: (ViewGroup, Int) -> VH
) : ListAdapter<T, VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), glide, viewModel, currentList.size - 1 == position, click)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }
}

abstract class BaseHolder<VM : BaseViewModel, T : ListItem>(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    abstract fun bind(item: T, glide: GlideRequests?, viewModel: VM, isLast: Boolean, click: (T, Any?) -> Unit)

    private val lifecycleRegistry = LifecycleRegistry(this)

    fun onAttached() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun onDetached() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun getLifecycle() = lifecycleRegistry
}

class DiffCallback<T : ListItem> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.unique() == newItem.unique()
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.sameContent(newItem)
    }
}