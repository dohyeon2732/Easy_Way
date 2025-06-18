package com.example.mobile_project_3.ui_screen

<<<<<<< HEAD
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
=======
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
>>>>>>> 964ebc6 (Initial commit)
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mobile_project_3.Navigation.Screen
import com.example.mobile_project_3.R

<<<<<<< HEAD
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedColor = Color(0xFF30C4CC)
    val defaultColor = Color.Black

    NavigationBar(containerColor = Color.White,
        modifier = Modifier.height(72.dp)
            .fillMaxWidth()) {
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        id = if (currentRoute == Screen.Home.route)
                            R.drawable.home_color else R.drawable.home_black
                    ),
                    contentDescription = "홈",
                    modifier = Modifier.height(22.dp)
                )
            },
            label = { Text("홈",
                color = if (currentRoute == Screen.Home.route) selectedColor else defaultColor
            ) },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false  }
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        id = if (currentRoute == Screen.Favorites.route)
                            R.drawable.stars else R.drawable.star_t
                    ),
                    contentDescription = "즐겨찾기",
                    modifier = Modifier.height(22.dp)
                )
            },
            label = { Text("즐겨찾기",
                color = if (currentRoute == Screen.Favorites.route) selectedColor else defaultColor
            ) },
            selected = currentRoute == Screen.Favorites.route,
            onClick = {
                if (currentRoute != Screen.Favorites.route) {
                    navController.navigate(Screen.Favorites.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        id = if (currentRoute == Screen.MyPage.route)
                            R.drawable.person_selected else R.drawable.person
                    ),
                    contentDescription = "마이페이지",
                    modifier = Modifier.height(22.dp)
                )
            },
            label = { Text("마이페이지",
                color = if (currentRoute == Screen.MyPage.route) selectedColor else defaultColor
            ) },
            selected = currentRoute == Screen.MyPage.route,
            onClick = {
                if (currentRoute != Screen.MyPage.route) {
                    navController.navigate(Screen.MyPage.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
        )
=======

@Composable
fun BottomNavigationBar(navController: NavController, isDarkTheme: Boolean = false) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFEEEEEE))
        )
        NavigationBar(
            containerColor = if (isDarkTheme) Color.Black else Color.White,
            modifier = Modifier.height(100.dp)
                .fillMaxWidth()) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(
                            id = if (currentRoute == Screen.Home.route)
                                R.drawable.home_selected else R.drawable.home_nonselected
                        ),
                        contentDescription = "홈",
                        modifier = Modifier.size(36.dp),
                        tint = if (isDarkTheme) Color.White else Color.Unspecified
                    )
                },
                label = { Text("홈",
                    color = if (isDarkTheme) Color.White else Color.Black
                ) },
                selected = currentRoute == Screen.Home.route,
                onClick = {
                    if (currentRoute != Screen.Home.route) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(
                            id = if (currentRoute == Screen.Favorites.route)
                                R.drawable.star_selected else R.drawable.star_nonselected
                        ),
                        contentDescription = "즐겨찾기",
                        modifier = Modifier.size(28.dp),
                        tint = if (isDarkTheme) Color.White else Color.Unspecified
                    )
                },
                label = { Text("즐겨찾기",
                    color = if (isDarkTheme) Color.White else Color.Black
                ) },
                selected = currentRoute == Screen.Favorites.route,
                onClick = {
                    if (currentRoute != Screen.Favorites.route) {
                        navController.navigate(Screen.Favorites.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(
                            id = if (currentRoute == Screen.MyPage.route)
                                R.drawable.mypage_selected else R.drawable.mypage_nonselected
                        ),
                        contentDescription = "마이페이지",
                        modifier = Modifier.size(28.dp),
                        tint = if (isDarkTheme) Color.White else Color.Unspecified
                    )
                },
                label = { Text("마이페이지",
                    color = if (isDarkTheme) Color.White else Color.Black
                ) },
                selected = currentRoute == Screen.MyPage.route,
                onClick = {
                    if (currentRoute != Screen.MyPage.route) {
                        navController.navigate(Screen.MyPage.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
>>>>>>> 964ebc6 (Initial commit)
    }
}
