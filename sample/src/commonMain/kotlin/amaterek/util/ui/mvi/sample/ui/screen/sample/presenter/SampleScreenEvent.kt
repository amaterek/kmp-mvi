package amaterek.util.ui.mvi.sample.ui.screen.sample.presenter

internal sealed interface SampleScreenEvent {

    data object NavigateBack : SampleScreenEvent
}