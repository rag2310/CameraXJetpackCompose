package com.rago.cameraxjetpackcompose.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
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

    Scaffold(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        floatingActionButton = {
            FloatingActionButton(onClick = homeUIState.handleButton) {
                Icon(Icons.Filled.Camera, contentDescription = null)
            }
        }) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(10.dp),
                content = {
                    items(homeUIState.gallery) { file ->
                        Box(modifier = Modifier.padding(10.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(model = file.toUri()),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp)),

                                )
                        }
                    }
                }
            )
        }
    }
}
