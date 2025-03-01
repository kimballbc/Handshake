package com.bck.handshake
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bck.handshake.data.sampleRecords


//navController.navigate(NewBetScreen().route)

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // create an Icon that used the Person icon from the Material Icons library.
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User image",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(86.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                )
                // Add the subheader.
                Text(
                    text = sampleRecords.formattedRecords,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
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

            // Add HandshakeSlider at the bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HandshakeSlider(
                    onConfirmed = onNewBetClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

