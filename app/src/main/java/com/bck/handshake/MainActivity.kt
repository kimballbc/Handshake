package com.bck.handshake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
// import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
// import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bck.handshake.auth.LoginScreen
import com.bck.handshake.data.Bet
import com.bck.handshake.ui.theme.TheSideBetTheme
// import com.bck.handshake.viewmodel.AuthState
// import com.bck.handshake.viewmodel.AuthViewModel

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
    var currentBets by remember { mutableStateOf(listOf<Bet>()) }
    // val authViewModel: AuthViewModel = viewModel()
    // val authState by authViewModel.authState.collectAsState()
    
    // Choose the starting destination based on authentication state
    // val startDestination = when (authState) {
    //     is AuthState.SignedIn -> "account"
    //     else -> "login"
    // }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        
        composable("home") {
            AccountScreen(
                currentBets = currentBets,
                onNewBetClicked = {
                    navController.navigate("new_bet") {
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onRecordsClicked = {
                    navController.navigate("records") {
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSignOut = {
                    // authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("account") { inclusive = true }
                    }
                }
            )
        }
        
        composable("landing") {
            LandingScreen(navController)
        }
        
        composable("account") {
            AccountScreen(
                currentBets = currentBets,
                onNewBetClicked = {
                    navController.navigate("new_bet") {
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onRecordsClicked = {
                    navController.navigate("records") {
                        popUpTo("account") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSignOut = {
                    // authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("account") { inclusive = true }
                    }
                }
            )
        }
        
        composable("new_bet") {
            NewBetScreen(
                onConfirmed = { bet ->
                    currentBets = currentBets + bet
                    navController.navigate("account") {
                        popUpTo("account") { inclusive = true }
                    }
                },
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

// @Composable
// fun HomeScreen() {
//     // Implement your home screen UI here
//     Text("Welcome to the Home Screen")
// }