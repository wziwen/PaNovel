package cc.aoeiuv020.panovel.detail

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckedTextView
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.api.NovelChapter
import cc.aoeiuv020.panovel.api.NovelItem
import cc.aoeiuv020.panovel.local.*
import cc.aoeiuv020.panovel.text.NovelTextActivity
import cn.lemon.view.adapter.BaseViewHolder
import cn.lemon.view.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.novel_chapter_item.view.*
import org.jetbrains.anko.AnkoLogger
import kotlin.properties.Delegates

class NovelChaptersAdapter(ctx: Context, private val novelItem: NovelItem) : RecyclerAdapter<NovelChapter>(ctx), AnkoLogger {
    private var readAt: Int by Delegates.notNull()
    private lateinit var container: Container
    override fun onCreateBaseViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<NovelChapter>
            = ViewHolder(parent)

    init {
        init()
    }

    private fun init() {
        readAt = Progress.load(novelItem).chapter
        container = Cache.text.container(novelItem)
    }

    fun refresh() {
        init()
        notifyDataSetChanged()
    }

    inner class ViewHolder(parent: ViewGroup?) : BaseViewHolder<NovelChapter>(parent, R.layout.novel_chapter_item) {
        private val nameTextView: CheckedTextView = itemView.name

        init {
            nameTextView.setTextColor(Settings.chapterColorList)
        }
        override fun setData(chapter: NovelChapter) {
            super.setData(chapter)
            nameTextView.apply {
                text = chapter.name
                isChecked = readAt == indexAsc
                isSelected = container.contains(chapter.id)
            }
        }

        /**
         * 计算顺序的索引，
         */
        private val indexAsc get() = data.size - 1 - layoutPosition

        override fun onItemViewClick(issue: NovelChapter) {
            NovelTextActivity.start(context, novelItem, indexAsc)
        }
    }
}