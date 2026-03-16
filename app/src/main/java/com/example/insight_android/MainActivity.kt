package com.example.insight_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insight_android.navigation.Screen
import com.example.insight_android.screen.*
import com.example.insight_android.ui.theme.InsightandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InsightandroidTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToSp = { navController.navigate(Screen.SpList.route) },
                            onNavigateToDb = { navController.navigate(Screen.DbList.route) },
                            onNavigateToFiles = { title, path ->
                                navController.navigate(Screen.FileList.createRoute(title, path))
                            }
                        )
                    }

                    composable(Screen.SpList.route) {
                        SpListScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToContent = { path ->
                                navController.navigate(Screen.SpContent.createRoute(path))
                            }
                        )
                    }

                    composable(Screen.SpContent.route) { backStackEntry ->
                        val path = Screen.SpContent.decodePath(
                            backStackEntry.arguments?.getString("path") ?: ""
                        )
                        SpContentScreen(
                            filePath = path,
                            onBack = { navController.popBackStack() },
                            onNavigateToDetail = { spName, key ->
                                navController.navigate(Screen.SpDetail.createRoute(spName, key))
                            }
                        )
                    }

                    composable(Screen.SpDetail.route) { backStackEntry ->
                        val spName = Screen.SpDetail.decodeArg(
                            backStackEntry.arguments?.getString("spName") ?: ""
                        )
                        val key = Screen.SpDetail.decodeArg(
                            backStackEntry.arguments?.getString("key") ?: ""
                        )
                        SpDetailScreen(
                            spName = spName,
                            key = key,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.FileList.route) { backStackEntry ->
                        val title = Screen.FileList.decodeArg(
                            backStackEntry.arguments?.getString("title") ?: ""
                        )
                        val path = Screen.FileList.decodeArg(
                            backStackEntry.arguments?.getString("path") ?: ""
                        )
                        FileListScreen(
                            title = title,
                            rootPath = path,
                            onBack = { navController.popBackStack() },
                            onNavigateToFileText = { filePath ->
                                navController.navigate(Screen.FileText.createRoute(filePath))
                            }
                        )
                    }

                    composable(Screen.FileText.route) { backStackEntry ->
                        val path = Screen.FileText.decodePath(
                            backStackEntry.arguments?.getString("path") ?: ""
                        )
                        FileTextScreen(
                            filePath = path,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.DbList.route) {
                        DbListScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
