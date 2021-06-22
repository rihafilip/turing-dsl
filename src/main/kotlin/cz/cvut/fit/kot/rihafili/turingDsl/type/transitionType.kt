package cz.cvut.fit.kot.rihafili.turingDsl.type

// Input to transition function
data class TransitionStart( val state: String, val symbol: Char ){
    override fun toString() = "($state, $symbol)"
}

// Output from transition function
sealed class TransitionEnd

// Transition to next state
data class NextState ( val state: String, val symbol: Char, val pos: Int ) : TransitionEnd() {
    fun toPrintTransition() : PrintTransition = PrintTransition( state, symbol, pos )

    override fun toString() = "($state, $symbol, $pos)"
}

// Print current symbol and then transition
data class PrintTransition ( val state: String, val symbol: Char, val pos: Int ) : TransitionEnd() {
    fun toNextState() : NextState = NextState( state, symbol, pos )

    override fun toString() = "print($state, $symbol, $pos)"
}
// Give control to next machine, name must be correct identifier in enclosing Turing machine
data class NextMachine ( val name: String ) : TransitionEnd() {
    override fun toString() = "call($name)"
}

// Class signalising halting of turing machine
object Halt : TransitionEnd() {
    override fun toString() = "Halt"
}
