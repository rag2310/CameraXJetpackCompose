package com.rago.cameraxjetpackcompose.ui.camera

import java.io.File

data class CameraUIState(
    val loading: Boolean = false,
    val file: File? = null,
    val gallery: List<File> = listOf(),
    val moreFile: () -> Unit = {},
    val newFile: (File) -> Unit = {},
    val deleteFile: () -> Unit = {}
)