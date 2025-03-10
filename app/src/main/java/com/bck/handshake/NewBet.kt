package com.bck.handshake

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bck.handshake.data.Bet
import com.bck.handshake.data.SupabaseHelper
import com.bck.handshake.data.User
import com.bck.handshake.navigation.BottomNavBar
import kotlinx.coroutines.launch

/**
 * NewBet screen implementation.
 * This screen provides the interface for creating and confirming new bets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBetScreen(
    onConfirmed: (Bet) -> Unit,
    onHomeClicked: () -> Unit,
    onRecordsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        var selectedUser by remember { mutableStateOf<User?>(null) }
        var betDescription by remember { mutableStateOf("") }
        var prideWagered by remember { mutableStateOf("") }
        var isParticipantMenuExpanded by remember { mutableStateOf(false) }
        var availableUsers by remember { mutableStateOf<List<User>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        
        val focusManager = LocalFocusManager.current
        val descriptionFocusRequester = remember { FocusRequester() }
        val prideFocusRequester = remember { FocusRequester() }
        val scope = rememberCoroutineScope()

        // Load available users when the screen is first displayed
        LaunchedEffect(Unit) {
            SupabaseHelper.getAvailableUsers().fold(
                onSuccess = { users ->
                    isLoading = false
                    availableUsers = users
                },
                onFailure = { e ->
                    isLoading = false
                    errorMessage = e.message ?: "Failed to load users"
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Main content in a scrollable column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // Account for bottom bar
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New Bet",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Participant Selection
                    ExposedDropdownMenuBox(
                        expanded = isParticipantMenuExpanded,
                        onExpandedChange = { isParticipantMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedUser?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Participant") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isParticipantMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isParticipantMenuExpanded,
                            onDismissRequest = { isParticipantMenuExpanded = false }
                        ) {
                            if (availableUsers.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No users available") },
                                    onClick = { isParticipantMenuExpanded = false }
                                )
                            } else {
                                availableUsers.forEach { user ->
                                    DropdownMenuItem(
                                        text = { Text(user.name) },
                                        onClick = {
                                            selectedUser = user
                                            isParticipantMenuExpanded = false
                                            descriptionFocusRequester.requestFocus()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Bet Description
                    OutlinedTextField(
                        value = betDescription,
                        onValueChange = { betDescription = it },
                        label = { Text("Bet Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(descriptionFocusRequester),
                        minLines = 3,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                prideFocusRequester.requestFocus()
                            }
                        )
                    )

                    // Pride Wagered
                    OutlinedTextField(
                        value = prideWagered,
                        onValueChange = { 
                            // Only allow numeric input
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                prideWagered = it
                            }
                        },
                        label = { Text("Pride Wagered") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(prideFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Only enable the HandshakeSlider if all fields are filled
                    val isFormValid = selectedUser != null && 
                                    betDescription.isNotEmpty() && 
                                    prideWagered.isNotEmpty()

                    if (isFormValid) {
                        HandshakeSlider(
                            onConfirmed = {
                                selectedUser?.let { user ->
                                    scope.launch {
                                        isLoading = true
                                        SupabaseHelper.createBet(
                                            participantId = user.id,
                                            description = betDescription,
                                            prideWagered = prideWagered.toInt()
                                        ).fold(
                                            onSuccess = {
                                                val bet = Bet(
                                                    id = "", // The ID will be set by Supabase
                                                    participant = user,
                                                    description = betDescription,
                                                    prideWagered = prideWagered.toInt(),
                                                    status = "pending",
                                                    isCreator = true
                                                )
                                                isLoading = false
                                                onConfirmed(bet)
                                            },
                                            onFailure = { e ->
                                                isLoading = false
                                                errorMessage = e.message ?: "Failed to create bet"
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        Text(
                            text = "Please fill in all fields to continue",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Bottom Navigation
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                BottomNavBar(
                    selectedIndex = 1,
                    onHomeSelected = onHomeClicked,
                    onNewBetSelected = { /* Already on NewBet screen */ },
                    onRecordsSelected = onRecordsClicked,
                    modifier = Modifier
                )
            }
        }
    }
} 