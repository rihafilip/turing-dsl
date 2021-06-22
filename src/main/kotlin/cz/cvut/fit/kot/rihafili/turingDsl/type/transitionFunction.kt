package cz.cvut.fit.kot.rihafili.turingDsl.type

import cz.cvut.fit.kot.rihafili.turingDsl.misc.joinToSet

/**
 * Non-deterministic turing machine
 */
class TransitionFunction ( private val data: MutableMap< TransitionStart, MutableList<TransitionEnd> > = mutableMapOf() ){
    // Input symbols used by this function
    val inputSymbols : Set<Char>
    get() = data.map { ( start, _ ) -> start.symbol }.toSet()

    // All symbols this function works with
    val tapeAlphabet : Set<Char>
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
        else if (index in data)
            data[index]?.add(value) ?: false
        else {
            data[index] = mutableListOf(value)
            true
        }

    override fun toString() = buildString {
        for ( (start, end) in data ){
            append(start)
            append( " -> " )
            append( end.joinToSet() )
            append( '\n' )
        }
    }


}
