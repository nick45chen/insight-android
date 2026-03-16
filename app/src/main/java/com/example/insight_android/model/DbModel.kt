package com.example.insight_android.model

import android.content.Context
import java.io.File

object DbModel {
    data class DbListData(val dir: File, val files: List<File>, val totalLength: Long)

    fun loadDbListData(targetContext: Context): DbListData {
        val dataDir = targetContext.filesDir.parentFile!!
        val dbDir = File(dataDir, "databases")
        val files = dbDir.listFiles()
        if (files == null || files.isEmpty()) {
            return DbListData(dbDir, emptyList(), 0)
        }

        val dbFiles = files.filter { it.name.endsWith(".db") }.sorted()
        val totalSize = dbFiles.sumOf { it.length() }
        return DbListData(dbDir, dbFiles, totalSize)
    }
}
