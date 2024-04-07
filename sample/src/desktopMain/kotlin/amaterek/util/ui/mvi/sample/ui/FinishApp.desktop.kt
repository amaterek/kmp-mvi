package amaterek.util.ui.mvi.sample.ui

import androidx.compose.ui.window.ApplicationScope
import kotlin.system.exitProcess

actual fun finishApp(context: Any) {
    when (context) {
        is ApplicationScope -> context.exitApplication()
        else -> exitProcess(0)
    }
}