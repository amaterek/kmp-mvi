package amaterek.util.ui.mvi.sample

import amaterek.util.ui.mvi.sample.ui.screen.sample.SampleScreen
import amaterek.util.ui.mvi.sample.ui.theme.AppTheme
import androidx.compose.runtime.Composable

@Composable
fun ComposeApp(context: Any) {
    AppTheme {
        SampleScreen(context)
    }
}
