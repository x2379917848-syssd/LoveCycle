package com.lovecare.cycle.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lovecare.cycle.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "首页", Icons.Default.Home)
    object Period : Screen("period", "经期", Icons.Default.DateRange)
    object PeriodAdd : Screen("period/add", "添加记录")
    object PeriodEdit : Screen("period/edit/{id}", "编辑记录") {
        fun createRoute(id: Long) = "period/edit/$id"
    }
    object Water : Screen("water", "喝水", Icons.Default.WaterDrop)
    object Diary : Screen("diary", "日记", Icons.Default.Book)
    object DiaryAdd : Screen("diary/add", "写日记")
    object DiaryEdit : Screen("diary/edit/{id}", "编辑日记") {
        fun createRoute(id: Long) = "diary/edit/$id"
    }
    object Stats : Screen("stats", "统计", Icons.Default.BarChart)
    object Settings : Screen("settings", "设置", Icons.Default.Settings)
    object About : Screen("about", "关于", Icons.Default.Info)
    object Lock : Screen("lock", "锁")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Period,
    Screen.Water,
    Screen.Diary,
    Screen.Stats,
    Screen.Settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveCycleNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let { Icon(it, contentDescription = screen.title) }
                            },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Period.route) {
                PeriodScreen(navController = navController)
            }
            composable(Screen.PeriodAdd.route) {
                PeriodAddEditScreen(navController = navController)
            }
            composable(
                route = Screen.PeriodEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                PeriodAddEditScreen(navController = navController, periodId = id)
            }
            composable(Screen.Water.route) {
                WaterScreen(navController = navController)
            }
            composable(Screen.Diary.route) {
                DiaryScreen(navController = navController)
            }
            composable(Screen.DiaryAdd.route) {
                DiaryAddEditScreen(navController = navController)
            }
            composable(
                route = Screen.DiaryEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                DiaryAddEditScreen(navController = navController, diaryId = id)
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable(Screen.About.route) {
                AboutScreen(navController = navController)
            }
        }
    }
}
