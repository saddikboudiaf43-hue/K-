package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.ui.AppViewModel
import com.example.ui.MainAppView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: AppViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeStyle by viewModel.currentTheme.collectAsStateWithLifecycle()
      val fontScale by viewModel.fontScale.collectAsStateWithLifecycle()
      val highContrast by viewModel.highContrastEnabled.collectAsStateWithLifecycle()
      
      MyApplicationTheme(themeStyle = themeStyle, highContrast = highContrast) {
        val currentDensity = LocalDensity.current
        val customDensity = Density(
          density = currentDensity.density,
          fontScale = currentDensity.fontScale * fontScale
        )
        CompositionLocalProvider(LocalDensity provides customDensity) {
          MainAppView(viewModel)
        }
      }
    }
  }
}
