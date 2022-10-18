package com.rago.cameraxjetpackcompose.ui.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.UseCase
import androidx.navigation.NavHostController
import java.io.File

data class CameraUIState(
    val loading: Boolean = false,
    val file: File? = null,
    val gallery: List<File> = listOf(),
    val moreFile: () -> Unit = {},
    val newFile: (File) -> Unit = {},
    val deleteFile: () -> Unit = {},
    val flash: Boolean = false,
    val onChangeFlash: () -> Unit = {},
    val cameraSelector: CameraSelector? = null,
    val onChangeCameraSelector: () -> Unit = {},
    val setCameraSelect: (CameraSelector) -> Unit = {},
    val imageCaptureUseCase: ImageCapture? = null,
    var previewUseCase: UseCase? = null,
    var onNavBack: () -> Unit = {},
    var onChangeNavBack: ((gallery: List<File>) -> Unit) -> Unit = {},
    val sendImages: () -> Unit = {},
    val navBack: Boolean = false
)