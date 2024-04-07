package amaterek.util.ui.mvi.sample.ui.screen.sample.presenter

internal sealed interface SampleViewIntent {

    data object IncrementCounter : SampleViewIntent

    data object DecrementCounter : SampleViewIntent

    data object NavigateBack : SampleViewIntent
}