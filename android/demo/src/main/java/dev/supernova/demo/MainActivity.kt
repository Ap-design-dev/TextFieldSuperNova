package dev.supernova.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import dev.supernova.textfield.SuperNovaAmountField
import dev.supernova.textfield.rememberSuperNovaAmountState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { DemoScreen() } }
    }
}

@Composable
private fun DemoScreen() {
    val state = rememberSuperNovaAmountState()
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    Column(Modifier.fillMaxSize().background(Color.White)) {
        SuperNovaAmountField(state)
        Spacer(Modifier.fillMaxWidth().weight(1f).clickable {
            focus.clearFocus()
            keyboard?.hide()
        })
    }
}
