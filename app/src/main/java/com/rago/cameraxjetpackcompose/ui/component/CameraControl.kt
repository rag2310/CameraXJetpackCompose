package com.rago.cameraxjetpackcompose.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


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