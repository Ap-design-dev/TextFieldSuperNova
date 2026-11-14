import Foundation

/// The model contains only ASCII digits. Empty and zero share the empty state.
public enum AmountText {
    public static func normalize(_ input: String) -> String? {
        var text = input.trimmingCharacters(in: .whitespacesAndNewlines)
        if text.hasSuffix("₽") { text.removeLast() }
        let separators: Set<Character> = [" ", "\u{00a0}", "\u{202f}"]
        let digits = text.filter { !separators.contains($0) }
        guard digits.allSatisfy({ $0 >= "0" && $0 <= "9" }) else { return nil }
        return String(digits.drop(while: { $0 == "0" }))
    }

    public static func formatted(_ digits: String) -> String {
        let value = normalize(digits) ?? ""
        if value.isEmpty { return "0" }
        return value.enumerated().reduce(into: "") { result, item in
            if item.offset > 0 && (value.count - item.offset).isMultiple(of: 3) {
                result.append("\u{00a0}")
            }
            result.append(item.element)
        }
    }

    struct Edit: Equatable {
        let amount: String
        let caret: Int
    }

    /// Formats a UIKit edit and keeps the caret beside the same logical digit.
    /// Backspacing a grouping space removes the preceding digit instead of getting stuck.
    static func edit(current: String, range: NSRange, replacement: String) -> Edit? {
        let source = current as NSString
        guard range.location >= 0, range.length >= 0,
              range.location <= source.length,
              range.length <= source.length - range.location else { return nil }
        var effectiveRange = range
        if replacement.isEmpty && range.length == 1 && range.location > 0,
           [" ", "\u{00a0}", "\u{202f}"].contains(source.substring(with: range)) {
            effectiveRange = NSRange(location: range.location - 1, length: 2)
        }
        let candidate = source.replacingCharacters(in: effectiveRange, with: replacement)
        guard let amount = normalize(candidate) else { return nil }
        let prefixLength = effectiveRange.location + (replacement as NSString).length
        let prefix = (candidate as NSString).substring(to: min(prefixLength, (candidate as NSString).length))
        let digitsBeforeCaret = prefix.filter { $0 >= "0" && $0 <= "9" }.count
        let allDigits = candidate.filter { $0 >= "0" && $0 <= "9" }
        let removedZeros = allDigits.count - amount.count
        let ordinal = max(0, digitsBeforeCaret - removedZeros)
        let rendered = formatted(amount)
        if amount.isEmpty { return Edit(amount: "", caret: 1) }
        var caret = 0
        var seen = 0
        for character in rendered {
            if seen >= ordinal { break }
            caret += character.utf16.count
            if character >= "0" && character <= "9" { seen += 1 }
        }
        return Edit(amount: amount, caret: caret)
    }
}
