package com.rago.cameraxjetpackcompose.ui.home

import java.io.File

data class HomeUIState(
    val youHavePermissions: Boolean = true,
    val onNavCamera: () -> Unit = {},
    val setOnNavCamera: (onNavCamera: () -> Unit) -> Unit = {},
    val handleButton: () -> Unit = {},
    val gallery: List<File> = listOf(),
    val setGallery: (List<File>) -> Unit = {}
)