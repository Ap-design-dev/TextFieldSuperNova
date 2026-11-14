import XCTest
@testable import TextFieldSuperNova

final class AmountTextTests: XCTestCase {
    func testIntegerPasteAndLeadingZeros() {
        XCTAssertEqual(AmountText.normalize("1 000 000 ₽"), "1000000")
        XCTAssertEqual(AmountText.normalize("0012"), "12")
        XCTAssertEqual(AmountText.normalize("0"), "")
        XCTAssertEqual(AmountText.normalize(""), "")
        XCTAssertNil(AmountText.normalize("1,50"))
        XCTAssertNil(AmountText.normalize("1.50"))
        XCTAssertNil(AmountText.normalize("-10"))
        XCTAssertNil(AmountText.normalize("1e6"))
    }

    func testGroupingWithoutNumericOverflow() {
        XCTAssertEqual(AmountText.formatted("1000000"), "1\u{00a0}000\u{00a0}000")
        XCTAssertEqual(AmountText.formatted("123456789012345678901"), "123\u{00a0}456\u{00a0}789\u{00a0}012\u{00a0}345\u{00a0}678\u{00a0}901")
    }

    func testFirstDigitReplacesPlaceholder() {
        XCTAssertEqual(AmountText.edit(current: "0", range: NSRange(location: 1, length: 0), replacement: "1"), .init(amount: "1", caret: 1))
    }

    func testInsertionInMiddleKeepsLogicalCaret() {
        XCTAssertEqual(AmountText.edit(current: "1\u{00a0}234", range: NSRange(location: 3, length: 0), replacement: "5"), .init(amount: "12534", caret: 4))
    }

    func testBackspaceAcrossGroupSeparator() {
        XCTAssertEqual(AmountText.edit(current: "1\u{00a0}234", range: NSRange(location: 1, length: 1), replacement: ""), .init(amount: "234", caret: 0))
    }

    func testDeletingAllKeepsZeroPlaceholder() {
        XCTAssertEqual(AmountText.edit(current: "123", range: NSRange(location: 0, length: 3), replacement: ""), .init(amount: "", caret: 1))
    }
}
