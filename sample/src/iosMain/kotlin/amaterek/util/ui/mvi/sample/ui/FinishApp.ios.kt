package amaterek.util.ui.mvi.sample.ui

import kotlin.system.exitProcess

actual fun finishApp(context: Any) {
    when (context) {
        else -> exitProcess(0) // TODO How to exit app on iOS?
    }
}