package com.bck.handshake

// import androidx.compose.runtime.collectAsState
// import androidx.lifecycle.viewmodel.compose.viewModel
// import com.bck.handshake.viewmodel.AuthState
// import com.bck.handshake.viewmodel.AuthViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bck.handshake.auth.LoginScreen
import com.bck.handshake.data.SupabaseHelper
import com.bck.handshake.ui.theme.TheSideBetTheme
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        
//        composable("onboarding") {
//            LandingScreen(navController)
//        }
        
        composable("home") {
            AccountScreen(
                currentBets = emptyList(),
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
                    scope.launch {
                        SupabaseHelper.signOut().fold(
                            onSuccess = {
                                navController.navigate("login") {
                                    popUpTo("account") { inclusive = true }
                                }
                            },
                            onFailure = { /* Handle error if needed */ }
                        )
                    }
                }
            )
        }
        
        composable("account") {
            AccountScreen(
                currentBets = emptyList(),
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
                    scope.launch {
                        SupabaseHelper.signOut().fold(
                            onSuccess = {
                                navController.navigate("login") {
                                    popUpTo("account") { inclusive = true }
                                }
                            },
                            onFailure = { /* Handle error if needed */ }
                        )
                    }
                }
            )
        }
        
        composable("new_bet") {
            NewBetScreen(
                onConfirmed = { bet ->
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