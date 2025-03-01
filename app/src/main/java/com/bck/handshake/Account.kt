/**
 * Account screen implementation for the Handshake application.
 * This screen displays user profile information, betting records, and interaction controls.
 */
package com.bck.handshake

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bck.handshake.data.sampleRecords
import kotlinx.coroutines.launch


//navController.navigate(NewBetScreen().route)

/**
 * Composable function that creates the Account screen UI.
 * 
 * The Account screen consists of:
 * - A user profile section with avatar and betting records
 * - A "New Bet" button for initiating new bets
 * - A HandshakeSlider component for confirming actions
 *
 * @param onNewBetClicked Callback function triggered when the user initiates a new bet
 * @param modifier Optional modifier for customizing the screen's layout
 */
@Composable
fun AccountScreen(
    onNewBetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // User Profile Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                // Betting Records
                Text(
                    text = sampleRecords.formattedRecords,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                // New Bet Button
                Button(
                    onClick = onNewBetClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(text = "New Bet")
                }
            }

            // Confirmation Slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp),
                contentAlignment = Alignment.Center
            ) {
                HandshakeSlider(
                    onConfirmed = onNewBetClicked
                )
            }
        }
    }
}

