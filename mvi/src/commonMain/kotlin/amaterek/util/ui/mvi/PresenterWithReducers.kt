package amaterek.util.ui.mvi

import kotlin.reflect.KClass

abstract class PresenterWithReducers<StateType, EventType, ViewIntentType, ActionType>(
    initialState: StateType,
) : Presenter<StateType, EventType, ViewIntentType, ActionType>(initialState)
        where StateType : Any, EventType : Any, ViewIntentType : Any, ActionType : Any {

    private val reducers =
        mutableListOf<Pair<KClass<out ActionType>, Reducer<StateType, EventType, ActionType, ActionType>>>()

    // TODO Force to call super
    override fun onInitialize() {
        reducers.forEach { (_, reducer) ->
            reducer.initialize(
                coroutineScope = coroutineScope,
                onAction = ::onAction,
                emitEvent = ::emitEvent,
            )
        }
    }

    protected inline fun <reified T : ActionType> registerReducer(
        reducer: Reducer<StateType, EventType, ActionType, out ActionType>,
    ) = registerReducer(reducer, T::class)

    protected fun registerReducer(
        reducer: Reducer<StateType, EventType, ActionType, out ActionType>,
        actionClass: KClass<out ActionType>,
    ) {
        require(!isInitialized)
        @Suppress("UNCHECKED_CAST")
        reducers.add(actionClass to (reducer as Reducer<StateType, EventType, ActionType, ActionType>))
    }

    final override fun StateType.reduce(action: ActionType): StateType {
        val reducerForAction = reducers.filter { (actionClass, _) -> actionClass.isInstance(action) }
        if (reducerForAction.size > 1) error("More then one reduces found for action: ${action::class.simpleName}")
        return reducerForAction.firstOrNull()?.second?.reduceState(this, action)
            ?: error("No reduces found for action: ${action::class.simpleName}")
    }
}