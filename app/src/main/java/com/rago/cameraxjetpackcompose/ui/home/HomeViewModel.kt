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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val utils: Utils
) : ViewModel() {

    private val _homeUIState: MutableStateFlow<HomeUIState> = MutableStateFlow(
        HomeUIState(
            setOnNavCamera = ::setOnNavCamera,
            handleButton = ::handleButton
        )
    )
    val homeUIState: StateFlow<HomeUIState> = _homeUIState.asStateFlow()

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
}