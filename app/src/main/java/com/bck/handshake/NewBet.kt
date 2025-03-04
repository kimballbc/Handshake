package com.bck.handshake

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bck.handshake.navigation.BottomNavBar
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

/**
 * NewBet screen implementation.
 * This screen provides the interface for creating and confirming new bets.
 */
@Composable
fun NewBetScreen(
    onConfirmed: () -> Unit,
    onHomeClicked: () -> Unit,
    onRecordsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Confirm New Bet",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                HandshakeSlider(
                    onConfirmed = onConfirmed
                )
            }
        }
    }
} 