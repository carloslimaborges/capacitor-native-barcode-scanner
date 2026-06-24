import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(MpdftBarcodeScannerPluginPlugin)
public class MpdftBarcodeScannerPluginPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "MpdftBarcodeScannerPluginPlugin"
    public let jsName = "MpdftBarcodeScannerPlugin"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = MpdftBarcodeScannerPlugin()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
