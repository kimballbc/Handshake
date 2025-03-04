package com.bck.handshake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bck.handshake.ui.theme.TheSideBetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheSideBetTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MyApp(
    modifier: Modifier
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") {
            LandingScreen(navController)
        }
        composable("account") {
            AccountScreen(
                onNewBetClicked = {
                    navController.navigate("new_bet") {
                        // Pop up to account screen and save state
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onRecordsClicked = {
                    navController.navigate("records") {
                        // Pop up to account screen and save state
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("new_bet") {
            NewBetScreen(
                onConfirmed = { navController.popBackStack() },
                onHomeClicked = {
                    navController.navigate("account") {
                        popUpTo("account") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onRecordsClicked = {
                    navController.navigate("records") {
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("records") {
            RecordsScreen(
                onHomeClicked = {
                    navController.navigate("account") {
                        popUpTo("account") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNewBetClicked = {
                    navController.navigate("new_bet") {
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}