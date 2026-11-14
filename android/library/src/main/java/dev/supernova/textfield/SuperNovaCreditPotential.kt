package dev.supernova.textfield

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SuperNovaCreditMode { Editable, ReadOnly, Skeleton }

data class SuperNovaCreditSpacing(
    val afterAmount: Dp = 4.dp,
    val bottom: Dp = 32.dp,
    val buttonVertical: Dp = 6.dp,
)

internal object SuperNovaCreditPresentation {
    fun afterAmount(hasText: Boolean, hasButton: Boolean, spacing: SuperNovaCreditSpacing): Dp =
        if (hasText || hasButton) spacing.afterAmount else 0.dp
}

/** The host opens a sheet or page from onInformation, also in Read Only. */
@Composable
fun SuperNovaCreditPotential(
    state: SuperNovaAmountState,
    onInformation: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = "Text Potential",
    buttonTitle: String? = "Button",
    mode: SuperNovaCreditMode = SuperNovaCreditMode.Editable,
    warning: String? = null,
    showChevron: Boolean = true,
    showClearButton: Boolean = true,
    spacing: SuperNovaCreditSpacing = SuperNovaCreditSpacing(),
) {
    val skeleton = mode == SuperNovaCreditMode.Skeleton
    val textStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.sb_sans_text_regular)),
        fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Center,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        color = if (warning == null) Color(0xFF1F1F22) else Color(0xFFEF4C01),
    )
    val textHeight = with(LocalDensity.current) { 20.sp.toDp() }
    Column(modifier.fillMaxWidth().padding(bottom = spacing.bottom)
        .then(if (skeleton) Modifier.semantics(mergeDescendants = true) {
            contentDescription = "Загрузка суммы"
        } else Modifier), horizontalAlignment = Alignment.CenterHorizontally) {
        // Shared normalization, IME, caret, icon and scaling behavior.
        SuperNovaAmountField(
            state, label = null, description = null, suggestions = emptyList(),
            mode = when (mode) {
                SuperNovaCreditMode.Editable -> SuperNovaMode.Editable
                SuperNovaCreditMode.ReadOnly -> SuperNovaMode.ReadOnly
                SuperNovaCreditMode.Skeleton -> SuperNovaMode.Skeleton
            },
            showChevron = showChevron, showClearButton = showClearButton,
            spacing = SuperNovaSpacing(vertical = 0.dp),
        )
        Spacer(Modifier.height(SuperNovaCreditPresentation.afterAmount(text != null, buttonTitle != null, spacing)))
        if (text != null) {
            if (skeleton) Box(Modifier.height(textHeight), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.sn_credit_text), null, Modifier.size(170.dp, 10.dp))
            } else Text(warning ?: text, style = textStyle, softWrap = true,
                modifier = Modifier.fillMaxWidth().testTag("supernova.credit.text")
                    .semantics { liveRegion = LiveRegionMode.Polite })
        }
        if (buttonTitle != null) {
            if (skeleton) Box(Modifier.height(textHeight + spacing.buttonVertical * 2), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.sn_credit_button), null, Modifier.size(102.dp, 8.dp))
            } else Box(
                Modifier.widthIn(min = 102.dp)
                    .clickable(role = Role.Button, onClick = onInformation)
                    .testTag("supernova.credit.information")
                    .padding(vertical = spacing.buttonVertical),
                contentAlignment = Alignment.Center,
            ) {
                Text(buttonTitle, softWrap = true, style = textStyle.copy(
                    fontFamily = FontFamily(Font(R.font.sb_sans_text_semibold, FontWeight.SemiBold)),
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF008985)))
            }
        }
    }
}
