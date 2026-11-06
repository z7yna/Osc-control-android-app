package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FireControlUiState
import com.example.ui.FireControlViewModel
import com.example.ui.ScreenMode
import com.example.ui.components.CustomKeypad
import com.example.ui.screens.InputScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FireControlApp()
            }
        }
    }
}

@Composable
fun FireControlApp(
    viewModel: FireControlViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF06070C))
    ) {
        // Authentic 4K Minecraft Orbital Strike Cannon panorama wallpaper
        Image(
            painter = painterResource(id = R.drawable.bg_osc_panorama),
            contentDescription = "Minecraft OSC Panorama",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Refined Liquid Glass Atmospheric Layer: rich contrast while keeping panorama vividly visible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x9905060C),
                            Color(0x66080A14),
                            Color(0xBB030408)
                        )
                    )
                )
        )

        // Main App Scaffold
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tap outside keypad to dismiss
                if (state.activeField != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.dismissKeypad()
                            }
                    )
                }

                // Smooth Horizontal Slide & Fade Screen Transitions
                AnimatedContent(
                    targetState = state.screenMode,
                    transitionSpec = {
                        if (targetState == ScreenMode.RESULTS) {
                            (slideInHorizontally(animationSpec = tween(360)) { width -> width } + fadeIn(animationSpec = tween(360)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(360)) { width -> -width } + fadeOut(animationSpec = tween(260)))
                        } else {
                            (slideInHorizontally(animationSpec = tween(360)) { width -> -width } + fadeIn(animationSpec = tween(360)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(360)) { width -> width } + fadeOut(animationSpec = tween(260)))
                        }
                    },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        ScreenMode.INPUT -> {
                            InputScreen(
                                state = state,
                                onSelectField = { viewModel.selectField(it) },
                                onSetCannonVariant = { viewModel.setCannonVariant(it) },
                                onSetFireMode = { viewModel.setFireMode(it) },
                                onSetOriginPreset = { viewModel.setOriginPreset(it) },
                                onCalculate = { viewModel.calculateSolution() }
                            )
                        }
                        ScreenMode.RESULTS -> {
                            ResultsScreen(
                                state = state,
                                onBack = { viewModel.returnToInputs() }
                            )
                        }
                    }
                }

                // Docked In-App Liquid Glass Keypad
                CustomKeypad(
                    activeField = state.activeField,
                    currentValue = state.activeField?.let { state.getFieldValue(it) } ?: "",
                    onDigitPress = { viewModel.onDigitPress(it) },
                    onToggleSign = { viewModel.onToggleSign() },
                    onBackspace = { viewModel.onBackspace() },
                    onClear = { viewModel.onClear() },
                    onPreviousField = { viewModel.previousField() },
                    onNextField = { viewModel.nextField() },
                    onDismiss = { viewModel.dismissKeypad() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
