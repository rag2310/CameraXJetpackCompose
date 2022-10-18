package com.rago.cameraxjetpackcompose.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.cameraxjetpackcompose.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val utils: Utils
) : ViewModel() {

    private val _homeUIState: MutableStateFlow<HomeUIState> = MutableStateFlow(
        HomeUIState(
            setOnNavCamera = ::setOnNavCamera,
            handleButton = ::handleButton,
            setGallery = ::setGallery
        )
    )
    val homeUIState: StateFlow<HomeUIState> = _homeUIState.asStateFlow()

    init {
        viewModelScope.launch {
            _homeUIState.update {
                it.copy(gallery = utils.getFiles())
            }
        }
    }

    private fun setOnNavCamera(onNavCamera: () -> Unit) {
        viewModelScope.launch {
            _homeUIState.update {
                it.copy(onNavCamera = onNavCamera)
            }
        }
    }

    private fun handleButton() {
        viewModelScope.launch {
            Log.i("HomeViewModel", "handleButton: ${utils.allPermissionsGranted()}")
            when {
                !utils.allPermissionsGranted() -> {
                    _homeUIState.update {
                        it.copy(youHavePermissions = false)
                    }
                }
                else -> {
                    _homeUIState.value.onNavCamera()
                }
            }
        }
    }

    private fun setGallery(list: List<File>) {
        viewModelScope.launch {
            val oldGallery = _homeUIState.value.gallery
            val newGallery = mutableListOf<File>()
            newGallery.addAll(oldGallery)
            newGallery.addAll(list)
            _homeUIState.update {
                it.copy(gallery = newGallery)
            }
        }
    }
}