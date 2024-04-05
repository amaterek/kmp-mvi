package amaterek.util.ui

import amaterek.util.ui.mvi.Presenter
import amaterek.util.ui.mvi.PresenterWithReducers
import amaterek.util.ui.mvi.Reducer
import kotlin.reflect.KClass

class TestState
class TestEvent
class TestViewIntent
open class TestAction

val initialTestState = TestState()

class TestBasePresenter : Presenter<TestState, TestEvent, TestViewIntent, TestAction>(initialTestState) {

    public override fun TestViewIntent.toAction(): TestAction {
        error("Should not be called without mocking")
    }

    public override fun TestState.reduce(action: TestAction): TestState {
        error("Should not be called without mocking")
    }

    // Make protected method as public

    public override fun onInitialize() {
        super.onInitialize()
    }

    fun testEmitEvent(event: TestEvent) = emitEvent(event)
}

class TestPresenterWithReducers : PresenterWithReducers<TestState, TestEvent, TestViewIntent, TestAction>(
    initialTestState,
) {
    public override fun TestViewIntent.toAction(): TestAction {
        error("Should not be called without mocking")
    }

    inline fun <reified T : TestAction> testRegisterReducer(
        reducer:  Reducer<TestState, TestEvent, TestAction, out TestAction>,
    ) {
        testRegisterReducer(reducer, T::class)
    }

    fun testRegisterReducer(
        reducer: Reducer<TestState, TestEvent, TestAction, out TestAction>,
        actionClass: KClass<out TestAction>,
    ) = registerReducer(reducer, actionClass)
}

class TestReducer<MyActionType> :
    Reducer<TestState, TestEvent, TestAction, MyActionType>() where MyActionType : TestAction {

    public override fun TestState.reduce(action: MyActionType): TestState {
        error("Should not be called without mocking")
    }
}