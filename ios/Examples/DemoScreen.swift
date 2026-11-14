import SwiftUI
import TextFieldSuperNova

struct DemoScreen: View {
    @State private var amount = ""
    @State private var isEditing = false

    var body: some View {
        VStack(spacing: 0) {
            SuperNovaAmountField(amount: $amount, isEditing: $isEditing)
            Spacer()
        }
        .background(Color.white.onTapGesture { isEditing = false })
        .preferredColorScheme(.light)
    }
}

#Preview { DemoScreen() }
