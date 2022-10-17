package com.rago.cameraxjetpackcompose.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rago.cameraxjetpackcompose.Utils

@Composable
fun HomeScreen(homeUIState: HomeUIState) {
    HomeContent(homeUIState = homeUIState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun HomeContent(homeUIState: HomeUIState = HomeUIState()) {

    // PERMISSION
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        val result = map.all {
            it.value
        }
        if (result) {
            homeUIState.handleButton()
        }
    }

    if (!homeUIState.youHavePermissions) {
        Dialog(onDismissRequest = {}) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(text = "Attention")
                    Text(
                        text = "You do not have permission",
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(onClick = {
                            launcher.launch(Utils.REQUIRED_PERMISSIONS_DEFAULT)
                        }) {
                            Text(text = "Accept")
                        }
                    }
                }
            }
        }
    }

    Scaffold(Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Button(onClick = homeUIState.handleButton) {
                Text(text = "Camera")
            }
        }
    }
}
