package amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.reducer

import amaterek.util.ui.mvi.Reducer
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.Action
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.Event
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.SamplePresenter
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.State

private typealias CounterAction = SamplePresenter.Action.Counter

internal class SampleCounterReducer : Reducer<State, Event, Action, CounterAction>() {

    override fun State.reduce(action: CounterAction): State = when (action) {
        SamplePresenter.Action.Counter.Increment -> onIncrementCounter()
        SamplePresenter.Action.Counter.Decrement -> onDecrementCounter()
    }

    private fun State.onIncrementCounter(): State {
        // State is not checked because action is valid in all states
        return copy(counter = counter + 1)
    }

    private fun State.onDecrementCounter(): State {
        // State is not checked because action is valid in all states
        return copy(counter = counter - 1)
    }
}