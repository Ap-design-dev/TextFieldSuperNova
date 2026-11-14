package dev.supernova.textfield

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PresentationTest {
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
