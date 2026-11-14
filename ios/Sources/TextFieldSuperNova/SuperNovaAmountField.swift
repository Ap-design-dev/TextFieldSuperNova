#if canImport(UIKit)
import SwiftUI
import UIKit

/// A whole-ruble amount field. `amount` is an ungrouped digit string; "" means no amount.
/// Set `isEditing = false` from the host to finish editing and show the chevron.
public struct SuperNovaAmountField: View {
    @Binding private var amount: String
    @Binding private var isEditing: Bool
    private let label: String?
    private let description: String?
    private let suggestions: [String]
    private let mode: SuperNovaMode
    private let validation: SuperNovaValidation
    private let showChevron: Bool
    private let showClearButton: Bool
    private let spacing: SuperNovaSpacing

    @ScaledMetric(relativeTo: .largeTitle) private var amountSize = 42.0
    @ScaledMetric(relativeTo: .largeTitle) private var amountHeight = 50.0
    @ScaledMetric(relativeTo: .body) private var textSize = 14.0
    @ScaledMetric(relativeTo: .body) private var labelHeight = 20.0
    @ScaledMetric(relativeTo: .body) private var descriptionHeight = 16.0
    @ScaledMetric(relativeTo: .caption) private var chipSize = 12.0
    @ScaledMetric(relativeTo: .caption) private var chipLineHeight = 16.0

    public init(
        amount: Binding<String>,
        isEditing: Binding<Bool>,
        label: String? = "Label",
        description: String? = "Description.",
        suggestions: [String] = ["300000", "1000000", "3000000"],
        mode: SuperNovaMode = .editable,
        validation: SuperNovaValidation = .none,
        showChevron: Bool = true,
        showClearButton: Bool = true,
        spacing: SuperNovaSpacing = .init()
    ) {
        _amount = amount
        _isEditing = isEditing
        self.label = label
        self.description = description
        self.suggestions = suggestions
        self.mode = mode
        self.validation = validation
        self.showChevron = showChevron
        self.showClearButton = showClearButton
        self.spacing = spacing
    }

    private var ink: Color { Color(red: 31/255, green: 31/255, blue: 34/255) }
    private var messageColor: Color {
        if mode == .disabled { return ink.opacity(0.35) }
        switch validation {
        case .none: return ink.opacity(0.65)
        case .error: return Color(red: 209/255, green: 0, blue: 50/255)
        case .warning: return Color(red: 239/255, green: 76/255, blue: 1/255)
        }
    }
    private var showsSuggestions: Bool {
        !suggestions.isEmpty && SuperNovaPresentation.showsSuggestions(mode: mode, validation: validation)
    }

    public var body: some View {
        VStack(spacing: 0) {
            if let label {
                if mode == .skeleton {
                    skeletonImage("sn_skeleton_label", width: 170, height: 10).frame(height: labelHeight)
                } else {
                    Text(label)
                        .font(Font(SuperNovaFonts.text(textSize)))
                        .multilineTextAlignment(.center)
                        .fixedSize(horizontal: false, vertical: true)
                        .foregroundStyle(mode == .disabled ? ink.opacity(0.35) : ink)
                        .frame(maxWidth: .infinity, minHeight: labelHeight)
                }
            }
            Spacer().frame(height: SuperNovaPresentation.beforeAmount(
                hasLabel: label != nil, hasDescription: description != nil,
                hasSuggestions: showsSuggestions, spacing: spacing))
            if mode == .skeleton {
                skeletonImage("sn_skeleton_amount", width: 230, height: 20)
                    .frame(height: amountHeight * 0.8)
            } else {
                NativeAmountEntry(
                    amount: $amount,
                    isEditing: $isEditing,
                    label: label ?? "Сумма в рублях",
                    fontSize: amountSize,
                    mode: mode,
                    showChevron: showChevron,
                    showClearButton: showClearButton
                )
                .frame(height: amountHeight)
            }
            Spacer().frame(height: description != nil ? spacing.afterAmount : 0)
            if let description {
                if mode == .skeleton {
                    skeletonImage("sn_skeleton_description", width: 130, height: 8).frame(height: descriptionHeight)
                } else {
                    Text(mode == .disabled ? description : validation.message ?? description)
                        .font(Font(SuperNovaFonts.text(textSize)))
                        .multilineTextAlignment(.center)
                        .lineLimit(nil)
                        .fixedSize(horizontal: false, vertical: true)
                        .foregroundStyle(messageColor)
                        .frame(maxWidth: .infinity, minHeight: descriptionHeight)
                        .accessibilityIdentifier("supernova.message")
                }
            }
            if showsSuggestions {
                GeometryReader { geometry in
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: spacing.betweenSuggestions) {
                            ForEach(Array(suggestions.enumerated()), id: \.offset) { index, suggestion in
                                if mode == .skeleton {
                                    skeletonImage("sn_skeleton_suggestion", width: 96, height: 28)
                                } else {
                                    let selected = SuperNovaPresentation.isSelected(amount: amount, suggestion: suggestion)
                                    Button {
                                        guard mode == .editable else { return }
                                        if let normalized = AmountText.normalize(suggestion) {
                                            amount = normalized
                                            isEditing = true
                                        }
                                    } label: {
                                        Text("\(AmountText.formatted(suggestion)) ₽")
                                            .font(Font(SuperNovaFonts.text(chipSize)))
                                            .foregroundStyle(selected ? Color.white : ink)
                                            .frame(minHeight: chipLineHeight)
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 6)
                                            .background(selected ? Color(red: 59/255, green: 59/255, blue: 62/255) : Color(red: 242/255, green: 244/255, blue: 247/255))
                                            .clipShape(RoundedRectangle(cornerRadius: 6))
                                    }
                                    .buttonStyle(.plain)
                                    .disabled(mode != .editable)
                                    .accessibilityLabel("Установить \(AmountText.formatted(suggestion)) рублей")
                                    .accessibilityAddTraits(selected ? [.isSelected] : [])
                                    .accessibilityIdentifier("supernova.suggestion.\(index)")
                                }
                            }
                        }
                        .frame(minWidth: geometry.size.width)
                        .padding(.top, spacing.beforeSuggestions)
                        .padding(.bottom, spacing.afterSuggestions)
                    }
                }
                .frame(height: chipLineHeight + 12 + spacing.beforeSuggestions + spacing.afterSuggestions)
            }
        }
        .foregroundStyle(ink)
        .padding(.vertical, spacing.vertical)
        .frame(maxWidth: .infinity)
        .accessibilityElement(children: mode == .skeleton ? .ignore : .contain)
        .accessibilityLabel(mode == .skeleton ? "Загрузка суммы" : "")
        .onAppear { if mode != .editable { isEditing = false } }
        .onChange(of: mode) { newMode in if newMode != .editable { isEditing = false } }
    }

    private func skeletonImage(_ name: String, width: CGFloat, height: CGFloat) -> some View {
        Image(name, bundle: .module).frame(width: width, height: height).accessibilityHidden(true)
    }
}

