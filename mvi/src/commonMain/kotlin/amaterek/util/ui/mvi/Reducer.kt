package amaterek.util.ui.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

abstract class Reducer<StateType, EventType, ActionType, MyActionType> where MyActionType : ActionType {

    private lateinit var coroutineScope: CoroutineScope
    protected lateinit var onAction: (ActionType) -> Unit
        private set
    protected lateinit var emitEvent: (EventType) -> Unit
        private set

    internal fun initialize(
        coroutineScope: CoroutineScope,
        onAction: (ActionType) -> Unit,
        emitEvent: (EventType) -> Unit,
    ) {
        this.coroutineScope = coroutineScope
        this.onAction = onAction
        this.emitEvent = emitEvent
        onInitialize()
    }

    protected open fun onInitialize() = Unit

    protected abstract fun StateType.reduce(action: MyActionType): StateType

    internal fun reduceState(state: StateType, action: MyActionType): StateType =
        with(state) { reduce(action) }

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job =
        coroutineScope.launch {
            yield()
            block()
        }

    protected fun launchImmediate(block: suspend CoroutineScope.() -> Unit): Job =
        coroutineScope.launch(block = block)

    protected fun <T> async(block: suspend () -> T): Deferred<T> =
        coroutineScope.async {
            yield()
            block()
        }
}