import XCTest
@testable import TextFieldSuperNova

final class PresentationTests: XCTestCase {
    func testCreditHiddenBlocksKeepOnlyBottomSpacing() {
        let cases: [(Bool, Bool, CGFloat)] = [
            (true, true, 138), (true, false, 106),
            (false, true, 118), (false, false, 82)
        ]
        let spacing = SuperNovaCreditSpacing()
        for (text, button, expected) in cases {
            let gap = SuperNovaCreditPresentation.afterAmount(hasText: text, hasButton: button, spacing: spacing)
            let height = 50 + gap + (text ? 20 : 0) + (button ? 32 : 0) + spacing.bottom
            XCTAssertEqual(height, expected)
        }
        XCTAssertEqual(SuperNovaCreditMode.readOnly.amountMode, .readOnly)
    }

    func testHiddenElementHeightsMatchFigmaSection2() {
        // Reference heights are measured from the eight Figma examples at width 375.
        let cases: [(Bool, Bool, Bool, CGFloat)] = [
            (true, true, true, 186), (false, true, true, 162),
            (true, false, true, 166), (false, false, true, 146),
            (true, true, false, 142), (true, false, false, 122),
            (false, true, false, 118), (false, false, false, 98)
        ]
        let spacing = SuperNovaSpacing()
        for (label, description, suggestions, expected) in cases {
            let top = SuperNovaPresentation.beforeAmount(hasLabel: label, hasDescription: description,
                                                        hasSuggestions: suggestions, spacing: spacing)
            let height = 48 + (label ? 20 : 0) + top + 50
                + (description ? 4 + 16 : 0) + (suggestions ? 44 : 0)
            XCTAssertEqual(height, expected, "Label=\(label), Description=\(description), Suggest=\(suggestions)")
        }
    }

    func testPresetSelectionTracksNumericValue() {
        XCTAssertTrue(SuperNovaPresentation.isSelected(amount: "1000000", suggestion: "1 000 000 ₽"))
        XCTAssertFalse(SuperNovaPresentation.isSelected(amount: "1000001", suggestion: "1000000"))
        XCTAssertFalse(SuperNovaPresentation.isSelected(amount: "", suggestion: "0"))
    }

    func testReadOnlyFeedbackHidesPresets() {
        XCTAssertTrue(SuperNovaPresentation.showsSuggestions(mode: .readOnly, validation: .none))
        XCTAssertFalse(SuperNovaPresentation.showsSuggestions(mode: .readOnly, validation: .error("Error")))
        XCTAssertFalse(SuperNovaPresentation.showsSuggestions(mode: .readOnly, validation: .warning("Warning")))
        XCTAssertTrue(SuperNovaPresentation.showsSuggestions(mode: .disabled, validation: .none))
        XCTAssertTrue(SuperNovaPresentation.showsSuggestions(mode: .editable, validation: .error("Error")))
    }

    func testMessagesComeFromTheHost() {
        XCTAssertNil(SuperNovaValidation.none.message)
        XCTAssertEqual(SuperNovaValidation.error("Недопустимая сумма").message, "Недопустимая сумма")
        XCTAssertEqual(SuperNovaValidation.warning("Проверьте сумму").message, "Проверьте сумму")
    }
}
