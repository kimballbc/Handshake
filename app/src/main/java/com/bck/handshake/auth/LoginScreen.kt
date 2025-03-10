package com.bck.handshake.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bck.handshake.R
import com.bck.handshake.data.SupabaseHelper
import com.bck.handshake.ui.theme.indieFlower
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var displayNameError by remember { mutableStateOf<String?>(null) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val emailFocusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    fun validateEmail(email: String): String? {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            return "Email cannot be empty"
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return "Please enter a valid email address"
        }
        return null
    }

    fun validateDisplayName(name: String): String? {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return "Display name cannot be empty"
        }
        if (trimmedName.length < 2) {
            return "Display name must be at least 2 characters"
        }
        return null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title "SIDE BET" with indieFlower font
            Text(
                text = "SIDE BET",
                fontFamily = indieFlower,
                fontSize = 85.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Image below the title
            Image(
                painter = painterResource(id = R.drawable.handcrush),
                contentDescription = "Handshake Image",
                modifier = Modifier.size(200.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = validateEmail(it)
                },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            // When focus is lost, trim the email and validate
                            val trimmedEmail = email.trim()
                            if (trimmedEmail != email) {
                                email = trimmedEmail
                            }
                            emailError = validateEmail(trimmedEmail)
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it) } },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isSignUpMode) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { 
                        displayName = it
                        displayNameError = validateDisplayName(it)
                    },
                    label = { Text("Display Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                val trimmedName = displayName.trim()
                                if (trimmedName != displayName) {
                                    displayName = trimmedName
                                }
                                displayNameError = validateDisplayName(trimmedName)
                            }
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = displayNameError != null,
                    supportingText = displayNameError?.let { { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (!isSignUpMode) {
                            val trimmedEmail = email.trim()
                            val validationError = validateEmail(trimmedEmail)
                            if (validationError == null) {
                                isLoading = true
                                scope.launch {
                                    SupabaseHelper.signInWithEmail(trimmedEmail, password).fold(
                                        onSuccess = {
                                            isLoading = false
                                            navController.navigate("account") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        onFailure = {
                                            isLoading = false
                                            errorMessage = "Either the email or password is incorrect."
                                        }
                                    )
                                }
                            } else {
                                emailError = validationError
                            }
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isSignUpMode) {
                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        val validationError = validateEmail(trimmedEmail)
                        if (validationError != null) {
                            emailError = validationError
                            return@Button
                        }
                        
                        isLoading = true
                        scope.launch {
                            SupabaseHelper.signInWithEmail(trimmedEmail, password).fold(
                                onSuccess = {
                                    isLoading = false
                                    navController.navigate("account") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onFailure = {
                                    isLoading = false
                                    errorMessage = "Either the email or password is incorrect."
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = emailError == null && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Sign In")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { isSignUpMode = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Create Account")
                }
            } else {
                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        val trimmedDisplayName = displayName.trim()
                        val emailValidationError = validateEmail(trimmedEmail)
                        val displayNameValidationError = validateDisplayName(trimmedDisplayName)
                        
                        if (emailValidationError != null) {
                            emailError = emailValidationError
                            return@Button
                        }
                        if (displayNameValidationError != null) {
                            displayNameError = displayNameValidationError
                            return@Button
                        }
                        
                        isLoading = true
                        scope.launch {
                            SupabaseHelper.signUpWithEmail(trimmedEmail, password, trimmedDisplayName).fold(
                                onSuccess = {
                                    isLoading = false
                                    errorMessage = "Check your email for verification."
                                    isSignUpMode = false
                                    displayName = ""
                                    displayNameError = null
                                },
                                onFailure = {
                                    isLoading = false
                                    errorMessage = "Either the email or password is incorrect."
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = emailError == null && displayNameError == null && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { 
                        isSignUpMode = false
                        displayName = ""
                        displayNameError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Back to Sign In")
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 