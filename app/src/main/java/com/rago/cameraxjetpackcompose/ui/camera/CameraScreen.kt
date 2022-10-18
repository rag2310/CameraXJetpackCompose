package com.rago.cameraxjetpackcompose.ui.camera

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rago.cameraxjetpackcompose.ui.component.BackgroundPanelCameraControls
import com.rago.cameraxjetpackcompose.ui.component.CameraCapture
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraScreen(cameraUIState: CameraUIState) {

    LaunchedEffect(key1 = cameraUIState.navBack, block = {
        if (cameraUIState.navBack){
            cameraUIState.onNavBack()
        }
    })

    if (cameraUIState.loading) {
        AlertDialog(onDismissRequest = {}, text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }, confirmButton = {})
    }

    Crossfade(targetState = cameraUIState.file) { file ->
        if (file != null) {
            PreviewScreen(cameraUIState, file)
        }

        if (file == null) {
            CameraCapture(
                cameraUIState = cameraUIState
            )
        }
    }
}

@Composable
private fun PreviewScreen(cameraUIState: CameraUIState, file: File) {
    val model =
        ImageRequest.Builder(LocalContext.current).data(file.toUri()).size(coil.size.Size.ORIGINAL)
            .crossfade(true).build()


    val animationRotation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.5f))
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = rememberAsyncImagePainter(
                model,
            ),
            contentDescription = "Captured image",
        )
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(IntrinsicSize.Min)
                .navigationBarsPadding(),
        ) {
            BackgroundPanelCameraControls()
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.DarkGray.copy(alpha = 0.3f))
                            .clickable(onClick = cameraUIState.deleteFile),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.padding(5.dp)) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.DarkGray.copy(alpha = 0.3f))
                            .clickable(onClick = cameraUIState.sendImages),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.padding(10.dp)) {
                            Icon(
                                Icons.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(42.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.DarkGray.copy(alpha = 0.3f))
                            .clickable(onClick = {
                                coroutineScope.launch {
                                    animationRotation.animateTo(360f)
                                    cameraUIState.moreFile()
                                }
                            }), contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.padding(5.dp)) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .graphicsLayer {
                                        rotationZ = animationRotation.value
                                    },
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}