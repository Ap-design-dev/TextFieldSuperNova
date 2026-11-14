package dev.supernova.textfield

import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SuperNovaAmountFieldTest {
    @get:Rule val compose = createComposeRule()

    @Test fun presetsAndClearKeepFocus() {
        lateinit var state: SuperNovaAmountState
        compose.setContent {
            state = rememberSuperNovaAmountState()
            SuperNovaAmountField(state)
        }
        compose.onNodeWithTag("supernova.action").performClick()
        compose.onNodeWithTag("supernova.amount").assertIsFocused().performTextInput("1")
        compose.runOnIdle { assertEquals("1", state.amount) }
        compose.onNodeWithTag("supernova.suggestion.1").performClick()
        compose.onNodeWithTag("supernova.suggestion.1").assertIsSelected()
        compose.onNodeWithTag("supernova.amount").assertIsFocused()
        compose.runOnIdle { assertEquals("1000000", state.amount) }
        compose.onNodeWithTag("supernova.action").performClick()
        compose.onNodeWithTag("supernova.amount").assertIsFocused()
        compose.runOnIdle { assertEquals("", state.amount) }
    }

    @Test fun validationDoesNotDisappearWhenEditing() {
        compose.setContent {
            SuperNovaAmountField(rememberSuperNovaAmountState(), validation = SuperNovaValidation.Error("Текст приложения"))
        }
        compose.onNodeWithTag("supernova.action").performClick()
        compose.onNodeWithTag("supernova.amount").performTextInput("123")
        compose.onNodeWithTag("supernova.message").assertTextEquals("Текст приложения")
    }

    @Test fun readOnlyPresetsCannotChangeAmount() {
        lateinit var state: SuperNovaAmountState
        compose.setContent {
            state = rememberSuperNovaAmountState("123")
            SuperNovaAmountField(state, mode = SuperNovaMode.ReadOnly)
        }
        compose.onNodeWithTag("supernova.suggestion.1").assertIsNotEnabled().performTouchInput { click() }
        compose.runOnIdle { assertEquals("123", state.amount) }
    }
}
