package amaterek.util.ui.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class Presenter<StateType, EventType, ViewIntentType, ActionType>(
    initialState: StateType,
) where StateType : Any, EventType : Any, ViewIntentType : Any, ActionType : Any {

    protected lateinit var coroutineScope: CoroutineScope
        private set

    private val _stateFlow = MutableStateFlow(initialState)
    val stateFlow = _stateFlow.asStateFlow()

    // Warning: Can have only one subscriber
    private var numberOfSubscribers = 0
    private val eventChannel = Channel<EventType>()
    val eventFlow: Flow<EventType> = eventChannel.receiveAsFlow()
        .onStart {
            if (++numberOfSubscribers > 1) error("event flow can have only one subscriber")
        }
        .onCompletion {
            if (--numberOfSubscribers < 0) error("something went wrong")
        }

    protected val isInitialized: Boolean
        get() = ::coroutineScope.isInitialized

    fun initialize(coroutineScope: CoroutineScope) {
        if (isInitialized) error("Presenter is already initialized")
        this.coroutineScope = coroutineScope
        onInitialize()
    }

    fun onViewIntent(viewIntent: ViewIntentType) = onAction(viewIntent.toAction())

    protected open fun onInitialize() = Unit

    protected abstract fun ViewIntentType.toAction(): ActionType

    protected abstract fun StateType.reduce(action: ActionType): StateType

    protected fun onAction(action: ActionType) {
        _stateFlow.value.let { state ->
            _stateFlow.value = state.reduce(action)
        }
    }

    protected fun emitEvent(event: EventType) {
        coroutineScope.launch {
            eventChannel.send(event)
        }
    }
}