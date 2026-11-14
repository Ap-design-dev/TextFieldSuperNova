import XCTest
@testable import TextFieldSuperNova

final class PresentationTests: XCTestCase {
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
