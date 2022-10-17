package com.rago.cameraxjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rago.cameraxjetpackcompose.ui.camera.CameraScreen
import com.rago.cameraxjetpackcompose.ui.camera.CameraViewModel
import com.rago.cameraxjetpackcompose.ui.home.HomeScreen
import com.rago.cameraxjetpackcompose.ui.home.HomeViewModel
import com.rago.cameraxjetpackcompose.ui.theme.CameraXJetpackComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CameraXJetpackComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {
    val navHostController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navHostController, startDestination = Routes.Home.route) {
            composable(Routes.Home.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val homeUIState by homeViewModel.homeUIState.collectAsState()
                LaunchedEffect(key1 = Unit, block = {
                    homeUIState.setOnNavCamera {
                        navHostController.navigate(Routes.Camera.route)
                    }
                })
                HomeScreen(homeUIState = homeUIState)
            }
            composable(Routes.Camera.route) {
                val cameraViewModel: CameraViewModel = hiltViewModel()
                val cameraUIState by cameraViewModel.cameraUIState.collectAsState()
                CameraScreen(cameraUIState = cameraUIState)
            }
        }
    }
}

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Camera : Routes("camera")
}