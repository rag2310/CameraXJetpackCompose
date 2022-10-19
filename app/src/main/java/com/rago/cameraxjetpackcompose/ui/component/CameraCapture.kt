package com.rago.cameraxjetpackcompose.ui.component

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import com.rago.cameraxjetpackcompose.getCameraProvider
import com.rago.cameraxjetpackcompose.takePicture
import com.rago.cameraxjetpackcompose.ui.camera.CameraUIState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCapture(
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
            val heightAllBoxActions = remember {
                mutableStateOf(0.dp)
            }
            val heightHalfBoxActions = remember {
                mutableStateOf(0.dp)
            }
            val configuration = LocalConfiguration.current
            var firstTime by remember {
                mutableStateOf(true)
            }

            LaunchedEffect(cameraUIState.previewUseCase) {
                if (!firstTime) {
                    startCamera(cameraUIState, context, lifecycleOwner)
                }
                if (firstTime) {
                    startCamera(cameraUIState, context, lifecycleOwner)
                    firstTime = false
                }
            }

            LaunchedEffect(key1 = cameraUIState.cameraSelector, block = {
                if (!firstTime) {
                    startCamera(cameraUIState, context, lifecycleOwner)
                }
            })

            LaunchedEffect(key1 = cameraUIState.flash, block = {
                cameraUIState.camera?.let {
                    if (it.cameraInfo.hasFlashUnit()) {
                        it.cameraControl.enableTorch(cameraUIState.flash)
                    }
                }
            })

            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(configuration.screenHeightDp.dp - heightAllBoxActions.value + paddingValues.calculateBottomPadding() + heightHalfBoxActions.value),
                onUseCase = {
                    cameraUIState.previewUseCase = it
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 15.dp, top = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .background(Color.DarkGray.copy(alpha = 0.3f))
                        .clickable(onClick = cameraUIState.onNavBack),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.padding(5.dp)) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            PanelCameraControls(
                cameraUIState = cameraUIState,
                heightAllBoxActions = heightAllBoxActions,
                heightHalfBoxActions = heightHalfBoxActions
            )
        }
    }
}

@Composable
private fun BoxScope.PanelCameraControls(
    cameraUIState: CameraUIState,
    heightAllBoxActions: MutableState<Dp> = mutableStateOf(0.dp),
    heightHalfBoxActions: MutableState<Dp> = mutableStateOf(0.dp)
) {
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
    ) {
        GalleryPreview(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            cameraUIState = cameraUIState
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .onGloballyPositioned { layoutCoordinates ->
                    heightAllBoxActions.value = with(density) {
                        layoutCoordinates.size.height.toDp()
                    }
                }
        ) {
            BackgroundPanelCameraControls(heightHalfBoxActions)
            CameraControls(cameraUIState = cameraUIState)
        }
    }
}

@Composable
fun BackgroundPanelCameraControls(
    heightHalfBoxActions: MutableState<Dp> = mutableStateOf(0.dp)
) {
    val density = LocalDensity.current

    Column(
        Modifier
            .fillMaxWidth()
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
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black)
        )
    }
}

@Composable
private fun BoxScope.CameraControls(
    cameraUIState: CameraUIState,
) {

    val coroutineScope = rememberCoroutineScope()
    val animationRotation = remember { Animatable(0f) }
    val context = LocalContext.current

    LaunchedEffect(key1 = cameraUIState.cameraSelector, block = {
        if (cameraUIState.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            animationRotation.animateTo(0f)
        }
        if (cameraUIState.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            animationRotation.animateTo(360f)
        }
    })

    Row(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(vertical = 10.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(Color.DarkGray.copy(alpha = 0.3f))
                    .clickable(onClick = cameraUIState.onChangeFlash),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.padding(5.dp)) {
                    Crossfade(targetState = cameraUIState.flash) { flash ->
                        if (flash) {
                            Icon(
                                Icons.Filled.FlashOff,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                        if (!flash) {
                            Icon(
                                Icons.Filled.FlashOn,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
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
                        cameraUIState.newFile(
                            cameraUIState.imageCaptureUseCase!!.takePicture(
                                context, cameraUIState.cameraSelector!!
                            )
                        )
                    }
                }
            )
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
                    .clickable(onClick = cameraUIState.onChangeCameraSelector),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.padding(5.dp)) {
                    Icon(
                        Icons.Filled.FlipCameraIos,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer {
                                rotationX = animationRotation.value
                            },
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private suspend fun startCamera(
    cameraUIState: CameraUIState,
    context: Context,
    lifecycleOwner: LifecycleOwner
) {
    val cameraProvider = context.getCameraProvider()
    try {
        // Must unbind the use-cases before rebinding them.
        cameraProvider.unbindAll()

        val hasCameraFront =
            cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        val hasCameraBack =
            cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)

        var cameraSelectorValidated: CameraSelector = cameraUIState.cameraSelector!!

        if (cameraUIState.cameraSelector.lensFacing == CameraSelector.LENS_FACING_BACK) {
            cameraSelectorValidated = if (hasCameraBack) {
                cameraUIState.cameraSelector
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
        }
        if (cameraUIState.cameraSelector.lensFacing == CameraSelector.LENS_FACING_FRONT) {
            cameraSelectorValidated = if (hasCameraFront) {
                cameraUIState.cameraSelector
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        }

        cameraUIState.setCameraSelect(cameraSelectorValidated)

        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelectorValidated,
            cameraUIState.previewUseCase,
            cameraUIState.imageCaptureUseCase
        )

        cameraUIState.onChangeCamera(camera)
    } catch (ex: Exception) {
        Log.e("CameraCapture", "Failed to bind camera use cases", ex)
    }
}