package com.example.insight_android.navigation

import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object SpList : Screen("sp_list")
    data object SpContent : Screen("sp_content/{path}") {
        fun createRoute(path: String): String = "sp_content/${URLEncoder.encode(path, "UTF-8")}"
        fun decodePath(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    }
    data object SpDetail : Screen("sp_detail/{spName}/{key}") {
        fun createRoute(spName: String, key: String): String =
            "sp_detail/${URLEncoder.encode(spName, "UTF-8")}/${URLEncoder.encode(key, "UTF-8")}"
        fun decodeArg(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    }
    data object FileList : Screen("file_list/{title}/{path}") {
        fun createRoute(title: String, path: String): String =
            "file_list/${URLEncoder.encode(title, "UTF-8")}/${URLEncoder.encode(path, "UTF-8")}"
        fun decodeArg(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    }
    data object FileText : Screen("file_text/{path}") {
        fun createRoute(path: String): String = "file_text/${URLEncoder.encode(path, "UTF-8")}"
        fun decodePath(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")
    }
    data object DbList : Screen("db_list")
}
