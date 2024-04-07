package amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.reducer

import amaterek.util.ui.mvi.Reducer
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.Action
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.Event
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.SampleScreenEvent
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.SamplePresenter
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.State

private typealias NavigationAction = SamplePresenter.Action.Navigation

internal class SampleNavigationReducer : Reducer<State, Event, Action, NavigationAction>() {

    override fun State.reduce(action: NavigationAction): State = when (action) {
        SamplePresenter.Action.Navigation.Back -> onNavigateBack()
    }

    private fun State.onNavigateBack(): State {
        // State is not checked because action is valid in all states
        launch {
            emitEvent(SampleScreenEvent.NavigateBack)
        }
        return this
    }
}