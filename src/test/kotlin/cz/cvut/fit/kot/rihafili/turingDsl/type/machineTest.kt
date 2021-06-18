package cz.cvut.fit.kot.rihafili.turingDsl.type

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

fun makeMachine(): TuringMachine {
    val tape = Tape()
    val submachine = TuringMachine(
        tape,
        "A",
        false,
        mutableSetOf("A")
    )

    submachine.addTransition(
        TransitionStart("A", SYMBOL_CONST.BLANK),
        NextState(END_STATE, 'x', 0)
    ) shouldBe true

    return TuringMachine(
        tape,
        "Init",
        false,
        mutableSetOf("Init", "A", "B"),
        mutableMapOf("Submachine" to submachine)
    )
}

class IncorrectTransitionTest : StringSpec({
    "Adding transition with state or machine not registered will fail"{
        val machine = makeMachine()

        machine.addTransition(
            TransitionStart("X", 'x'),
            NextState( "A", 'x', 0 )
        ) shouldBe false

        machine.addTransition(
            TransitionStart("A", 'x'),
            NextState( "X", 'x', 0 )
        ) shouldBe false

        machine.addTransition(
            TransitionStart("A", 'x'),
            NextMachine( "Not registered" )
        ) shouldBe false
    }
})

class HaltedMachineTest : StringSpec({
    "Machine with no transitions should be halted"{
        makeMachine().run() shouldBe MachineEnd.HALT
    }
})

class EndedMachineTest : StringSpec({
    "Correct machine should end"{
        val machine = makeMachine()

        machine.addTransition(
            TransitionStart("Init", SYMBOL_CONST.BLANK),
            NextState("A", SYMBOL_CONST.BLANK, 0)
        ) shouldBe true

        machine.addTransition(
            TransitionStart("A", SYMBOL_CONST.BLANK),
            NextMachine( "Submachine" )
        ) shouldBe true

        machine.addTransition(
            TransitionStart("A", 'x'),
            NextState("B", SYMBOL_CONST.BLANK, 0)
        ) shouldBe true

        machine.addTransition(
            TransitionStart("B", SYMBOL_CONST.BLANK),
            NextState(END_STATE, SYMBOL_CONST.BLANK, 0)
        ) shouldBe true

        machine.run() shouldBe MachineEnd.END
    }
})
