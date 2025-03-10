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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bck.handshake.components.LoadingIndicator
import com.bck.handshake.components.ProfileDrawer
import com.bck.handshake.data.Bet
import com.bck.handshake.data.SupabaseHelper
import com.bck.handshake.data.sampleRecords
import com.bck.handshake.navigation.BottomNavBar
import com.bck.handshake.ui.theme.indieFlower
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    currentBets: List<Bet>,
    onNewBetClicked: () -> Unit,
    onRecordsClicked: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var bets by remember { mutableStateOf(currentBets) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Function to fetch bets
    suspend fun fetchBets() {
        isLoading = true
        SupabaseHelper.getUserBets().fold(
            onSuccess = { fetchedBets ->
                bets = fetchedBets.filter { it.status != "completed" && it.status != "rejected" }
                error = null
            },
            onFailure = { e ->
                error = e.message
            }
        )
        isLoading = false
    }

    // Initial load and when returning to screen
    LaunchedEffect(Unit) {
        fetchBets()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProfileDrawer(
                onSignOut = onSignOut,
                onDismiss = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp) // Account for bottom bar
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Top row with Profile Icon
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Open profile",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            }
                        }
                    }

                    // Active Bets Section with Refresh
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Bets",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            IconButton(
                                onClick = { scope.launch { fetchBets() } },
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh bets"
                                    )
                                }
                            }
                        }
                    }

                    // Error State
                    if (error != null) {
                        item {
                            Text(
                                text = error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Active Bets List
                    if (bets.isNotEmpty()) {
                        items(bets) { bet ->
                            BetCard(
                                bet = bet,
                                onBetUpdated = { scope.launch { fetchBets() } }
                            )
                        }
                    } else if (!isLoading) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.poor_gator),
                                    contentDescription = "Sad gator",
                                    modifier = Modifier.size(200.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "It looks like you don't have any current bets...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                        selectedIndex = 0,
                        onHomeSelected = { /* Already on Home screen */ },
                        onNewBetSelected = onNewBetClicked,
                        onRecordsSelected = onRecordsClicked,
                        modifier = Modifier
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
private fun BetCard(
    bet: Bet,
    onBetUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showOutcomeDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Define handlers at the beginning of the composable
    fun handleAccept() {
        scope.launch {
            isLoading = true
            errorMessage = null
            SupabaseHelper.updateBetStatus(bet.id, "accepted").fold(
                onSuccess = {
                    isLoading = false
                    onBetUpdated()
                },
                onFailure = { e ->
                    isLoading = false
                    errorMessage = e.message
                }
            )
        }
    }

    fun handleReject() {
        scope.launch {
            isLoading = true
            errorMessage = null
            SupabaseHelper.updateBetStatus(bet.id, "rejected").fold(
                onSuccess = {
                    isLoading = false
                    onBetUpdated()
                },
                onFailure = { e ->
                    isLoading = false
                    errorMessage = e.message
                }
            )
        }
    }

    fun handleComplete(outcome: String) {
        scope.launch {
            isLoading = true
            errorMessage = null
            val winnerId = when (outcome) {
                "they_won" -> bet.participant.id
                "i_won" -> SupabaseHelper.getCurrentUserId()
                else -> null  // Draw case
            }
            SupabaseHelper.updateBetStatus(bet.id, "completed", winnerId).fold(
                onSuccess = {
                    isLoading = false
                    showOutcomeDialog = false
                    onBetUpdated()
                },
                onFailure = { e ->
                    isLoading = false
                    errorMessage = e.message
                }
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.surfaceVariant
                bet.status == "completed" && bet.winnerId == SupabaseHelper.getCurrentUserId() -> MaterialTheme.colorScheme.primaryContainer
                bet.status == "completed" && bet.winnerId != null -> MaterialTheme.colorScheme.errorContainer
                bet.status == "accepted" -> MaterialTheme.colorScheme.secondaryContainer
                bet.status == "rejected" -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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

                        // Action buttons
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
                                        onClick = { handleAccept() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Accept")
                                    }
                                    Button(
                                        onClick = { handleReject() },
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

            // Loading indicator overlay
            if (isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    LoadingIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    if (showOutcomeDialog) {
        OutcomeDialog(
            onDismiss = { showOutcomeDialog = false },
            onOutcomeSelected = { outcome -> handleComplete(outcome) }
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

