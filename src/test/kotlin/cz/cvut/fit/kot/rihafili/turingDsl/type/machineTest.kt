package cz.cvut.fit.kot.rihafili.turingDsl.type

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf

private val nextMachine = TuringMachine(Tape())

private fun getTestMachine( tape: Tape ) : TuringMachine {
    val machine = TuringMachine( tape )

    machine.addState("a")
    machine.addState("b")
    machine.setInitialState("a")

    // a, 'g' -> b, 'h', 0
    machine.addTransition( TransitionStart( "a", 'g' ),
        NextState( "b", 'h', 0 )
    )

    // a, 'h' -> end, 'h', 0
    machine.addTransition( TransitionStart( "a", 'e' ),
        NextState( TuringMachine.END, 'e', 0 )
    )

    // a, 'x' calls next machine
    machine.addTransition( TransitionStart("a", 'x'),
        NextMachine( nextMachine )
    )

    return machine
}

class DoubleSetInitialState : StringSpec ({
    "Setting initial set twice should throw" {
        shouldThrow<IllegalArgumentException> {
            TuringMachine(Tape()).apply {
                setInitialState("a")
                setInitialState("a")
            }
        }
    }
})

class TransitionTest : StringSpec ({
    "Transition to next state should set everything correctly"{
        val tape = Tape().apply { set('g') }
        val machine = getTestMachine( tape )
        val end = machine.next()

        machine.finished shouldBe false
        tape.get() shouldBe 'h'

        end.shouldBeTypeOf<NextState>()
        end.pos shouldBe 0
        end.state shouldBe "b"
        end.symbol shouldBe 'h'
    }
})

class TransitionToNextMachineTest : StringSpec ({
    "Transition to next machine should be successful"{
        val tape = Tape().apply { set('x') }
        val machine = getTestMachine( tape )
        val end = machine.next()

        machine.finished shouldBe false
        tape.get() shouldBe 'x'

        end.shouldBeTypeOf<NextMachine>()
        end.machine shouldBeSameInstanceAs nextMachine
    }
})


class TransitionToEndTest : StringSpec ({
    "Transition to test should be successful"{
        val tape = Tape().apply { set('e') }
        val machine = getTestMachine( tape )
        val end = machine.next()

        machine.finished shouldBe true
        tape.get() shouldBe 'e'

        end.shouldBeTypeOf<NextState>()
        end.state shouldBe TuringMachine.END
        end.pos shouldBe 0
        end.symbol shouldBe 'e'
    }
})
