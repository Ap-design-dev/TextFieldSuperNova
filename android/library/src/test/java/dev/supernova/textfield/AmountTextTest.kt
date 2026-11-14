package dev.supernova.textfield

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmountTextTest {
    @Test fun acceptsGroupedIntegerPaste() {
        assertEquals("1000000", AmountText.normalize("1 000 000 ₽"))
        assertEquals("1000000", AmountText.normalize("1\u00a0000\u202f000 ₽"))
        assertEquals("12", AmountText.normalize("00012"))
        assertEquals("", AmountText.normalize("0"))
    }

    @Test fun refusesDecimalsNegativeAndExponent() {
        for (value in listOf("1.50", "1,50", "-10", "1e6", "₽100", "NaN")) {
            assertNull(value, AmountText.normalize(value))
        }
    }

    @Test fun groupsWithoutIntegerOverflow() {
        assertEquals("0", AmountText.formatted(""))
        assertEquals("1\u00a0000\u00a0000", AmountText.formatted("1000000"))
        assertEquals("123\u00a0456\u00a0789\u00a0012\u00a0345\u00a0678\u00a0901", AmountText.formatted("123456789012345678901"))
    }
}
