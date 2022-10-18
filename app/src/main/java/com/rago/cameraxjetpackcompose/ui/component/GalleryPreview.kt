package com.rago.cameraxjetpackcompose.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.rago.cameraxjetpackcompose.ui.camera.CameraUIState

@Composable
fun GalleryPreview(
    modifier: Modifier = Modifier, cameraUIState: CameraUIState
) {
    LazyRow(modifier = modifier) {
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