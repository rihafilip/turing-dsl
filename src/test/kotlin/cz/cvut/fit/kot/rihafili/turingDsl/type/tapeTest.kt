package cz.cvut.fit.kot.rihafili.turingDsl.type

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SimpleTapeTest : StringSpec({
    "Setting then getting should return the same character"{
        Tape().apply { set('g') }.get() shouldBe 'g'
    }
})

class WriteFromOffsetTapeTest : StringSpec ({
    "Writing to offset should work"{
        Tape(5).apply{
            set('g', 8)
            move(8)
        }.get() shouldBe 'g'

        Tape(5).apply{
            set('g', -8)
            move(-8)
        }.get() shouldBe 'g'
    }
})

class ReadFromOffsetTapeTest : StringSpec ({
    "Reading from offset should work"{
        Tape(5).apply {
            set('g')
            move(8)
        }.get(-8) shouldBe 'g'

        Tape(5).apply{
            set('g')
            move(-8)
        }.get(8) shouldBe 'g'
    }
})

class InitialEmptyTapeTest : StringSpec ({
    "Even initially 0 length tape should work"{
        Tape(0).apply {
                move(5)
                move(-10)
                set('g')
            }
            .get() shouldBe 'g'
    }
})

class BlankSymbolTest : StringSpec ({
    "Symbols in tape should be initialy empty" {
        val tape = Tape()

        for ( i in -5 .. 5 )
            tape.get( i ) shouldBe BLANK_SYMBOL
    }
})

class KeepSymbolTest : StringSpec ({
    "Keep symbol should keep the curent symbol"{
        Tape().apply {
            set('a')
            set(KEEP_SYMBOL)
        }.get() shouldBe 'a'
    }
})
