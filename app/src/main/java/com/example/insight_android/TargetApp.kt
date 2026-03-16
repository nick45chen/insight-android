package com.example.insight_android

import android.app.ActivityManager
import android.content.Context

object TargetApp {
    val TARGET_PACKAGE_NAME: String = BuildConfig.TARGET_PACKAGE_NAME

    private var _targetContext: Context? = null

    fun getTargetContext(appContext: Context): Context {
        return _targetContext ?: appContext.createPackageContext(
            TARGET_PACKAGE_NAME,
            Context.CONTEXT_IGNORE_SECURITY
        ).also { _targetContext = it }
    }

    fun getTargetPid(context: Context): Int {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = am.runningAppProcesses ?: return -1
        for (info in list) {
            if (info.processName == TARGET_PACKAGE_NAME) {
                return info.pid
            }
        }
        return -1
    }
}
