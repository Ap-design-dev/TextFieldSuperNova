# TextField SuperNova

Нативное поле ввода целой суммы в рублях для **SwiftUI** и **Jetpack Compose**, по [17 состояниям Figma](https://www.figma.com/design/l1Mc2dfm0aVfw93T8GmmuM/?node-id=3-27) и [правилам поведения](https://www.figma.com/design/l1Mc2dfm0aVfw93T8GmmuM/?node-id=9-1497).

## Статус

Исходный код версии 0.3 подготовлен. Браузерный предпросмотр проверен интерактивно; он является отдельной моделью интерфейса. Локально нет Xcode, JDK и Android SDK; нативные сборки и тесты выполняются в [GitHub Actions](https://github.com/Ap-design-dev/TextFieldSuperNova/actions). Проверяйте результат для нужного коммита. Визуальная проверка на устройствах ещё требуется. Результаты и ограничения — в [docs/verification.md](docs/verification.md).

Режимы: редактируемое поле, Read Only, Disabled, Skeleton. Сообщения Error/Warning принимает приложение и снимает явно. Точное совпадение с готовой суммой выделяет Suggest, в том числе при ручном вводе. Каждый обычный тап ставит каретку в конец, толщина — 2 единицы макета; сумма уменьшается до 50%, затем прокручивается горизонтально.

Error и Warning поддерживают многострочный текст: передайте строку с `\n` для ручных переносов; длинные строки переносятся автоматически по ширине компонента. Высота сообщения адаптируется к содержимому. В предпросмотре поле «Текст сообщения» многострочное, Enter добавляет перенос.

При скрытии Label и Description их разделители убираются по вариантам Section 2. Исключение из макета — только сумма + Suggest: перед суммой сохраняется 4 px. Эталонные размеры всех восьми комбинаций записаны в [docs/layout-reference.json](docs/layout-reference.json); те же правила используются в SwiftUI, Compose и предпросмотре.

## Посмотреть и скачать

Скачайте репозиторий через **Code → Download ZIP**, распакуйте и откройте [preview/index.html](preview/index.html) в браузере. В нём можно вводить сумму, менять Label/Description, переключать режимы, ошибки и предупреждения. Встроенное отображение HTML на странице GitHub показывает код; для работы предпросмотра файл нужно скачать и открыть локально.

Редактируемый исходный фрагмент находится в [preview/fragment.html](preview/fragment.html). Это браузерная модель интерфейса; она не исполняет Swift/Kotlin. Готовый [архив исходников](deliverables/TextFieldSuperNova-v0.3-source.zip) также включён в репозиторий.

## Credit Potential

Добавлен новый вид по [макетам Figma](https://www.figma.com/design/l1Mc2dfm0aVfw93T8GmmuM/?node-id=15-1940): сумма, многострочный текст и кнопка информации по центру. Кнопка работает в Read Only и вызывает обработчик приложения для открытия пуллера/страницы. При скрытых тексте и кнопке под суммой остаётся 32 px. Каретка — 2 px; SB Sans Text Semibold подключён.

Откройте [preview/credit-potential.html](preview/credit-potential.html) после скачивания. API SwiftUI/Compose и все размеры — в [docs/credit-potential.md](docs/credit-potential.md). Исходный вид доступен в прежнем предпросмотре.

## iOS — SwiftUI

Требуется iOS 16+, Xcode с поддержкой Swift 5.9+. В Xcode добавьте локальный Swift Package из папки `ios` или укажите путь к ней в своём `Package.swift`.

```swift
import SwiftUI
import TextFieldSuperNova

struct AmountScreen: View {
    @State private var amount = ""
    @State private var editing = false

    var body: some View {
        SuperNovaAmountField(amount: $amount, isEditing: $editing)
    }
}
```

`amount` — строка цифр без разделителей и ₽; пусто — `""`. Для начального заполнения задайте `"1000000"`. Чтобы завершить ввод программно, установите `editing = false`. `label`, `description` и `suggestions` можно передать в инициализатор; `nil`/пустой список скрывают соответствующий блок.

```swift
SuperNovaAmountField(
    amount: $amount,
    isEditing: $editing,
    label: "Сумма вложения\nна покупку",
    description: "Выберите готовую сумму\nили введите свою",
    mode: .editable,
    validation: .warning("Проверьте сумму"),
    showChevron: true,
    showClearButton: true,
    spacing: .init(vertical: 24)
)
```

`SuperNovaMode`: `.editable`, `.readOnly`, `.disabled`, `.skeleton`. `SuperNovaValidation`: `.none`, `.error(String)`, `.warning(String)`. Ввод не очищает валидацию. При `description: nil` блок описания/сообщения скрыт. В Read Only и Disabled пользователь не может менять значение, включая Suggest; приложение по-прежнему может менять binding. UIKit-каретка задаётся шириной 2 pt; размеры из макета переводятся в логические единицы платформы.

Шрифты регистрируются из ресурсов пакета; запись `UIAppFonts` в приложении не нужна. Для нативного курсора, редактирования выделения и вставки используется `UITextField` внутри `UIViewRepresentable`. Пример экрана — `ios/Examples/DemoScreen.swift`; добавьте его в iOS-приложение, импортирующее пакет. Сам Swift Package не является запускаемым приложением.

Проверки логики форматирования на машине со Swift:

```sh
cd ios
swift test
```

Для проверки iOS-части соберите подключившее пакет приложение в симуляторе или на устройстве. `swift test` на macOS проверяет только независимую от UIKit логику.

## Android — Jetpack Compose

Откройте папку `android` в Android Studio. Проект содержит библиотеку `library` и запускаемое приложение `demo`. Требуются JDK 17, Android SDK 35 и Gradle 8.11.1. Версии зафиксированы: AGP 8.9.2, Kotlin/Compose Compiler 2.1.20, Compose BOM 2025.04.01. Gradle Wrapper в комплект не включён; используйте установленный Gradle 8.11.1 либо создайте wrapper этой версии.

```kotlin
import dev.supernova.textfield.SuperNovaAmountField
import dev.supernova.textfield.rememberSuperNovaAmountState

@Composable
fun AmountScreen() {
    val amount = rememberSuperNovaAmountState()
    SuperNovaAmountField(state = amount)
    // amount.amount — строка цифр; amount.setAmount("1000000") / amount.clear()
}
```

Для существующего приложения подключите модуль `library` и зависимость `implementation(project(":library"))`. Подпись, описание и подсказки настраиваются параметрами функции. Состояние поля и выделение сохраняются через `rememberTextFieldState`. Форматирование использует `InputTransformation` / `OutputTransformation`, поэтому позиции курсора сопоставляет Compose. Для наблюдения за суммой из корутины используйте `snapshotFlow { amount.amount }`; завершить редактирование можно через `LocalFocusManager.current.clearFocus()` и `LocalSoftwareKeyboardController.current?.hide()`.

```kotlin
SuperNovaAmountField(
    state = amount,
    label = "Сумма вложения\nна покупку",
    description = "Выберите готовую сумму\nили введите свою",
    mode = SuperNovaMode.Editable,
    validation = SuperNovaValidation.Warning("Проверьте сумму"),
    showChevron = true,
    showClearButton = true,
    spacing = SuperNovaSpacing(vertical = 24.dp),
)
```

Импортируйте `SuperNovaMode`, `SuperNovaValidation`, `SuperNovaSpacing` из `dev.supernova.textfield` и `dp` из `androidx.compose.ui.unit.dp`. Режимы — `Editable`, `ReadOnly`, `Disabled`, `Skeleton`; валидация — `None`, `Error(text)`, `Warning(text)`. Для редактируемого поля используется штатная каретка BasicTextField; на Android размеры макета переводятся в dp/sp.

```sh
cd android
gradle :library:testDebugUnitTest :library:lintDebug :demo:assembleDebug
gradle :library:connectedDebugAndroidTest
```

Последняя команда требует запущенного эмулятора/устройства. Инструментальный тест проверяет, что подсказка и очистка сохраняют фокус; фактическую видимость клавиатуры нужно также проверить вручную.

## Материалы

- `assets/icons` — оригинальные SVG без изменений.
- `assets/skeleton` — оригинальные элементы Skeleton из Figma, сохранённые без перерисовки.
- `assets/fonts` — все шесть предоставленных файлов шрифтов; в каждую платформу включены Display Semibold, Text Regular и Text Semibold.
- `ios/.../Resources` и `android/library/src/main/res` — шрифты и экспортированные иконки для платформ.
- `docs/behavior.md` — поведение, размеры, принятые допущения и сценарии приёмки.

Иконки и Skeleton экспортированы из исходных SVG с сохранением геометрии и цветов: iOS 1x/2x/3x, Android mdpi–xxxhdpi. Генератор — `scripts/export-icons.cjs` (`npm install`, затем `npm run assets`). Sharp используется только при подготовке ресурсов, не в приложениях.

Предоставленные шрифты и иконки остаются материалами их правообладателей. Данные TTF содержат ссылку на [лицензию ParaType](https://www.paratype.com/eula); этот проект не меняет условия их использования и не присваивает им открытую лицензию.

## Документация платформ

- [Apple: UITextFieldDelegate](https://developer.apple.com/documentation/uikit/uitextfielddelegate).
- [Android: состояние поля и преобразования ввода/вывода](https://developer.android.com/develop/ui/compose/text/migrate-state-based).
- [Совместимость AGP 8.9 и Gradle/JDK](https://developer.android.com/build/releases/agp-8-9-0-release-notes).
