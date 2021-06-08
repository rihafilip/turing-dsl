package cz.cvut.fit.kot.rihafili.turingDsl.type

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.NoAvailableTransitionException

data class TransitionStart( val state: String, val symbol: Char )

abstract class TransitionEnd
data class NextState ( val state: String, val symbol: Char, val pos: Int ) : TransitionEnd()
data class NextMachine ( val machine: TuringMachine ) : TransitionEnd()

class TuringMachine ( private val tape: Tape ) {

    companion object {
        const val END = "END"
    }

    private val states: MutableSet<String> = mutableSetOf()
    private val transitions: MutableMap<TransitionStart, TransitionEnd> = mutableMapOf()

    var currentState: String? = null
    private set

    val finished
        get() = currentState == END

    fun setInitialState( value: String ) =
        if ( currentState == null )
            currentState = value
        else
            throw IllegalArgumentException("State is already assigned")

    fun addState( st: String ) {
      states.add(st)
    }

    fun addTransition( start: TransitionStart, end: TransitionEnd ) {
        transitions[start] = end
    }

    fun next() : TransitionEnd {
        if ( currentState == null )
            throw IllegalStateException("Initial state not set")

        val next = transitions[ TransitionStart( currentState!!, tape.get() ) ]
            ?: throw NoAvailableTransitionException()

        if ( next is NextState ) {
            tape.set( next.symbol )
            tape.move( next.pos )
            currentState = next.state
        }

        return next
    }
}
