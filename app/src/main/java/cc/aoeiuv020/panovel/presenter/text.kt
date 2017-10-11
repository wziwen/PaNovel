package cc.aoeiuv020.panovel.presenter

import cc.aoeiuv020.panovel.api.ChaptersRequester
import cc.aoeiuv020.panovel.api.NovelChapter
import cc.aoeiuv020.panovel.api.NovelContext
import cc.aoeiuv020.panovel.api.NovelItem
import cc.aoeiuv020.panovel.local.Cache
import cc.aoeiuv020.panovel.ui.NovelTextActivity
import cc.aoeiuv020.panovel.ui.NovelTextPagerAdapter
import io.reactivex.Observable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.error
import java.io.IOException

/**
 *
 * Created by AoEiuV020 on 2017.10.03-19:06:50.
 */
class NovelTextPresenter(private val view: NovelTextActivity, private val novelItem: NovelItem) : AnkoLogger {
    private val context: NovelContext by lazy {
        NovelContext.getNovelContextByUrl(novelItem.requester.url)
    }
    private var refresh = false

    fun start() {
        requestNovelDetail()
    }

    fun refresh() {
        refresh = true
        requestNovelDetail()
    }

    fun download(fromIndex: Int) {
        Observable.fromCallable {
            val detail = Cache.getDetail(novelItem)
                    ?: context.getNovelDetail(novelItem.requester).also { Cache.putDetail(it) }
            val chapters = Cache.getChapters(novelItem)
                    ?: context.getNovelChaptersAsc(detail.requester).also { Cache.putChapters(novelItem, it) }
            var exists = 0
            var downloads = 0
            var errors = 0
            chapters.drop(fromIndex).forEach { chapter ->
                Cache.getText(novelItem, chapter).also { exists++ } ?: try {
                    context.getNovelText(chapter.requester)
                } catch (_: Exception) {
                    errors++
                    null
                }?.also { Cache.putText(novelItem, chapter, it); downloads++ }
            }
            Triple(exists, downloads, errors)
        }.async().subscribe({ (exists, downloads, errors) ->
            debug {
                "download complete <exists, $exists> <downloads, $downloads> <errors, $errors>"
            }
            view.downloadComplete(exists, downloads, errors)
        }, { e ->
            val message = "下载小说失败，"
            error(message, e)
            view.showError(message, e)
        })
    }

    private fun requestNovelDetail() {
        val requester = novelItem.requester
        Observable.fromCallable {
            if (refresh) {
                context.getNovelDetail(requester).also { Cache.putDetail(it) }
            } else {
                Cache.getDetail(novelItem)
                        ?: context.getNovelDetail(requester).also { Cache.putDetail(it) }
            }
        }.async().subscribe({ detail ->
            view.showDetail(detail)
        }, { e ->
            val message = "加载小说章节详情失败，"
            error(message, e)
            view.showError(message, e)
        })
    }

    fun requestChapters(requester: ChaptersRequester) {
        Observable.fromCallable {
            if (refresh) {
                context.getNovelChaptersAsc(requester).also { Cache.putChapters(novelItem, it) }
            } else {
                try {
                    Cache.getChapters(novelItem)
                            ?: context.getNovelChaptersAsc(requester).also { Cache.putChapters(novelItem, it) }
                } catch (e: IOException) {
                    error { "网络有问题，读取缓存不判断超时，" }
                    Cache.getChapters(novelItem, 0) ?: throw e
                }
            }
        }.async().subscribe({ chapters ->
            view.showChapters(chapters)
        }, { e ->
            val message = "加载小说章节列表失败，"
            error(message, e)
            view.showError(message, e)
        })
    }

    fun subPresenter(view: NovelTextPagerAdapter.ViewHolder) = NTPresenter(view)

    inner class NTPresenter(private val view: NovelTextPagerAdapter.ViewHolder) {
        fun requestNovelText(chapter: NovelChapter) {
            Observable.fromCallable {
                if (refresh) {
                    // 只刷新一章，
                    refresh = false
                    context.getNovelText(chapter.requester).also { Cache.putText(novelItem, chapter, it) }
                } else {
                    Cache.getText(novelItem, chapter)
                            ?: context.getNovelText(chapter.requester).also { Cache.putText(novelItem, chapter, it) }
                }
            }.async().subscribe({ novelText ->
                view.showText(novelText)
            }, { e ->
                val message = "加载小说页面失败，"
                error(message, e)
                view.showError(message, e)
            })
        }
    }
}
