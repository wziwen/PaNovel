package cc.aoeiuv020.panovel.base.item

import android.content.Context
import android.view.ViewGroup
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.local.NovelHistory
import cc.aoeiuv020.panovel.local.Settings
import cn.lemon.view.adapter.BaseViewHolder
import cn.lemon.view.adapter.RecyclerAdapter

/**
 *
 * Created by AoEiuV020 on 2017.11.22-12:02:27.
 */
abstract class BaseItemListAdapter(context: Context)
    : RecyclerAdapter<NovelHistory>(context) {

    override fun onViewRecycled(holder: BaseViewHolder<NovelHistory>) {
        // header和footer会强转失败，
        (holder as? SmallItemViewHolder<*>)?.destroy()
    }
}

open class DefaultItemListAdapter(context: Context, private val presenter: BaseItemListPresenter<*, DefaultItemPresenter>,
                                  private val listener: OnItemLongClickListener? = null)
    : BaseItemListAdapter(context) {
    override fun onCreateBaseViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<NovelHistory>
            = if (Settings.BookSmallLayout) {
        DefaultItemViewHolder(presenter, context, parent, R.layout.novel_item_small, listener)
    } else {
        DefaultItemViewHolder(presenter, context, parent, R.layout.novel_item_big, listener)
    }

}
