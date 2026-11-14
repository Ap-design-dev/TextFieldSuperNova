import Foundation

public enum SuperNovaMode: Equatable {
    case editable, readOnly, disabled, skeleton
}

/// Validation is supplied and cleared by the host. Editing never changes it implicitly.
public enum SuperNovaValidation: Equatable {
    case none
    case error(String)
    case warning(String)

    public var message: String? {
        switch self {
        case .none: return nil
        case .error(let message), .warning(let message): return message
        }
    }
}

public struct SuperNovaSpacing {
    public var vertical: CGFloat
    public var afterLabel: CGFloat
    public var afterAmount: CGFloat
    public var beforeSuggestions: CGFloat
    public var afterSuggestions: CGFloat
    public var betweenSuggestions: CGFloat

    public init(vertical: CGFloat = 24, afterLabel: CGFloat = 4, afterAmount: CGFloat = 4,
                beforeSuggestions: CGFloat = 12, afterSuggestions: CGFloat = 4,
                betweenSuggestions: CGFloat = 10) {
        self.vertical = vertical
        self.afterLabel = afterLabel
        self.afterAmount = afterAmount
        self.beforeSuggestions = beforeSuggestions
        self.afterSuggestions = afterSuggestions
        self.betweenSuggestions = betweenSuggestions
    }
}

enum SuperNovaPresentation {
    static func showsSuggestions(mode: SuperNovaMode, validation: SuperNovaValidation) -> Bool {
        mode != .readOnly || validation == .none
    }

    static func isSelected(amount: String, suggestion: String) -> Bool {
        guard let value = AmountText.normalize(amount), !value.isEmpty,
              let preset = AmountText.normalize(suggestion) else { return false }
        return value == preset
    }
}
