package com.rago.cameraxjetpackcompose.ui.home

data class HomeUIState(
    val youHavePermissions: Boolean = true,
    val onNavCamera: () -> Unit = {},
    val setOnNavCamera: (onNavCamera: () -> Unit) -> Unit = {},
    val handleButton: () -> Unit = {}
)