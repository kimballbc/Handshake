package com.bck.handshake.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

val navigationItems = listOf(
    NavigationItem("Home", Icons.Default.Home),
    NavigationItem("New Bet", Icons.Default.Add),
    NavigationItem("Records", Icons.Default.EmojiEvents)
)

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onHomeSelected: () -> Unit,
    onNewBetSelected: () -> Unit,
    onRecordsSelected: () -> Unit
) {
    NavigationBar {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedIndex == index,
                onClick = {
                    when (index) {
                        0 -> onHomeSelected()
                        1 -> onNewBetSelected()
                        2 -> onRecordsSelected()
                    }
                }
            )
        }
    }
} 