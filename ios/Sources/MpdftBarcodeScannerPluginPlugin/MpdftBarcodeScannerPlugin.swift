import Foundation

@objc public class MpdftBarcodeScannerPlugin: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
