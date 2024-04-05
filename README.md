[![Release](https://jitpack.io/v/amaterek/kmp-mvi.svg)](https://jitpack.io/#amaterek/kmp-mvi)

# Kotlin multiplatform MVI library
This is simple MVI helper that make development easier.

Kotlin version: 1.9.22

## Supported platforms
* Android
* JVM
* iOS // Not supported by jitpack.io
* Js
* wasmJs

## Setup

First add jitpack repository

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

Then add this to your dependencies

```gradle
dependencies {
    implementation 'com.github.amaterek.kmp-mvi:mvi:{version}'
}
```

## Usage

Please check sample project in the repo.

### Define Presenter's State, Event, ViewIntent and Action classes
```kotlin
data class State(val counter: Int)

sealed interface Event {

    data object NavigateBack : Event
}

sealed interface ViewIntent {

    data object IncrementCounter : ViewIntent

    data object DecrementCounter : ViewIntent

    data object NavigateBack : ViewIntent
}

sealed interface Action {

    sealed interface Counter : Action {

        data object Increment : Counter

        data object Decrement : Counter
    }

    sealed interface Navigation : Action {

        data object Back : Navigation
    }
}
```

### Define yor presenter and ViewIntent to Action mapper in the presenter
```kotlin
MyPresenter : PresenterWithReducers<State, Event, ViewIntent, Action>(
    initialState = State(counter = 0)
) {
    override fun ViewIntent.toAction(): Action = when (this) {
        ViewIntent.IncrementCounter -> Action.Counter.Increment
        ViewIntent.DecrementCounter -> Action.Counter.Decrement
        ViewIntent.NavigateBack -> Action.Navigation.Back
    }
}
```

### Define your reducers
```kotlin
class CounterReducer : Reducer<State, Event, Action, Action.Counter>() {

    override fun State.reduce(action: CounterAction): State = when (action) {
        Action.Counter.Increment -> onIncrementCounter()
        Action.Counter.Decrement -> onDecrementCounter()
    }

    private fun State.onIncrementCounter(): State {
        return copy(counter = counter + 1)
    }

    private fun State.onDecrementCounter(): State {
        return copy(counter = counter - 1)
    }
}

class NavigationReducer : Reducer<State, Event, Action, Action.Navigation>() {

    override fun State.reduce(action: NavigationAction): State = when (action) {
        Action.Navigation.Back -> onNavigateBack()
    }

    private fun State.onNavigateBack(): State {
        launch {
            emitEvent(Event.NavigateBack)
        }
        return this
    }

```

### Register the reducers in the Presenter
```kotlin
MyPresenter : PresenterWithReducers<State, Event, ViewIntent, Action>(
    initialState = State(counter = 0)
) {
    init {
        registerReducer<Action.Counter>(CounterReducer())
        registerReducer<Action.Navigation>(NavigationReducer())
    }
}
```

### Use the presenter in the view (compose)
```kotlin
fun MyView(presenter: MyPresenter) {

    // Observe presenter's events
    LaunchedEffect(Unit) {
        presenter.eventFlow
            .onEach {
                when (it) {
                    Event.NavigateBack -> ...
                }
            }
            .launchIn(this)
    }

    val state by presenter.stateFlow.collectAsState()

    // Observe presenter's state
    when (state) {
        ...
    }

    // Communicate with presenter by view intents
    Button(
        onClick = {
            presenter.onViewIntent(ViewIntent.NavigateBack)
        },
    ) {
        Text("Back")
    }

    ...
}
```

### Make sure that presenter is initialized with proper coroutine scope.
```kotlin
// Examples:

// Android ViewModeModel
class MyViewModel(val presenter: MyPresenter): ViewModel() {
    init {
        presenter.initialize(viewModelScope)
    }
}

// Voyager ScreenModel
class MyScreenModel(val presenter: MyPresenter): ScreenModel {
    init {
        presenter.initialize(screenModelScope)
    }
}
```
