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
import androidx.compose.material3.CardDefaults
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
import com.bck.handshake.components.LoadingIndicator
import com.bck.handshake.data.Bet
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
    var userStats by remember { mutableStateOf<UserRecord?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val currentUserId = remember { SupabaseHelper.getCurrentUserId() }

    LaunchedEffect(Unit) {
        // Get user records
        val userId = SupabaseHelper.getCurrentUserId()
        if (userId != null) {
            try {
                userStats = try {
                    SupabaseHelper.supabase.postgrest.from("user_records")
                        .select() {
                            filter {
                                eq("user_id", userId)
                            }
                        }
                        .decodeSingle<UserRecord>()
                } catch (e: Exception) {
                    // User might not have a record yet, create a default one
                    UserRecord("", userId, 0, 0, 0, 0)
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }

        // Get completed bets
        SupabaseHelper.getUserBets().fold(
            onSuccess = { bets ->
                completedBets = bets.filter { it.status == "completed" }
                isLoading = false
            },
            onFailure = { e ->
                isLoading = false
                error = e.message ?: "Failed to load bets"
            }
        )
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
                    LoadingIndicator(
                        message = "Loading your records...",
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
                    // Pride Wallet Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pride Wallet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${userStats?.pride_balance ?: 0} Pride",
                                style = MaterialTheme.typography.headlineMedium,
                                color = if ((userStats?.pride_balance ?: 0) >= 0) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Stats Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                        text = (userStats?.wins ?: 0).toString(),
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
                                        text = (userStats?.draws ?: 0).toString(),
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
                                        text = (userStats?.losses ?: 0).toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            // Win Rate
                            val totalGames = (userStats?.wins ?: 0) + 
                                (userStats?.draws ?: 0) + 
                                (userStats?.losses ?: 0)
                            val winRate = if (totalGames > 0) {
                                (userStats?.wins ?: 0) * 100f / totalGames
                            } else 0f

                            Text(
                                text = "Win Rate: ${String.format("%.1f", winRate)}%",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    // Completed Bets Section
                    if (completedBets.isNotEmpty()) {
                        Text(
                            text = "Bet History",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(completedBets) { bet ->
                                CompletedBetCard(bet = bet, currentUserId = currentUserId)
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
private fun CompletedBetCard(bet: Bet, currentUserId: String?) {
    val isWinner = bet.winnerId == currentUserId
    val isDraw = bet.winnerId == null
    val backgroundColor = when {
        isDraw -> MaterialTheme.colorScheme.surfaceVariant
        isWinner -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
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
                Text(
                    text = when {
                        isDraw -> "Draw"
                        isWinner -> "You Won!"
                        else -> "You Lost"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = when {
                        isDraw -> MaterialTheme.colorScheme.secondary
                        isWinner -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    },
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
                if (!isDraw) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = if (isWinner) "You Won" else "They Won",
                        tint = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
} 