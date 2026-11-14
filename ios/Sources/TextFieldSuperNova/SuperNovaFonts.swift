#if canImport(UIKit)
import CoreText
import UIKit

enum SuperNovaFonts {
    private static let registration: Void = {
        for name in ["SBSansDisplay-SemiBold", "SBSansText-Regular", "SBSansText-Semibold"] {
            if let url = Bundle.module.url(forResource: name, withExtension: "ttf") {
                CTFontManagerRegisterFontsForURL(url as CFURL, .process, nil)
            }
        }
    }()

    static func display(_ size: CGFloat) -> UIFont {
        _ = registration
        return UIFont(name: "SBSansDisplay-Semibold", size: size)
            ?? .systemFont(ofSize: size, weight: .semibold)
    }

    static func text(_ size: CGFloat) -> UIFont {
        _ = registration
        return UIFont(name: "SBSansText-Regular", size: size) ?? .systemFont(ofSize: size)
    }

    static func textSemibold(_ size: CGFloat) -> UIFont {
        _ = registration
        return UIFont(name: "SBSansText-Semibold", size: size)
            ?? .systemFont(ofSize: size, weight: .semibold)
    }
}
#endif
