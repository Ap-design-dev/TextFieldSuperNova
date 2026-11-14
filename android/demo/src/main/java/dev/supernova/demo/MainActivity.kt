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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import dev.supernova.textfield.SuperNovaAmountField
import dev.supernova.textfield.SuperNovaCreditPotential
import dev.supernova.textfield.rememberSuperNovaAmountState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { DemoScreen() } }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DemoScreen() {
    val state = rememberSuperNovaAmountState()
    val credit = rememberSuperNovaAmountState("1500000")
    var showsInformation by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    Column(Modifier.fillMaxSize().background(Color.White)) {
        SuperNovaAmountField(state)
        SuperNovaCreditPotential(credit, onInformation = {
            focus.clearFocus()
            keyboard?.hide()
            showsInformation = true
        })
        Spacer(Modifier.fillMaxWidth().weight(1f).clickable {
            focus.clearFocus()
            keyboard?.hide()
        })
    }
    if (showsInformation) ModalBottomSheet(onDismissRequest = { showsInformation = false }) {
        Text("Дополнительная информация")
    }
}
