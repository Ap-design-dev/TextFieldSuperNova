// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "TextFieldSuperNova",
    platforms: [.iOS(.v16)],
    products: [.library(name: "TextFieldSuperNova", targets: ["TextFieldSuperNova"])],
    targets: [
        .target(name: "TextFieldSuperNova", resources: [.process("Resources")]),
        .testTarget(name: "TextFieldSuperNovaTests", dependencies: ["TextFieldSuperNova"])
    ]
)
