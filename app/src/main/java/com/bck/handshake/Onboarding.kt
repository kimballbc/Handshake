package com.bck.handshake

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bck.handshake.components.LoadingOverlay
import com.bck.handshake.data.SupabaseHelper
import com.bck.handshake.ui.theme.indieFlower

@Composable
fun LandingScreen(navController: NavController) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with title
            Text(
                text = "SIDE BET",
                fontFamily = indieFlower,
                fontSize = 85.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 24.dp)
                    .testTag("sideBet_title")
            )

            // Middle section with image
            Image(
                painter = painterResource(id = R.drawable.handcrush),
                contentDescription = "Hand being crushed",
                modifier = Modifier
                    .size(300.dp)
                    .testTag("sideBet_logo")
            )

            // Bottom section with buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("account") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Sign In")
                }

                Button(
                    onClick = { /* Handle Sign Up button click */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Sign Up")
                }
            }
        }
    }
}

@Composable
fun Onboarding(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Handshake",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = indieFlower
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    error = null // Clear error when user types
                },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = !isLoading,
                isError = error != null
            )

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    error = null // Clear error when user types
                },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            SupabaseHelper.signInWithEmail(email, password) { success, errorMessage ->
                                isLoading = false
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    error = errorMessage
                                }
                            }
                        }
                    }
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = !isLoading,
                isError = error != null
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        SupabaseHelper.signInWithEmail(email, password) { success, errorMessage ->
                            isLoading = false
                            if (success) {
                                onLoginSuccess()
                            } else {
                                error = errorMessage
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
            ) {
                Text("Sign In")
            }

            TextButton(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        val displayName = email.substringBefore("@")
                        SupabaseHelper.signUpWithEmail(email, password, displayName) { success, errorMessage ->
                            isLoading = false
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Check your email to confirm your account",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                error = errorMessage
                            }
                        }
                    }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
            ) {
                Text("Create Account")
            }
        }

        // Loading overlay
        if (isLoading) {
            LoadingOverlay(message = "Authenticating...")
        }
    }
}