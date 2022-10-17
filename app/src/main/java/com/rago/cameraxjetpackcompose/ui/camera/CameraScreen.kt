package com.rago.cameraxjetpackcompose.ui.camera

import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.rago.cameraxjetpackcompose.executor
import com.rago.cameraxjetpackcompose.getCameraProvider
import com.rago.cameraxjetpackcompose.takePicture
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraScreen() {
    val emptyImageUri = Uri.parse("file://dev/null")
    var imageUri by remember { mutableStateOf(emptyImageUri) }
    if (imageUri != emptyImageUri) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Captured image"
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    imageUri = emptyImageUri
                }
            ) {
                Text("Remove image")
            }
        }
    } else
        CameraCapture(
            onImageFile = { file ->
                imageUri = file.toUri()
            }
        )
}

@Composable
private fun CameraPreview(
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onUseCase: (UseCase) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCapture(
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onImageFile: (File) -> Unit = { }
) {
    Scaffold(Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
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


            CameraPreview(
                onUseCase = {
                    previewUseCase = it
                }
            )
            Button(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                onClick = {
                    coroutineScope.launch {
                        onImageFile(imageCaptureUseCase.takePicture(context.executor))
                    }
                }
            ) {
                Text("Click!")
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