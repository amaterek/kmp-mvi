package amaterek.util.ui

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifySequence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class BasePresenterTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var coroutineScope: CoroutineScope

    private lateinit var subject: TestBasePresenter

    @BeforeTest
    fun setUp() {
        subject = spyk<TestBasePresenter>()
        coroutineScope = CoroutineScope(testDispatcher)
    }

    @Test
    fun `WHEN initialize is called THEN onInitialize is called`() {
        subject.initialize(coroutineScope)

        verifySequence {
            subject.initialize(coroutineScope)

            subject.onInitialize()
        }
    }

    @Test
    fun `WHEN initialize is called more than once THEN throws error`() {
        subject.initialize(coroutineScope)

        assertFailsWith<IllegalStateException> {
            subject.initialize(coroutineScope)
        }
    }

    @Test
    fun `WHEN onViewIntent is called THEN toAction is called on ViewIntent and reduce with mapped action on initial state and emits new state`() =
        runTest(testDispatcher) {
            val viewIntent = mockk<TestViewIntent>()
            val action = mockk<TestAction>()
            val nextState = mockk<TestState>()
            every { with(subject) { viewIntent.toAction() } } returns action
            every { with(subject) { initialTestState.reduce(action) } } returns nextState

            subject.stateFlow.test {
                assertEquals(initialTestState, awaitItem())

                subject.onViewIntent(viewIntent)

                assertEquals(nextState, awaitItem())
                expectNoEvents()

                verifySequence {
                    subject.stateFlow
                    subject.onViewIntent(viewIntent)

                    with(subject) { viewIntent.toAction() }
                    with(subject) { initialTestState.reduce(action) }
                }
            }
        }

    @Test
    fun `WHEN more than two observers are registered THEN crashes`() = runTest(testDispatcher, timeout = 1.seconds) {
        subject.eventFlow.launchIn(coroutineScope)

        assertFailsWith<IllegalStateException> {
            subject.eventFlow.collect { }
        }
    }

    @Test
    fun `WHEN second observers is registered after first has been removed THEN second can observe events`() =
        runTest(testDispatcher) {
            subject.eventFlow.launchIn(coroutineScope).cancel()

            subject.eventFlow.launchIn(coroutineScope)
        }

    @Test
    fun `WHEN emitEvent is called THEN emits the event`() = runTest(testDispatcher) {
        val testEvent = mockk<TestEvent>()

        subject.initialize(coroutineScope)

        subject.eventFlow.test {
            subject.testEmitEvent(testEvent)
            assertEquals(testEvent, awaitItem())
            expectNoEvents()
        }
    }
}