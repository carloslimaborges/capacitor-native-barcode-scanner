import XCTest
@testable import MpdftBarcodeScannerPluginPlugin

class MpdftBarcodeScannerPluginTests: XCTestCase {
    func testFormatMapping() {
        let vc = BarcodeScannerViewController()
        XCTAssertNotNil(vc)
    }
}
