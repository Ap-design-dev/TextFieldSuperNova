#if canImport(UIKit)
import SwiftUI
import UIKit

/// Centered credit amount with optional multiline supporting text and an information action.
/// The host presents a sheet or navigates from `onInformation`, including in Read Only.
public struct SuperNovaCreditPotential: View {
    @Binding private var amount: String
    @Binding private var isEditing: Bool
    private let text: String?
    private let buttonTitle: String?
    private let mode: SuperNovaCreditMode
    private let warning: String?
    private let showChevron: Bool
    private let showClearButton: Bool
    private let spacing: SuperNovaCreditSpacing
    private let onInformation: () -> Void
    @ScaledMetric(relativeTo: .body) private var textSize = 14.0
    @ScaledMetric(relativeTo: .body) private var textHeight = 20.0

    public init(amount: Binding<String>, isEditing: Binding<Bool>,
                text: String? = "Text Potential", buttonTitle: String? = "Button",
                mode: SuperNovaCreditMode = .editable, warning: String? = nil,
                showChevron: Bool = true, showClearButton: Bool = true,
                spacing: SuperNovaCreditSpacing = .init(),
                onInformation: @escaping () -> Void) {
        _amount = amount
        _isEditing = isEditing
        self.text = text
        self.buttonTitle = buttonTitle
        self.mode = mode
        self.warning = warning
        self.showChevron = showChevron
        self.showClearButton = showClearButton
        self.spacing = spacing
        self.onInformation = onInformation
    }

    public var body: some View {
        VStack(spacing: 0) {
            // Reuse the existing entry, including normalization, focus, caret and scaling.
            SuperNovaAmountField(amount: $amount, isEditing: $isEditing,
                                 label: nil, description: nil, suggestions: [],
                                 mode: mode.amountMode, showChevron: showChevron,
                                 showClearButton: showClearButton, spacing: .init(vertical: 0))
            Spacer().frame(height: SuperNovaCreditPresentation.afterAmount(
                hasText: text != nil, hasButton: buttonTitle != nil, spacing: spacing))
            if let text {
                if mode == .skeleton {
                    Image("sn_credit_text", bundle: .module)
                        .frame(width: 170, height: 10).frame(height: textHeight)
                        .accessibilityHidden(true)
                } else {
                    Text(warning ?? text)
                        .font(Font(SuperNovaFonts.text(textSize)))
                        .lineSpacing(max(0, textHeight - SuperNovaFonts.text(textSize).lineHeight))
                        .padding(.vertical, max(0, (textHeight - SuperNovaFonts.text(textSize).lineHeight) / 2))
                        .multilineTextAlignment(.center).lineLimit(nil)
                        .fixedSize(horizontal: false, vertical: true)
                        .foregroundStyle(warning == nil
                            ? Color(red: 31/255, green: 31/255, blue: 34/255)
                            : Color(red: 239/255, green: 76/255, blue: 1/255))
                        .frame(maxWidth: .infinity, minHeight: textHeight)
                        .accessibilityIdentifier("supernova.credit.text")
                }
            }
            if let buttonTitle {
                if mode == .skeleton {
                    Image("sn_credit_button", bundle: .module)
                        .frame(width: 102, height: 8)
                        .frame(height: textHeight + spacing.buttonVertical * 2)
                        .accessibilityHidden(true)
                } else {
                    Button(action: onInformation) {
                        Text(buttonTitle)
                            .font(Font(SuperNovaFonts.textSemibold(textSize)))
                            .multilineTextAlignment(.center)
                            .fixedSize(horizontal: false, vertical: true)
                            .foregroundStyle(Color(red: 0, green: 137/255, blue: 133/255))
                            .frame(minWidth: 102, minHeight: textHeight)
                            .padding(.vertical, spacing.buttonVertical)
                    }
                    .buttonStyle(.plain)
                    .accessibilityIdentifier("supernova.credit.information")
                }
            }
        }
        .padding(.bottom, spacing.bottom)
        .frame(maxWidth: .infinity)
        .accessibilityElement(children: mode == .skeleton ? .ignore : .contain)
        .accessibilityLabel(mode == .skeleton ? "Загрузка суммы" : "")
    }
}
#endif
