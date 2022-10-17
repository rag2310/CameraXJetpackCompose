package com.rago.cameraxjetpackcompose.ui.camera

import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rago.cameraxjetpackcompose.getCameraProvider
import com.rago.cameraxjetpackcompose.takePicture
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(cameraUIState: CameraUIState) {

    LaunchedEffect(key1 = cameraUIState.gallery.size, block = {
        Log.i("CameraScreen", "CameraScreen: ${cameraUIState.gallery.size}")
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

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onUseCase: (UseCase) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            onUseCase(
                Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
            )

            previewView
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCapture(
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    cameraUIState: CameraUIState
) {
    Scaffold(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.Yellow)
    ) { paddingValues ->
        Log.i("CameraScreen", "padding = $paddingValues")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
            val imageCaptureUseCase by remember {
                mutableStateOf(
                    ImageCapture.Builder()
                        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setTargetResolution(Size(720, 720))
                        .build()
                )
            }
            val coroutineScope = rememberCoroutineScope()
            val heightAllBoxActions = remember {
                mutableStateOf(0.dp)
            }
            val heightHalfBoxActions = remember {
                mutableStateOf(0.dp)
            }
            val configuration = LocalConfiguration.current
            val density = LocalDensity.current

            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(configuration.screenHeightDp.dp - heightAllBoxActions.value + paddingValues.calculateBottomPadding() + heightHalfBoxActions.value),
                onUseCase = {
                    previewUseCase = it
                }
            )
            GalleryPreview(cameraUIState = cameraUIState)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned { layoutCoordinates ->
                        heightAllBoxActions.value = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    }
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .background(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.Transparent)
                            .onGloballyPositioned { layoutCoordinates ->
                                heightHalfBoxActions.value = with(density) {
                                    layoutCoordinates.size.height.toDp()
                                }
                            }
                    ) {
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.Black)
                    ) {

                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CameraControl(
                        Icons.Sharp.Lens,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(1.dp)
                            .border(1.dp, Color.White, CircleShape),
                        effect = true,
                        onClick = {
                            coroutineScope.launch {
                                cameraUIState.newFile(imageCaptureUseCase.takePicture(context))
                            }
                        }
                    )
                }
            }

            LaunchedEffect(previewUseCase) {
                val cameraProvider = context.getCameraProvider()
                try {
                    // Must unbind the use-cases before rebinding them.
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                    )
                } catch (ex: Exception) {
                    Log.e("CameraCapture", "Failed to bind camera use cases", ex)
                }
            }
        }
    }
}

@Composable
private fun GalleryPreview(cameraUIState: CameraUIState) {
    LazyRow {
        items(cameraUIState.gallery) { file ->
            Box(modifier = Modifier.padding(horizontal = 10.dp)) {
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
}

@Composable
fun CameraControl(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    effect: Boolean = false,
    onClick: () -> Unit
) {
    if (effect) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsPressedAsState()
        val color = if (isFocused) Color.Red else Color.Transparent

        IconButton(
            onClick = onClick,
            modifier = modifier,
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector,
                contentDescription = null,
                modifier = modifier,
                tint = color
            )
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier,
        ) {
            Icon(
                imageVector,
                contentDescription = null,
                modifier = modifier,
                tint = Color.White
            )
        }
    }
}