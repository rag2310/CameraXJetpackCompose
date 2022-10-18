package com.rago.cameraxjetpackcompose.ui.camera

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rago.cameraxjetpackcompose.ui.component.CameraCapture

@Composable
fun CameraScreen(cameraUIState: CameraUIState) {

    if (cameraUIState.loading) {
        AlertDialog(onDismissRequest = {}, text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }, confirmButton = {})
    }

    Crossfade(targetState = cameraUIState.file) { file ->
        if (file != null) {
            val model = ImageRequest.Builder(LocalContext.current)
                .data(file.toUri())
                .size(coil.size.Size.ORIGINAL)
                .crossfade(true)
                .build()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        model,
                    ),
                    contentDescription = "Captured image",
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = cameraUIState.deleteFile
                    ) {
                        Text("Delete")
                    }
                    Button(
                        onClick = cameraUIState.moreFile
                    ) {
                        Text("More")
                    }
                    Button(
                        onClick = {
//                            imageUri = emptyImageUri
                        }
                    ) {
                        Text("Send")
                    }
                }
            }
        }

        if (file == null) {
            CameraCapture(
                cameraUIState = cameraUIState
            )
        }
    }
}