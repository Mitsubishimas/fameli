package com.fameli.budget.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var start by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (start) 1f else 0f, tween(1000))
    val scale by animateFloatAsState(if (start) 1f else 0.3f, tween(1000))
    LaunchedEffect(Unit) { start = true; delay(1500); onFinished() }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(alpha).scale(scale)) {
            Text("💰", style = MaterialTheme.typography.displayLarge)
            Text("Семейный Бюджет", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
