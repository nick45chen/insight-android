package com.example.insight_android.model

import android.content.Context
import java.io.File

object SpModel {
    data class SpFileInfo(val file: File, val size: Long)
    data class SpListData(val dir: File, val files: List<SpFileInfo>, val totalLength: Long)

    fun loadSpListData(targetContext: Context): SpListData {
        val dataDir = targetContext.filesDir.parentFile!!
        val spDir = File(dataDir, "shared_prefs")
        val files = spDir.listFiles()
        if (files == null || files.isEmpty()) {
            return SpListData(spDir, emptyList(), 0)
        }

        val list = files.map { SpFileInfo(it, it.length()) }.sortedBy { it.file.name }
        val totalSize = list.sumOf { it.size }
        return SpListData(spDir, list, totalSize)
    }

    fun loadSpContent(targetContext: Context, spName: String): Map<String, Any?> {
        val sp = targetContext.getSharedPreferences(spName, Context.MODE_PRIVATE)
        return sp.all.toSortedMap()
    }
}
