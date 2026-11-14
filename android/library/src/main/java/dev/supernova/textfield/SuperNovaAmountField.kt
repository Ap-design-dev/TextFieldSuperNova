package dev.supernova.textfield

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private val Ink = Color(0xFF1F1F22)
private val TextFont = FontFamily(Font(R.font.sb_sans_text_regular))
private val DisplayFont = FontFamily(Font(R.font.sb_sans_display_semibold, FontWeight.SemiBold))

private val WholeRubles = InputTransformation {
    val candidate = asCharSequence().toString()
    val normalized = AmountText.normalize(candidate)
    if (normalized == null) {
        revertAllChanges()
    } else {
        val display = normalized.ifEmpty { "0" }
        if (candidate != display) {
            val preceding = candidate.take(selection.end).count { it in '0'..'9' }
            val leadingZeros = candidate.count { it in '0'..'9' } - normalized.length
            replace(0, length, display)
            selection = TextRange(if (normalized.isEmpty()) 1 else (preceding - leadingZeros).coerceIn(0, length))
        }
    }
}

private val GroupThousands = OutputTransformation {
    var index = length - 3
    while (index > 0) {
        insert(index, "\u00a0")
        index -= 3
    }
}

/**
 * Seventeen visual variants derive from mode, validation, focus and amount.
 * Presets and clear keep the IME open. Validation belongs to the host.
 * The host can finish editing with LocalFocusManager.current.clearFocus().
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuperNovaAmountField(
    state: SuperNovaAmountState,
    modifier: Modifier = Modifier,
    label: String? = "Label",
    description: String? = "Description.",
    suggestions: List<String> = listOf("300000", "1000000", "3000000"),
    mode: SuperNovaMode = SuperNovaMode.Editable,
    validation: SuperNovaValidation = SuperNovaValidation.None,
    showChevron: Boolean = true,
    showClearButton: Boolean = true,
    spacing: SuperNovaSpacing = SuperNovaSpacing(),
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    var focused by remember { mutableStateOf(false) }
    var hadKeyboard by remember { mutableStateOf(false) }
    val canEdit = mode == SuperNovaMode.Editable
    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeVisible, focused) {
        if (focused && imeVisible) hadKeyboard = true
        if (focused && hadKeyboard && !imeVisible) focusManager.clearFocus()
        if (!focused) hadKeyboard = false
    }
    LaunchedEffect(mode) {
        if (!canEdit && focused) {
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }

    val startEditing: () -> Unit = {
        if (canEdit) {
            state.moveCaretToEnd()
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }
    val smallText = TextStyle(
        fontFamily = TextFont, fontSize = 14.sp, color = Ink,
        platformStyle = PlatformTextStyle(includeFontPadding = false), textAlign = TextAlign.Center,
    )
    if (mode == SuperNovaMode.Skeleton) {
        SuperNovaSkeleton(label != null, description != null, suggestions.size, modifier, spacing)
        return
    }
    val message = when {
        mode == SuperNovaMode.Disabled -> description
        validation is SuperNovaValidation.Error -> validation.text
        validation is SuperNovaValidation.Warning -> validation.text
        else -> description
    }
    val messageColor = when {
        mode == SuperNovaMode.Disabled -> Ink.copy(alpha = 0.35f)
        validation is SuperNovaValidation.Error -> Color(0xFFD10032)
        validation is SuperNovaValidation.Warning -> Color(0xFFEF4C01)
        else -> Ink.copy(alpha = 0.65f)
    }
    Column(modifier.padding(vertical = spacing.vertical), horizontalAlignment = Alignment.CenterHorizontally) {
        if (label != null) Text(label, modifier = Modifier.fillMaxWidth(), style = smallText.copy(
            lineHeight = 20.sp, color = if (mode == SuperNovaMode.Disabled) Ink.copy(alpha = 0.35f) else Ink))
        Spacer(Modifier.height(spacing.afterLabel))
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val density = LocalDensity.current
            val measurer = rememberTextMeasurer()
            val unscaled = TextStyle(
                fontFamily = DisplayFont, fontWeight = FontWeight.SemiBold,
                fontSize = 42.sp, lineHeight = 50.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )
            val display = AmountText.formatted(state.amount)
            val fullWidth = measurer.measure("$display ₽", unscaled).size.width
            val showAction = canEdit && (if (focused) showClearButton else showChevron)
            val available = with(density) { (maxWidth - if (showAction) 64.dp else 32.dp).toPx() }.coerceAtLeast(1f)
            val scale = (available / fullWidth.coerceAtLeast(1)).coerceIn(0.5f, 1f)
            val color = when {
                mode == SuperNovaMode.Disabled -> Ink.copy(alpha = 0.35f)
                state.amount.isEmpty() -> Ink.copy(alpha = 0.65f)
                else -> Ink
            }
            val amountStyle = unscaled.copy(fontSize = (42 * scale).sp, color = color)
            val currencyWidth = measurer.measure("₽", amountStyle).size.width
            val availableNumber = with(density) { (available - currencyWidth).toDp() } - 4.dp
            val numberWidth = (with(density) { measurer.measure(display, amountStyle).size.width.toDp() } + 3.dp)
                .coerceAtMost(availableNumber.coerceAtLeast(1.dp))
            val lineHeight = with(density) { 50.sp.toDp() }
            Row(Modifier.fillMaxWidth().height(lineHeight), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                // A matching leading slot keeps digits + ₽ centered independently of the action.
                if (showAction) Spacer(Modifier.width(30.dp))
                if (canEdit) BasicTextField(
                    state = state.field,
                    modifier = Modifier
                        .width(numberWidth)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            focused = it.isFocused
                            if (it.isFocused) state.moveCaretToEnd()
                        }
                        .caretAtEndOnTap(state)
                        .testTag("supernova.amount")
                        .semantics { contentDescription = label ?: "Сумма в рублях" },
                    textStyle = amountStyle,
                    cursorBrush = SolidColor(Ink),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = WholeRubles,
                    outputTransformation = GroupThousands,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    // With the state-based API, default Done handling hides the keyboard.
                    // The IME observer above then clears focus, restoring the chevron.
                ) else Text(display, style = amountStyle, maxLines = 1,
                    modifier = Modifier.width(numberWidth).horizontalScroll(rememberScrollState()).testTag("supernova.amount"))
                Spacer(Modifier.width(4.dp))
                Text("₽", style = amountStyle, modifier = Modifier
                    .focusProperties { canFocus = false }
                    .clickable(enabled = canEdit, onClick = startEditing))
                if (showAction) {
                  Spacer(Modifier.width(6.dp))
                  Box(
                    Modifier.width(24.dp).height(lineHeight)
                        .focusProperties { canFocus = false }
                        .clickable(role = Role.Button) {
                            if (focused) state.clear()
                            startEditing()
                        }
                        .testTag("supernova.action")
                        .semantics { contentDescription = if (focused) "Очистить сумму" else "Редактировать сумму" },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painterResource(if (focused) R.drawable.sc_srv_cross_stroke_16 else R.drawable.sc_srv_caretright_stroke_16),
                        contentDescription = null,
                        modifier = if (focused) Modifier.size(16.dp) else Modifier.size(14.dp, 15.dp),
                    )
                  }
                }
            }
        }
        Spacer(Modifier.height(spacing.afterAmount))
        if (description != null && message != null) Text(message,
            modifier = Modifier.fillMaxWidth().testTag("supernova.message").semantics { liveRegion = LiveRegionMode.Polite },
            softWrap = true,
            style = smallText.copy(lineHeight = 16.sp, color = messageColor))
        if (suggestions.isNotEmpty() && SuperNovaPresentation.showsSuggestions(mode, validation)) {
            LazyRow(
                Modifier.fillMaxWidth().padding(top = spacing.beforeSuggestions, bottom = spacing.afterSuggestions),
                horizontalArrangement = Arrangement.spacedBy(spacing.betweenSuggestions, Alignment.CenterHorizontally),
            ) {
                itemsIndexed(suggestions) { index, value ->
                    val isSelected = SuperNovaPresentation.isSelected(state.amount, value)
                    Box(
                        Modifier.clip(RoundedCornerShape(6.dp)).background(if (isSelected) Color(0xFF3B3B3E) else Color(0xFFF2F4F7))
                            .focusProperties { canFocus = false }
                            .clickable(enabled = canEdit, role = Role.Button) {
                                state.setAmount(value)
                                startEditing()
                            }
                            .testTag("supernova.suggestion.$index")
                            .semantics {
                                contentDescription = "Установить ${AmountText.formatted(value)} рублей"
                                selected = isSelected
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("${AmountText.formatted(value)} ₽", style = smallText.copy(fontSize = 12.sp, lineHeight = 16.sp,
                            color = if (isSelected) Color.White else Ink))
                    }
                }
            }
        }
    }
}

// Observes ordinary taps without consuming selection/scroll gestures handled by BasicTextField.
private fun Modifier.caretAtEndOnTap(state: SuperNovaAmountState) = pointerInput(state) {
    coroutineScope {
        val scope = this
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            while (true) {
                // Ignore consumption by the text field's own tap handler, but leave drags alone.
                val event = awaitPointerEvent(PointerEventPass.Final)
                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                if ((change.position - down.position).getDistance() > viewConfiguration.touchSlop) break
                if (!change.pressed) {
                    scope.launch {
                        withFrameNanos { }
                        state.moveCaretToEnd()
                    }
                    break
                }
            }
        }
    }
}

@Composable
private fun SuperNovaSkeleton(hasLabel: Boolean, hasDescription: Boolean, suggestionCount: Int,
                              modifier: Modifier, spacing: SuperNovaSpacing) {
    Column(modifier.fillMaxWidth().padding(vertical = spacing.vertical)
        .semantics(mergeDescendants = true) { contentDescription = "Загрузка суммы" },
        horizontalAlignment = Alignment.CenterHorizontally) {
        if (hasLabel) Box(Modifier.height(20.dp), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.sn_skeleton_label), null, Modifier.size(170.dp, 10.dp))
        }
        Spacer(Modifier.height(spacing.afterLabel))
        Box(Modifier.height(40.dp), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.sn_skeleton_amount), null, Modifier.size(230.dp, 20.dp))
        }
        Spacer(Modifier.height(spacing.afterAmount))
        if (hasDescription) Box(Modifier.height(16.dp), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.sn_skeleton_description), null, Modifier.size(130.dp, 8.dp))
        }
        if (suggestionCount > 0) LazyRow(Modifier.fillMaxWidth().padding(top = spacing.beforeSuggestions, bottom = spacing.afterSuggestions),
            horizontalArrangement = Arrangement.spacedBy(spacing.betweenSuggestions, Alignment.CenterHorizontally)) {
            items(suggestionCount) {
                Image(painterResource(R.drawable.sn_skeleton_suggestion), null, Modifier.size(96.dp, 28.dp))
            }
        }
    }
}
