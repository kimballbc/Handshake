package com.bck.handshake

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bck.handshake.navigation.BottomNavBar
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import com.bck.handshake.data.Bet
import com.bck.handshake.data.User
import com.bck.handshake.data.sampleUser
import com.bck.handshake.data.sampleUser2
import com.bck.handshake.data.sampleUser3
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

// Get list of available users from dtos
private val availableUsers = listOf(sampleUser, sampleUser2, sampleUser3)

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
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var betDescription by remember { mutableStateOf("") }
    var prideWagered by remember { mutableStateOf("") }
    var isParticipantMenuExpanded by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val descriptionFocusRequester = remember { FocusRequester() }
    val prideFocusRequester = remember { FocusRequester() }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    selectedIndex = 1,
                    onHomeSelected = onHomeClicked,
                    onNewBetSelected = { /* Already on NewBet screen */ },
                    onRecordsSelected = onRecordsClicked
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New Bet",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

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
                                val bet = Bet(
                                    participant = user,
                                    description = betDescription,
                                    prideWagered = prideWagered.toInt(),
                                    isConfirmed = true
                                )
                                onConfirmed(bet)
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
            }
        }
    }
} 