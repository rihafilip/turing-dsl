package cz.cvut.fit.kot.rihafili.turingDsl.type

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class HaltTest : StringSpec({
    "Transition without end should return Halt"{
        TransitionFunction()(
            TransitionStart( "x", 'x' )
        ) shouldContainExactly listOf(Halt)
    }
})

class SingleTransitionTest : StringSpec({
    "Transition should be successful"{
        val start = TransitionStart( "y", 'x' )
        val end = NextState( "x", 'x', 0 )

        val func = TransitionFunction()
        func.set(start, end) shouldBe true
        func(start) shouldContainExactly listOf(end)
    }
})

class MultipleTransitionsTest : StringSpec({
    "Transition with multiple ends should be successful"{
        val start = TransitionStart( "y", 'x' )
        val ends = listOf(
            NextState( "x", 'x', 0 ),
            PrintTransition( "x", 'x', 0  ),
            NextMachine( "m1" )
        )

        val func = TransitionFunction()
        for ( end in ends )
            func.set(start, end) shouldBe true

        func(start) shouldContainExactly ends
    }
})

class IncorrectSetTest : StringSpec({
    "Incorrect input should return false"{
        val func = TransitionFunction()
        func.set(
            TransitionStart( "y", 'x' ),
            Halt
        ) shouldBe false

        func.set(
            TransitionStart(END_STATE, 'x'),
            NextState( "x", 'x', 0 )
        ) shouldBe false
    }
})

class WorkingAlphabetTest : StringSpec({
    "Working alphabet should be correct"{
        val start = TransitionStart( "y", 'a' )
        val ends = listOf(
            NextState( "x", 'b', 0 ),
            PrintTransition( "x", 'c', 0  ),
            NextMachine( "m1" )
        )

        val func = TransitionFunction()
        for ( end in ends )
            func.set(start, end) shouldBe true

        func.workingAlphabet shouldContainExactly listOf(SYMBOL_CONST.BLANK, 'a', 'b', 'c')
    }
})

class InputSymbolsTest : StringSpec({
    "Input symbols should be correct"{
        val start1 = TransitionStart("y", 'a')
        val start2 = TransitionStart("y", 'x')
        val ends = listOf(
            NextState("x", 'b', 0),
            PrintTransition("x", 'c', 0),
            NextMachine("m1")
        )

        val func = TransitionFunction()
        for (end in ends)
            func.set(start1, end) shouldBe true

        func.set(start2, ends[0]) shouldBe true

        func.inputSymbols shouldContainExactly listOf('a', 'x')
    }
})
