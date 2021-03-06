package cc.aoeiuv020.pager.test

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import cc.aoeiuv020.pager.Margins
import cc.aoeiuv020.reader.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx

class MainActivity : Activity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fl = FrameLayout(ctx)
        setContentView(fl)

        val requester = object : TextRequester {
            override fun request(index: Int, refresh: Boolean): Text {
                return Text(List(10) {
                    List(it + 1) {
                        "小说内容ablIj" + it
                    }.joinToString(";")
                })
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                return
            }
        }

        val config = ReaderConfig(31, 11, 8,
                Margins(), Margins(), Margins(), Margins(), Margins(), Margins(),
                12, "HH:mm:ss",
                0xff000000.toInt(), 0xffffffff.toInt(), null,
                AnimationMode.SIMULATION, animationSpeed = 0.2f)
        val reader = Readers.getReader(ctx, Novel("书名", "作者名"), fl, requester, config)
        val chapters = List(20) {
            Chapter("章节名" + it)
        }
        reader.chapterList = chapters
    }
}
