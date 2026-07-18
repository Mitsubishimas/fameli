package com.fameli.budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fameli.budget.ui.navigation.FameliNavHost
import com.fameli.budget.ui.screens.splash.SplashScreen
import com.fameli.budget.ui.theme.FameliTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FameliTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) SplashScreen { showSplash = false }
                else Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { FameliNavHost() }
            }
        }
    }
}
