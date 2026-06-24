// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorNativeBarcodeScanner",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "CapacitorNativeBarcodeScanner",
            targets: ["MpdftBarcodeScannerPluginPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "8.0.0")
    ],
    targets: [
        .target(
            name: "MpdftBarcodeScannerPluginPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/MpdftBarcodeScannerPluginPlugin"),
        .testTarget(
            name: "MpdftBarcodeScannerPluginPluginTests",
            dependencies: ["MpdftBarcodeScannerPluginPlugin"],
            path: "ios/Tests/MpdftBarcodeScannerPluginPluginTests")
    ]
)