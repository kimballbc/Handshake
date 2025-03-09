/**
 * Account screen implementation for the Handshake application.
 * This screen displays user profile information, betting records, and interaction controls.
 */
package com.bck.handshake

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bck.handshake.data.Bet
import com.bck.handshake.data.SupabaseHelper
import com.bck.handshake.data.sampleRecords
import com.bck.handshake.navigation.BottomNavBar
import com.bck.handshake.ui.theme.indieFlower

@Composable
fun AccountScreen(
    currentBets: List<Bet>,
    onNewBetClicked: () -> Unit,
    onRecordsClicked: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTab by remember { mutableStateOf(0) }
    var bets by remember { mutableStateOf(currentBets) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Function to fetch bets
    fun fetchBets() {
        isLoading = true
        SupabaseHelper.getUserBets { fetchedBets, fetchError ->
            isLoading = false
            if (fetchError != null) {
                error = fetchError
            } else {
                // Filter out completed bets
                bets = fetchedBets.filter { it.status != "completed" }
            }
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        fetchBets()
    }

    // Periodic refresh every 5 seconds
    LaunchedEffect(refreshTrigger) {
        kotlinx.coroutines.delay(5000)
        fetchBets()
        refreshTrigger++
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                BottomNavBar(
                    selectedIndex = 0,
                    onHomeSelected = { /* Already on Home screen */ },
                    onNewBetSelected = onNewBetClicked,
                    onRecordsSelected = onRecordsClicked
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (selectedTab) {
                    0 -> Tab1Content(
                        bets = bets,
                        onSignOut = onSignOut,
                        isLoading = isLoading,
                        error = error,
                        onBetUpdated = { fetchBets() }
                    )
                }
            }
        }
    }
}

@Composable
private fun Tab1Content(
    bets: List<Bet>,
    onSignOut: () -> Unit,
    isLoading: Boolean,
    error: String?,
    onBetUpdated: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Avatar
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "User image",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(86.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        )
        
        // Sign Out Button
        Button(
            onClick = onSignOut,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Sign Out")
        }
        
        // Betting Records
        Text(
            text = sampleRecords.formattedRecords,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.labelMedium
        )

        // Loading State
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // Active Bets Section
            if (bets.isNotEmpty()) {
                Text(
                    text = "Active Bets",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                bets.forEach { bet ->
                    BetCard(
                        bet = bet,
                        onBetUpdated = onBetUpdated
                    )
                }
            } else {
                Text(
                    text = "No active bets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BetCard(bet: Bet, onBetUpdated: () -> Unit = {}) {
    var isExpanded by remember { mutableStateOf(false) }
    var showOutcomeDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val currentUserId = remember { SupabaseHelper.getCurrentUserId() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "vs ${bet.participant.name}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = indieFlower
                        )
                    )
                    Text(
                        text = bet.statusDisplay,
                        color = when (bet.status) {
                            "pending" -> MaterialTheme.colorScheme.primary
                            "accepted" -> MaterialTheme.colorScheme.secondary
                            "rejected" -> MaterialTheme.colorScheme.error
                            "completed" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${bet.prideWagered} Pride",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Image(
                        painter = painterResource(id = bet.participant.avatar),
                        contentDescription = "Participant avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = bet.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Show action buttons based on bet status and user role
                    if (!isLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Participant actions
                            if (!bet.isCreator && bet.status == "pending") {
                                Button(
                                    onClick = {
                                        isLoading = true
                                        SupabaseHelper.updateBetStatus(bet.id, "accepted") { success, error ->
                                            isLoading = false
                                            if (!success) {
                                                errorMessage = error
                                            } else {
                                                onBetUpdated()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Accept")
                                }
                                Button(
                                    onClick = {
                                        isLoading = true
                                        SupabaseHelper.updateBetStatus(bet.id, "rejected") { success, error ->
                                            isLoading = false
                                            if (!success) {
                                                errorMessage = error
                                            } else {
                                                onBetUpdated()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Reject")
                                }
                            }

                            // Creator actions
                            if (bet.isCreator && bet.status == "accepted") {
                                Button(
                                    onClick = { showOutcomeDialog = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Declare Outcome")
                                }
                            }
                        }
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }

    if (showOutcomeDialog) {
        OutcomeDialog(
            onDismiss = { showOutcomeDialog = false },
            onOutcomeSelected = { outcome ->
                isLoading = true
                val (status, winnerId) = when (outcome) {
                    "they_won" -> "completed" to bet.participant.id
                    "i_won" -> "completed" to currentUserId
                    else -> "completed" to null // Draw
                }
                SupabaseHelper.updateBetStatus(bet.id, status, winnerId) { success, error ->
                    isLoading = false
                    if (success) {
                        showOutcomeDialog = false
                        onBetUpdated()
                    } else {
                        errorMessage = error
                    }
                }
            }
        )
    }
}

@Composable
private fun OutcomeDialog(
    onDismiss: () -> Unit,
    onOutcomeSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Declare Outcome") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Who won the bet?")
                Button(
                    onClick = { onOutcomeSelected("they_won") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("They Won")
                }
                Button(
                    onClick = { onOutcomeSelected("i_won") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I Won")
                }
                Button(
                    onClick = { onOutcomeSelected("draw") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Draw")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun Tab2Content() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tab 2 Content")
    }
}

@Composable
private fun Tab3Content() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tab 3 Content")
    }
}

