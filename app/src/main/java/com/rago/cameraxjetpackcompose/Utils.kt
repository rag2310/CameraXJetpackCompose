package com.rago.cameraxjetpackcompose

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject

class Utils @Inject constructor(
    private val context: Context
) {

    companion object {
        val REQUIRED_PERMISSIONS_DEFAULT = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
        }.toTypedArray()
    }

    fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS_DEFAULT.all {
        ContextCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}