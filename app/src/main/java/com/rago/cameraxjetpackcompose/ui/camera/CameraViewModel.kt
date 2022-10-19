package com.rago.cameraxjetpackcompose.ui.camera

import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _cameraUIState: MutableStateFlow<CameraUIState> = MutableStateFlow(
        CameraUIState(
            imageCaptureUseCase = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetResolution(Size(720, 720)).build(),
            previewUseCase = Preview.Builder().build(),
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            deleteFile = ::deleteFile,
            newFile = ::newFile,
            moreFile = ::moreFile,
            onChangeFlash = ::onChangeFlash,
            onChangeCameraSelector = ::onChangeCameraSelect,
            setCameraSelect = ::setCameraSelect,
            onChangeNavBack = ::onChangeNavBack,
            sendImages = ::sendImages,
            onChangeCamera = ::onChangeCamera
        )
    )

    val cameraUIState: StateFlow<CameraUIState> = _cameraUIState.asStateFlow()

    private fun deleteFile() {
        viewModelScope.launch {
            val file = _cameraUIState.value.file
            _cameraUIState.update {
                it.copy(loading = true)
            }
            file?.let {
                if (file.exists()) {
                    file.delete()
                    _cameraUIState.update {
                        it.copy(file = null, loading = false)
                    }
                }
            }
        }
    }

    private fun newFile(file: File) {
        viewModelScope.launch {
            _cameraUIState.update {
                it.copy(file = file)
            }
        }
    }

    private fun moreFile() {
        viewModelScope.launch {
            val gallery = _cameraUIState.value.gallery
            val file = _cameraUIState.value.file
            val newGallery = mutableListOf<File>()
            file?.let {
                newGallery.add(it)
            }
            newGallery.addAll(gallery)
            _cameraUIState.update {
                it.copy(gallery = newGallery, file = null)
            }
        }
    }

    private fun onChangeFlash() {
        viewModelScope.launch {
            val flash = _cameraUIState.value.flash
            _cameraUIState.update {
                it.copy(flash = !flash)
            }
        }
    }

    private fun setCameraSelect(cameraSelector: CameraSelector) {
        viewModelScope.launch {
            _cameraUIState.update {
                it.copy(cameraSelector = cameraSelector)
            }
        }
    }

    private fun onChangeCameraSelect() {
        viewModelScope.launch {

            if (_cameraUIState.value.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                _cameraUIState.update {
                    it.copy(cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA)
                }
            } else {
                _cameraUIState.update {
                    it.copy(cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA)
                }
            }
        }
    }

    private fun onChangeNavBack(func: (List<File>) -> Unit) {
        viewModelScope.launch {
            _cameraUIState.update {
                it.copy(onNavBack = {
                    func(_cameraUIState.value.gallery)
                })
            }
        }
    }

    private fun sendImages() {
        viewModelScope.launch {
            val gallery = _cameraUIState.value.gallery
            val file = _cameraUIState.value.file
            val newGallery = mutableListOf<File>()
            file?.let {
                newGallery.add(it)
            }
            newGallery.addAll(gallery)
            _cameraUIState.update {
                it.copy(gallery = newGallery, navBack = true)
            }
        }
    }

    private fun onChangeCamera(camera: Camera) {
        viewModelScope.launch {
            _cameraUIState.update {
                it.copy(camera = camera)
            }
        }
    }
}