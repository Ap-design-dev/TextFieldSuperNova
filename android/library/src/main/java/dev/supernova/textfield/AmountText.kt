package dev.supernova.textfield

/** Whole rubles, stored as digits without separators or a currency sign. Zero is empty. */
object AmountText {
    fun normalize(input: String): String? {
        val text = input.trim().removeSuffix("₽")
        val digits = text.filterNot { it == ' ' || it == '\u00a0' || it == '\u202f' }
        if (digits.any { it !in '0'..'9' }) return null
        return digits.trimStart('0')
    }

    fun formatted(input: String): String {
        val digits = normalize(input).orEmpty()
        if (digits.isEmpty()) return "0"
        return buildString {
            digits.forEachIndexed { index, digit ->
                if (index > 0 && (digits.length - index) % 3 == 0) append('\u00a0')
                append(digit)
            }
        }
    }
}
