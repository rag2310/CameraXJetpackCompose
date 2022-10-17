package com.rago.cameraxjetpackcompose.ui.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CameraScreen() {
    CameraContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraContent() {
    Scaffold(Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Camera")
        }
    }
}
