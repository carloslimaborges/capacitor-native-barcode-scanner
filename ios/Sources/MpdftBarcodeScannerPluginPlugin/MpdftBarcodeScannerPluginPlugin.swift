import Foundation
import Capacitor

@objc(MpdftBarcodeScannerPlugin)
public class MpdftBarcodeScannerPlugin: CAPPlugin, CAPBridgedPlugin {

    public let identifier = "MpdftBarcodeScannerPlugin"
    public let jsName = "MpdftBarcodeScanner"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "scan", returnType: CAPPluginReturnPromise)
    ]

    private var pendingCall: CAPPluginCall?

    @objc func scan(_ call: CAPPluginCall) {
        self.pendingCall = call
        DispatchQueue.main.async {
            let vc = BarcodeScannerViewController()
            vc.delegate = self
            vc.modalPresentationStyle = .fullScreen
            self.bridge?.viewController?.present(vc, animated: true)
        }
    }
}

extension MpdftBarcodeScannerPlugin: BarcodeScannerDelegate {

    func didScan(value: String, format: String) {
        var result = JSObject()
        result["value"] = value
        result["format"] = format
        result["cancelled"] = false
        pendingCall?.resolve(result)
        pendingCall = nil
    }

    func didCancel() {
        var result = JSObject()
        result["value"] = ""
        result["format"] = ""
        result["cancelled"] = true
        pendingCall?.resolve(result)
        pendingCall = nil
    }
}
