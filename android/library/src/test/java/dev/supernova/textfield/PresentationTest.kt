package dev.supernova.textfield

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class PresentationTest {
    @Test fun creditHiddenBlocksKeepOnlyBottomSpacing() {
        val spacing = SuperNovaCreditSpacing()
        val cases = listOf(
            (true to true) to 138, (true to false) to 106,
            (false to true) to 118, (false to false) to 82,
        )
        for ((visibility, expected) in cases) {
            val (text, button) = visibility
            val gap = SuperNovaCreditPresentation.afterAmount(text, button, spacing).value
            val actual = 50 + gap + (if (text) 20 else 0) + (if (button) 32 else 0) + spacing.bottom.value
            assertEquals(visibility.toString(), expected.toFloat(), actual, 0f)
        }
    }

    @Test fun hiddenElementHeightsMatchFigmaSection2() {
        val cases = listOf(
            Triple(true, true, true) to 186, Triple(false, true, true) to 162,
            Triple(true, false, true) to 166, Triple(false, false, true) to 146,
            Triple(true, true, false) to 142, Triple(true, false, false) to 122,
            Triple(false, true, false) to 118, Triple(false, false, false) to 98,
        )
        for ((visibility, expected) in cases) {
            val (label, description, suggestions) = visibility
            val top = SuperNovaPresentation.beforeAmount(label, description, suggestions, SuperNovaSpacing()).value
            val actual = 48 + (if (label) 20 else 0) + top + 50 +
                (if (description) 20 else 0) + (if (suggestions) 44 else 0)
            assertEquals(visibility.toString(), expected.toFloat(), actual, 0f)
        }
    }

    @Test fun presetSelectionTracksNumericValue() {
        assertTrue(SuperNovaPresentation.isSelected("1000000", "1 000 000 ₽"))
        assertFalse(SuperNovaPresentation.isSelected("1000001", "1000000"))
        assertFalse(SuperNovaPresentation.isSelected("", "0"))
    }

    @Test fun onlyReadOnlyFeedbackHidesPresets() {
        assertTrue(SuperNovaPresentation.showsSuggestions(SuperNovaMode.ReadOnly, SuperNovaValidation.None))
        assertFalse(SuperNovaPresentation.showsSuggestions(SuperNovaMode.ReadOnly, SuperNovaValidation.Error("Error")))
        assertFalse(SuperNovaPresentation.showsSuggestions(SuperNovaMode.ReadOnly, SuperNovaValidation.Warning("Warning")))
        assertTrue(SuperNovaPresentation.showsSuggestions(SuperNovaMode.Disabled, SuperNovaValidation.None))
        assertTrue(SuperNovaPresentation.showsSuggestions(SuperNovaMode.Editable, SuperNovaValidation.Error("Error")))
    }
}
