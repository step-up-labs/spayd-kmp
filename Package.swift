// swift-tools-version:5.8
import PackageDescription

let package = Package(
    name: "SPAYD_KMP",
    platforms: [
        .iOS(.v14)
    ],
    products: [
        .library(
            name: "SPAYD_KMP",
            targets: ["SPAYD_KMP"]
        )
    ],
    targets: [
        .binaryTarget(
            name: "SPAYD_KMP",
            path: "./frameworks/shared.xcframework"
        )
    ]
)