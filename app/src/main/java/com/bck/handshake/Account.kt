/**
 * Account screen implementation for the Handshake application.
 * This screen displays user profile information, betting records, and interaction controls.
 */
package com.bck.handshake

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bck.handshake.data.sampleRecords
import com.bck.handshake.navigation.BottomNavBar
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.bck.handshake.data.Bet
import androidx.compose.ui.res.painterResource
import com.bck.handshake.ui.theme.indieFlower
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable

@Composable
fun AccountScreen(
    currentBets: List<Bet>,
    onNewBetClicked: () -> Unit,
    onRecordsClicked: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

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
                    0 -> Tab1Content(currentBets, onSignOut)
                }
            }
        }
    }
}

@Composable
private fun Tab1Content(bets: List<Bet>, onSignOut: () -> Unit) {
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

        // Active Bets Section
        if (bets.isNotEmpty()) {
            Text(
                text = "Active Bets",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            bets.forEach { bet ->
                BetCard(bet = bet)
            }
        }
    }
}

@Composable
private fun BetCard(bet: Bet) {
    var isExpanded by remember { mutableStateOf(false) }

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
                    if (bet.isConfirmed) {
                        Text(
                            text = "Confirmed",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
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
                }
            }
        }
    }
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

