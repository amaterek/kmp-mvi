package amaterek.util.ui.mvi.sample.ui.screen.sample.presenter

import amaterek.util.ui.mvi.PresenterWithReducers
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.reducer.SampleCounterReducer
import amaterek.util.ui.mvi.sample.ui.screen.sample.presenter.reducer.SampleNavigationReducer

internal typealias State = SampleState
internal typealias Event = SampleScreenEvent
internal typealias ViewIntent = SampleViewIntent
internal typealias Action = SamplePresenter.Action

internal class SamplePresenter : PresenterWithReducers<State, Event, ViewIntent, Action>(
    initialState = State(counter = 0)
) {

    sealed interface Action {

        sealed interface Counter : Action {

            data object Increment : Counter

            data object Decrement : Counter
        }

        sealed interface Navigation : Action {

            data object Back : Navigation
        }
    }

    init {
        registerReducer<Action.Counter>(SampleCounterReducer())
        registerReducer<Action.Navigation>(SampleNavigationReducer())
    }

    override fun ViewIntent.toAction(): Action = when (this) {
        SampleViewIntent.IncrementCounter -> Action.Counter.Increment
        SampleViewIntent.DecrementCounter -> Action.Counter.Decrement
        SampleViewIntent.NavigateBack -> Action.Navigation.Back
    }
}