private struct NativeAmountEntry: UIViewRepresentable {
    @Binding var amount: String
    @Binding var isEditing: Bool
    let label: String
    let fontSize: CGFloat
    let mode: SuperNovaMode
    let showChevron: Bool
    let showClearButton: Bool

    func makeUIView(context: Context) -> AmountEntryView { AmountEntryView() }

    func updateUIView(_ view: AmountEntryView, context: Context) {
        view.onAmountChange = { amount = $0 }
        view.onFocusChange = { focused in
            // UIKit can deliver delegate callbacks during a SwiftUI update.
            DispatchQueue.main.async { if isEditing != focused { isEditing = focused } }
        }
        view.field.accessibilityLabel = label
        view.baseFontSize = fontSize
        view.mode = mode
        view.showChevron = showChevron
        view.showClearButton = showClearButton
        view.setAmount(amount)
        view.setEditing(isEditing && mode == .editable)
    }

    static func dismantleUIView(_ view: AmountEntryView, coordinator: ()) {
        view.onAmountChange = nil
        view.onFocusChange = nil
        view.field.resignFirstResponder()
    }
}

private final class AmountTextField: UITextField, UIGestureRecognizerDelegate {
    override init(frame: CGRect) {
        super.init(frame: frame)
        let tap = UITapGestureRecognizer(target: self, action: #selector(moveCaretToEnd))
        tap.cancelsTouchesInView = false
        tap.delegate = self
        addGestureRecognizer(tap)
    }

    required init?(coder: NSCoder) { fatalError("Use init(frame:)") }

    override func caretRect(for position: UITextPosition) -> CGRect {
        var rect = super.caretRect(for: position)
        rect.size.width = 2
        return rect
    }

    @objc private func moveCaretToEnd() {
        DispatchQueue.main.async { [weak self] in
            guard let self, self.isFirstResponder else { return }
            self.selectedTextRange = self.textRange(from: self.endOfDocument, to: self.endOfDocument)
        }
    }

    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer,
                           shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool { true }
}

private final class AmountEntryView: UIView, UITextFieldDelegate {
    let field = AmountTextField()
    private let currency = UILabel()
    private let action = UIButton(type: .custom)
    private var amount = ""
    private var wantsEditing = false
    var baseFontSize: CGFloat = 42 { didSet { setNeedsLayout() } }
    var mode: SuperNovaMode = .editable
    var showChevron = true
    var showClearButton = true
    var onAmountChange: ((String) -> Void)?
    var onFocusChange: ((Bool) -> Void)?
    private let ink = UIColor(red: 31/255, green: 31/255, blue: 34/255, alpha: 1)

