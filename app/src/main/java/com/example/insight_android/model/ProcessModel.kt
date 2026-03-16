package com.example.insight_android.model

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import java.io.File

object ProcessModel {

    fun getThreadCount(pid: Int): Int {
        val file = File("/proc/$pid/task")
        return if (file.exists() && file.canRead()) {
            file.list()?.size ?: 0
        } else 0
    }

    fun getFdCount(pid: Int): Int {
        val file = File("/proc/$pid/fd")
        return if (file.exists() && file.canRead()) {
            file.list()?.size ?: 0
        } else 0
    }

    fun getMemoryInfo(context: Context, pid: Int): Debug.MemoryInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = am.getProcessMemoryInfo(intArrayOf(pid))
        return memoryInfo[0]
    }
}
