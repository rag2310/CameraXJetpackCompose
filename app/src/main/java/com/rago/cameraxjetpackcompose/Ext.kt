package com.rago.cameraxjetpackcompose

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener({ continuation.run { resume(future.get()) } }, executor)
        }
    }

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)

suspend fun ImageCapture.takePicture(context: Context, cameraSelector: CameraSelector): File {
    val path = "${context.applicationInfo.dataDir}/images"
    val directory = File(path)

    if (!directory.exists()) {
        directory.mkdir()
    }

    val photoFile = createFile(directory, Constant.FILENAME)

    val metadata = ImageCapture.Metadata().apply {
        isReversedHorizontal =
            cameraSelector.lensFacing == CameraSelector.LENS_FACING_FRONT
    }

    return suspendCoroutine { continuation ->
        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(metadata).build()
        takePicture(outputOptions, context.executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                continuation.resume(photoFile)
            }

            override fun onError(ex: ImageCaptureException) {
                Log.e("TakePicture", "Image capture failed", ex)
                continuation.resumeWithException(ex)
            }
        })
    }
}

fun createFile(baseFolder: File, format: String) =
    File(
        baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + Constant.PHOTO_EXTENSION
    )