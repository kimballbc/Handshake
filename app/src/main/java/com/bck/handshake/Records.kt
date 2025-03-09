package com.bck.handshake

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bck.handshake.data.Bet
import com.bck.handshake.data.Records
import com.bck.handshake.data.SupabaseHelper
import com.bck.handshake.data.UserRecord
import com.bck.handshake.navigation.BottomNavBar
import com.bck.handshake.ui.theme.indieFlower
import io.github.jan.supabase.postgrest.postgrest

@Composable
fun RecordsScreen(
    onHomeClicked: () -> Unit,
    onNewBetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var completedBets by remember { mutableStateOf<List<Bet>>(emptyList()) }
    var userRecords by remember { mutableStateOf<Records?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Get user records
        val userId = SupabaseHelper.getCurrentUserId()
        if (userId != null) {
            try {
                val record = try {
                    SupabaseHelper.supabase.postgrest.from("user_records")
                        .select() {
                            filter {
                                eq("user_id", userId)
                            }
                        }
                        .decodeSingle<UserRecord>()
                } catch (e: Exception) {
                    // User might not have any records yet
                    null
                }

                userRecords = Records(
                    wins = (record?.wins ?: 0).toString(),
                    draws = (record?.draws ?: 0).toString(),
                    loss = (record?.losses ?: 0).toString()
                )
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }

        // Get completed bets
        SupabaseHelper.getUserBets { bets, fetchError ->
            if (fetchError != null) {
                error = fetchError
            } else {
                completedBets = bets.filter { it.status == "completed" }
            }
            isLoading = false
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                BottomNavBar(
                    selectedIndex = 2,
                    onHomeSelected = onHomeClicked,
                    onNewBetSelected = onNewBetClicked,
                    onRecordsSelected = { /* Already on Records screen */ }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Records",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 32.dp)
                    )
                } else if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                } else {
                    // Records Summary
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Wins",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = userRecords?.wins ?: "0",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Draws",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = userRecords?.draws ?: "0",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Losses",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = userRecords?.loss ?: "0",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Completed Bets
                    if (completedBets.isNotEmpty()) {
                        Text(
                            text = "Completed Bets",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(completedBets) { bet ->
                                CompletedBetCard(bet = bet)
                            }
                        }
                    } else {
                        Text(
                            text = "No completed bets yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedBetCard(bet: Bet) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "vs ${bet.participant.name}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = indieFlower
                    )
                )
                Text(
                    text = bet.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${bet.prideWagered} Pride",
                    style = MaterialTheme.typography.labelMedium
                )
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Winner",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
} 