package dev.supernova.textfield

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange

@Stable
class SuperNovaAmountState internal constructor(internal val field: TextFieldState) {
    /** Observable from composition or snapshotFlow; no integer overflow or floating point. */
    val amount: String get() = AmountText.normalize(field.text.toString()).orEmpty()

    /** Replaces the amount and puts the caret at its end without changing focus. */
    fun setAmount(value: String) {
        val normalized = requireNotNull(AmountText.normalize(value)) { "Expected whole rubles" }
        val display = normalized.ifEmpty { "0" }
        field.edit {
            replace(0, length, display)
            selection = TextRange(length)
        }
    }

    fun clear() = setAmount("")

    internal fun moveCaretToEnd() {
        field.edit { selection = TextRange(length) }
    }
}

/** Retains the amount and selection across recomposition and saved-state recreation. */
@Composable
fun rememberSuperNovaAmountState(initialAmount: String = ""): SuperNovaAmountState {
    val initial = requireNotNull(AmountText.normalize(initialAmount)) { "Expected whole rubles" }
    val field = rememberTextFieldState(initialText = initial.ifEmpty { "0" })
    return remember(field) { SuperNovaAmountState(field) }
}
