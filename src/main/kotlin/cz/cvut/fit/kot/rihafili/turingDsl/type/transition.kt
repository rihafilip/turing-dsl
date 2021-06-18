package cz.cvut.fit.kot.rihafili.turingDsl.type

// Input to transition function
data class TransitionStart( val state: String, val symbol: Char )

// Output from transition function
sealed class TransitionEnd

// Transition to next state
data class NextState ( val state: String, val symbol: Char, val pos: Int ) : TransitionEnd()

// Give control to next machine, name must be correct identifier in enclosing Turing machine
data class NextMachine ( val name: String ) : TransitionEnd()

// Print current symbol and then transition
data class PrintTransition ( val state: String, val symbol: Char, val pos: Int ) : TransitionEnd() {
    fun toNextState() : NextState = NextState( state, symbol, pos )
}

// Class signalising halting of turing machine
object Halt : TransitionEnd()

/**
 * Non-deterministic turing machine
 */
class TransitionFunction ( private val data: MutableMap< TransitionStart, MutableList<TransitionEnd> > = mutableMapOf() ){
    // Input symbols used by this function
    val inputSymbols : Set<Char>
    get() = data.map { ( start, _ ) -> start.symbol }.toSet()


    // All symbols this function works with
    val workingAlphabet : Set<Char>
    get() {
        val output = mutableSetOf(SYMBOL_CONST.BLANK)

        for ( (start, endList) in data ) {
            output.add(start.symbol)

            for ( end in endList ){
                when( end ){
                    is NextState -> output.add( end.symbol )
                    is PrintTransition -> output.add( end.symbol )
                    else -> Unit
                }
            }
        }

        return output
    }

    operator fun invoke( start: TransitionStart ) : List<TransitionEnd> = data[start] ?: listOf(Halt)

    operator fun set ( index: TransitionStart, value: TransitionEnd ) : Boolean =
        if (index.state == END_STATE || value is Halt )
            false
        else if (data.containsKey(index))
            data[index]?.add(value) ?: false
        else {
            data[index] = mutableListOf(value)
            true
        }

}
