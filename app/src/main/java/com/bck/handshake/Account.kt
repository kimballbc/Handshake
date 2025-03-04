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

@Composable
fun AccountScreen(
    onNewBetClicked: () -> Unit,
    onRecordsClicked: () -> Unit,
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
                    0 -> Tab1Content()
                }
            }
        }
    }
}

@Composable
private fun Tab1Content() {
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

