package amaterek.util.ui.mvi.sample.ui

import amaterek.util.ui.mvi.sample.ComposeApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeApp(this)
        }
    }
}
