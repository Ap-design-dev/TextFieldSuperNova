import Foundation

public enum SuperNovaCreditMode: Equatable {
    case editable, readOnly, skeleton

    var amountMode: SuperNovaMode {
        switch self {
        case .editable: return .editable
        case .readOnly: return .readOnly
        case .skeleton: return .skeleton
        }
    }
}

public struct SuperNovaCreditSpacing {
    public var afterAmount: CGFloat
    public var bottom: CGFloat
    public var buttonVertical: CGFloat

    public init(afterAmount: CGFloat = 4, bottom: CGFloat = 32, buttonVertical: CGFloat = 6) {
        self.afterAmount = afterAmount
        self.bottom = bottom
        self.buttonVertical = buttonVertical
    }
}

enum SuperNovaCreditPresentation {
    /// When both optional blocks are hidden, only the bottom 32pt remains (user correction).
    static func afterAmount(hasText: Bool, hasButton: Bool, spacing: SuperNovaCreditSpacing) -> CGFloat {
        hasText || hasButton ? spacing.afterAmount : 0
    }
}
