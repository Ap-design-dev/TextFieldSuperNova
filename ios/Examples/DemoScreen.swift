import SwiftUI
import TextFieldSuperNova

struct DemoScreen: View {
    @State private var amount = ""
    @State private var isEditing = false
    @State private var creditAmount = "1500000"
    @State private var creditEditing = false
    @State private var showsInformation = false

    var body: some View {
        VStack(spacing: 0) {
            SuperNovaAmountField(amount: $amount, isEditing: $isEditing)
            SuperNovaCreditPotential(amount: $creditAmount, isEditing: $creditEditing) {
                creditEditing = false
                showsInformation = true
            }
            Spacer()
        }
        .background(Color.white.onTapGesture { isEditing = false; creditEditing = false })
        .sheet(isPresented: $showsInformation) {
            NavigationStack {
                Text("Дополнительная информация")
                    .navigationTitle("Кредитный потенциал")
                    .navigationBarTitleDisplayMode(.inline)
                    .toolbar { Button("Закрыть") { showsInformation = false } }
            }
            .presentationDetents([.medium, .large])
        }
        .preferredColorScheme(.light)
    }
}

#Preview { DemoScreen() }
