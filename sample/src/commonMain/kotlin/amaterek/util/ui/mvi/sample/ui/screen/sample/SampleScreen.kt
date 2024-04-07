package amaterek.util.ui.mvi.sample.ui.screen.sample

import amaterek.util.ui.mvi.sample.ui.finishApp
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.SampleViewIntent
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.SamplePresenter
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.SampleScreenEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun SampleScreen(context: Any) {
    // In real project the presenter should be created to respect screen lifecycle.
    val presenter = remember {
        SamplePresenter()
    }

    LaunchedEffect(Unit) {
        presenter.initialize(this)

        presenter.eventFlow
            .onEach {
                when (it) {
                    SampleScreenEvent.NavigateBack -> finishApp(context)
                }
            }
            .launchIn(this)
    }

    val state by presenter.stateFlow.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = "Counter ${state.counter}"
        )
        Button(
            onClick = {
                presenter.onViewIntent(SampleViewIntent.IncrementCounter)
            },
        ) {
            Text("Increment counter")
        }
        Button(
            onClick = { presenter.onViewIntent(SampleViewIntent.DecrementCounter) },
        ) {
            Text("Decrement counter")
        }
        Button(
            onClick = { presenter.onViewIntent(SampleViewIntent.NavigateBack) },
        ) {
            Text("Back")
        }
    }
}