    override init(frame: CGRect) {
        super.init(frame: frame)
        field.delegate = self
        field.keyboardType = .numberPad
        field.borderStyle = .none
        field.textAlignment = .left
        field.autocorrectionType = .no
        field.spellCheckingType = .no
        field.smartInsertDeleteType = .no
        field.tintColor = ink
        field.textContentType = .none
        field.accessibilityIdentifier = "supernova.amount"
        currency.text = "₽"
        currency.isAccessibilityElement = false
        action.accessibilityIdentifier = "supernova.action"
        action.addTarget(self, action: #selector(performAction), for: .touchUpInside)
        addSubview(field)
        addSubview(currency)
        addSubview(action)
        let tap = UITapGestureRecognizer(target: self, action: #selector(beginEditing))
        currency.isUserInteractionEnabled = true
        currency.addGestureRecognizer(tap)
        let done = UIBarButtonItem(title: "Готово", style: .done, target: self, action: #selector(finishEditing))
        let toolbar = UIToolbar()
        toolbar.items = [UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil), done]
        toolbar.sizeToFit()
        field.inputAccessoryView = toolbar
        setAmount("")
    }

    required init?(coder: NSCoder) { fatalError("Use init(frame:)") }

    func setAmount(_ value: String) {
        guard let normalized = AmountText.normalize(value) else { return }
        amount = normalized
        let display = AmountText.formatted(normalized)
        if field.text != display {
            field.text = display
            placeCaret(display.utf16.count)
        }
        let color = mode == .disabled ? ink.withAlphaComponent(0.35) : (normalized.isEmpty ? ink.withAlphaComponent(0.65) : ink)
        field.textColor = color
        currency.textColor = color
        field.isUserInteractionEnabled = mode == .editable
        field.accessibilityTraits = mode == .editable ? [] : [.staticText]
        setNeedsLayout()
    }

    func setEditing(_ editing: Bool) {
        wantsEditing = editing
        updateAction(editing)
        // Wait until the representable has joined its window before requesting focus.
        if editing && !field.isFirstResponder {
            DispatchQueue.main.async { [weak self] in
                guard let self, self.window != nil, self.wantsEditing else { return }
                self.field.becomeFirstResponder()
            }
        } else if !editing && field.isFirstResponder {
            field.resignFirstResponder()
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        let fullFont = SuperNovaFonts.display(baseFontSize)
        let text = field.text ?? "0"
        let numberWidth = (text as NSString).size(withAttributes: [.font: fullFont]).width + 3
        let currencyWidth = ("₽" as NSString).size(withAttributes: [.font: fullFont]).width
        let iconVisible = mode == .editable && (field.isFirstResponder ? showClearButton : showChevron)
        let available = max(1, bounds.width - (iconVisible ? 64 : 32))
        let scale = max(0.5, min(1, available / (numberWidth + 4 + currencyWidth)))
        let font = SuperNovaFonts.display(baseFontSize * scale)
        field.font = font
        currency.font = font
        let rubleWidth = ceil(("₽" as NSString).size(withAttributes: [.font: font]).width)
        let width = min(ceil((text as NSString).size(withAttributes: [.font: font]).width) + 3, max(1, available - 4 - rubleWidth))
        let groupWidth = width + 4 + rubleWidth
        let start = (bounds.width - groupWidth) / 2
        field.frame = CGRect(x: start, y: 0, width: width, height: bounds.height)
        currency.frame = CGRect(x: start + width + 4, y: 0, width: rubleWidth, height: bounds.height)
        action.frame = CGRect(x: start + groupWidth + 6 - 10, y: 0, width: 44, height: bounds.height)
    }

    private func updateAction(_ editing: Bool) {
        action.isHidden = mode != .editable || !(editing ? showClearButton : showChevron)
        let name = editing ? "sc_srv_cross_stroke_16" : "sc_srv_caretright_stroke_16"
        action.setImage(UIImage(named: name, in: .module, compatibleWith: nil)?.withRenderingMode(.alwaysOriginal), for: .normal)
        action.accessibilityLabel = editing ? "Очистить сумму" : "Редактировать сумму"
    }

    @objc private func beginEditing() {
        guard mode == .editable else { return }
        field.becomeFirstResponder()
        placeCaret((field.text ?? "0").utf16.count)
    }
    @objc private func finishEditing() { field.resignFirstResponder() }
    @objc private func performAction() {
        guard mode == .editable else { return }
        if field.isFirstResponder {
            setAmount("")
            onAmountChange?("")
            placeCaret(1)
        } else {
            beginEditing()
        }
    }

    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool { mode == .editable }

    func textFieldDidBeginEditing(_ textField: UITextField) {
        updateAction(true)
        placeCaret((textField.text ?? "0").utf16.count)
        onFocusChange?(true)
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        updateAction(false)
        onFocusChange?(false)
    }

    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        guard let edit = AmountText.edit(current: textField.text ?? "0", range: range, replacement: string) else { return false }
        setAmount(edit.amount)
        placeCaret(edit.caret)
        onAmountChange?(edit.amount)
        return false
    }

    private func placeCaret(_ offset: Int) {
        guard let position = field.position(from: field.beginningOfDocument, offset: offset) else { return }
        field.selectedTextRange = field.textRange(from: position, to: position)
    }
}
#endif
