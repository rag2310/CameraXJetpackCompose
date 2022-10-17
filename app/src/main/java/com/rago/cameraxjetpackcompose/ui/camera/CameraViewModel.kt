package com.rago.cameraxjetpackcompose.ui.camera

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
            deleteFile = ::deleteFile,
            newFile = ::newFile,
            moreFile = ::moreFile
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
}