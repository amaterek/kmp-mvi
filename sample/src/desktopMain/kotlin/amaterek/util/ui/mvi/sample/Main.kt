package amaterek.util.ui.mvi.sample

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() {
    return application(exitProcessOnExit = true) {
        Window(
            ::exitApplication,
            state = WindowState(size = DpSize(500.dp, 800.dp)),
            title = "MVI Sample",
        ) {
            ComposeApp(this)
        }
    }
}