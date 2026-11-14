package dev.supernova.textfield

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class SuperNovaMode { Editable, ReadOnly, Disabled, Skeleton }

/** The host owns validation and its lifetime; typing and presets do not clear it. */
sealed interface SuperNovaValidation {
    data object None : SuperNovaValidation
    data class Error(val text: String) : SuperNovaValidation
    data class Warning(val text: String) : SuperNovaValidation
}

data class SuperNovaSpacing(
    val vertical: Dp = 24.dp,
    val afterLabel: Dp = 4.dp,
    val afterAmount: Dp = 4.dp,
    val beforeSuggestions: Dp = 12.dp,
    val afterSuggestions: Dp = 4.dp,
    val betweenSuggestions: Dp = 10.dp,
)

internal object SuperNovaPresentation {
    fun showsSuggestions(mode: SuperNovaMode, validation: SuperNovaValidation): Boolean =
        mode != SuperNovaMode.ReadOnly || validation == SuperNovaValidation.None

    fun isSelected(amount: String, suggestion: String): Boolean {
        val normalized = AmountText.normalize(amount)
        return !normalized.isNullOrEmpty() && normalized == AmountText.normalize(suggestion)
    }
}
