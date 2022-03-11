package com.reny.git.tree

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.reny.git.tree.adapter.ChapterAdapter
import com.reny.git.tree.bean.Chapter
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    lateinit var rv: RecyclerView

    private fun readAsString(fileName: String): String? = assets?.open(fileName)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream, "utf-8")).use {
            val stringBuffer = StringBuffer()
            it.forEachLine {
                stringBuffer.append(it)
            }
            stringBuffer.toString()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv = findViewById(R.id.rv)
        val data = Gson().fromJson(readAsString("data/data.json"), Chapter::class.java)

        val chapterAdapter = ChapterAdapter()
        chapterAdapter.setNewData(data.getRootList())
        chapterAdapter.setOnItemClickListener { _, _, position ->
            //控制展开和收缩的关键代码调用
            chapterAdapter.data[position].onClickNode(chapterAdapter.data, position){
                chapterAdapter.notifyDataSetChanged()
            }
        }
        rv.adapter = chapterAdapter
    }


}