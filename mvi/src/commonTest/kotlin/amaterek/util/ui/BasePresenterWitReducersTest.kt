package amaterek.util.ui

import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class BasePresenterWitReducersTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var coroutineScope: CoroutineScope

    private lateinit var subject: TestPresenterWithReducers

    @BeforeTest
    fun setUp() {
        subject = spyk<TestPresenterWithReducers>()
        coroutineScope = CoroutineScope(testDispatcher)
    }

    @Test
    fun `WHEN initialize is called THEN initialize of registered reducers is called`() {
        val reducers = List(3) {
            mockk<TestReducer<TestAction>>().apply {
                every { initialize(coroutineScope, any(), any()) } just runs
            }
        }.onEach { subject.testRegisterReducer<TestAction>(it) }

        subject.initialize(coroutineScope)

        reducers.forEach {
            verifySequence {
                it.initialize(coroutineScope, any(), any())
            }
        }
    }

    @Test
    fun `WHEN registerReducer is called after initialize has been called THEN throws error`() {
        subject.initialize(coroutineScope)

        assertFailsWith<IllegalArgumentException> {
            subject.testRegisterReducer<TestAction>(TestReducer())
        }
    }

    @Test
    fun `WHEN onViewIntent is called and there is correcponding reducer THEN maps intent to action and call reduce on the reducer`() {
        val viewIntent = mockk<TestViewIntent>()
        val action = mockk<TestAction>()

        every { with(subject) { viewIntent.toAction() } } returns action

        val reducer = spyk<TestReducer<TestAction>>()
        every { with(reducer) { initialTestState.reduce(action) } } returns mockk()

        subject.testRegisterReducer<TestAction>(reducer)
        subject.initialize(coroutineScope)

        subject.onViewIntent(viewIntent)

        verify(exactly = 1) {
            with(subject) { viewIntent.toAction() }
            with(reducer) { initialTestState.reduce(action) }
        }
    }

    @Test
    fun `WHEN onViewIntent is called and there is no correcponding reducer THEN maps intent to action and throws error`() {
        val viewIntent = mockk<TestViewIntent>()
        val action = mockk<TestAction>()

        every { with(subject) { viewIntent.toAction() } } returns action

        subject.initialize(coroutineScope)

        assertFailsWith<IllegalStateException> {
            subject.onViewIntent(viewIntent)
        }

        verify(exactly = 1) {
            with(subject) { viewIntent.toAction() }
        }
    }

    @Test
    fun `WHEN onViewIntent is called and more then one reducer is registered THEN maps intent to action and call reduce on corresponding reducer`() {
        val viewIntent = mockk<TestViewIntent>()
        val action = mockk<TestAction2>()

        every { with(subject) { viewIntent.toAction() } } returns action

        val reducer1 = spyk<TestReducer<TestAction1>>()

        val reducer2 = spyk<TestReducer<TestAction2>>()
        every { with(reducer2) { initialTestState.reduce(action) } } returns mockk()

        subject.testRegisterReducer<TestAction1>(reducer1)
        subject.testRegisterReducer<TestAction2>(reducer2)
        subject.initialize(coroutineScope)

        subject.onViewIntent(viewIntent)

        verify(exactly = 1) {
            with(subject) { viewIntent.toAction() }
            with(reducer2) { initialTestState.reduce(action) }
        }
    }

    @Test
    fun `WHEN onViewIntent is called and more then one reducers are registered for the same action THEN throws error`() {
        val viewIntent = mockk<TestViewIntent>()
        val action = mockk<TestAction>()

        every { with(subject) { viewIntent.toAction() } } returns action

        subject.testRegisterReducer<TestAction>(TestReducer())
        subject.testRegisterReducer<TestAction>(TestReducer())

        subject.initialize(coroutineScope)

        assertFailsWith<IllegalStateException> {
            subject.onViewIntent(viewIntent)
        }
    }
}

private class TestAction1 : TestAction()
private class TestAction2 : TestAction()