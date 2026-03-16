package com.example.insight_android.util

object Utilities {

    fun formatFileLength(length: Long): String {
        val numK: Float = 1.0f * length / 1024
        return if (numK > 1024) {
            "%.3f".format(numK / 1024) + " MB"
        } else {
            "%.3f".format(numK) + " KB"
        }
    }

    fun formatMemorySize(length: Int): String {
        return "%.3f".format(1.0f * length / 1024) + " MB"
    }
}
