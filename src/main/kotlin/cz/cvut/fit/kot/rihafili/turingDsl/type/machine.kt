package cz.cvut.fit.kot.rihafili.turingDsl.type

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.InvalidTransitionEnd
import cz.cvut.fit.kot.rihafili.turingDsl.misc.joinToSet
import cz.cvut.fit.kot.rihafili.turingDsl.misc.offset

enum class MachineEnd {
    HALT, END
}

/**
 * A basic Turing machine
 * States are implemented as Strings
 * Final states are set to one reserved state "END_STATE"
 * Working alphabet and input symbols are not explicitly set, but all Chars are used instead
 */
class TuringMachine(
    private val initialState: String,
    private val states: MutableSet<String> = mutableSetOf(), // Set of all possible states
    private val machines: MutableMap<String, TuringMachine> = mutableMapOf(),
    private val transFun: TransitionFunction = TransitionFunction() // Transition function
) {
    init {
        states.add( initialState )
        states.add( END_STATE )
    }

    fun addMachine ( name: String, machine: TuringMachine ) = machines.put( name, machine )

    // Adds transition to transitions function
    fun addTransition( start: TransitionStart, end: TransitionEnd ) : Boolean {
        if ( start.state !in states )
            return false

        when(end){
            Halt -> return false
            is NextState -> if ( end.state !in states ) return false
            is PrintTransition -> if ( end.state !in states ) return false
            is NextMachine -> if ( end.name !in machines ) return false
        }

        return transFun.set(start, end)
    }

    // Stop on first signalizes if machine ends on first found "end" or it continues
    fun start(tape: Tape, debug: Boolean ) : Pair<MachineEnd, Tape> {
        val runtime = TuringMachineRuntime( tape.copy(), debug )
        return runtime.start( this )
    }

    override fun toString() = buildString {
        append( "states Q: ")
        append( states.joinToSet() )
        append('\n')

        append( "input symbols ∑: " )
        append( transFun.inputSymbols.joinToSet() )
        append( '\n' )

        append( "tape alphabet G: " )
        append( transFun.tapeAlphabet.joinToSet() )
        append( '\n' )

        append( "initial state q: $initialState \n" )
        append( "blank symbol: ${SYMBOL_CONST.BLANK}\n"  )
        append( "end states F: { $END_STATE }\n")

        append( "transition function δ: (Q\\F) x G -> Q x G x N\n" )
        append( transFun.toString().offset() )
    }

    // Runtime wrapper around TuringMachine
    private inner class TuringMachineRuntime ( private val tape: Tape, private val debug: Boolean ) {
        fun start ( machine: TuringMachine ) = process( machine, machine.initialState, "Main" )

        private fun process( machine: TuringMachine, state: String, name: String ) : Pair<MachineEnd, Tape> {
            if ( state == END_STATE )
                return MachineEnd.END to tape

            val nextList = machine.transFun( TransitionStart(state, tape.get()) )
            if ( debug ){
                println( "[$name] ($state, ${tape.get()}) -> ${nextList.joinToSet()}" )
                println( tape )
            }

            var ret : Pair<MachineEnd, Tape>?

            for ( (index, next) in nextList.withIndex() ){
                val forked =
                    if ( index == nextList.lastIndex ) this // Last nextTransition is not forked
                    else TuringMachineRuntime( tape.copy(), debug )

                ret = when ( next ){
                    Halt -> return MachineEnd.HALT to tape
                    is NextMachine -> {
                        val nextMachine = machine.machines[ next.name ]
                            ?: throw InvalidTransitionEnd ("Machine with name ${next.name} not found within TuringMachine context")
                        val semiRet = forked.process( nextMachine, nextMachine.initialState, next.name )
                        when( semiRet.first ){
                            MachineEnd.HALT -> semiRet
                            MachineEnd.END -> process( machine, state, name )
                        }
                    }
                    is NextState -> forked.transition( machine, next, name )
                    is PrintTransition ->{
                        print( tape.get() )
                        forked.transition( machine, next.toNextState(), name )
                    }
                }

                if ( ret.first == MachineEnd.END
                    || index == nextList.lastIndex
                ){
                    return ret
                }
            }

            return MachineEnd.HALT to tape
        }

        private fun transition ( machine: TuringMachine, transition: NextState, name: String ) : Pair<MachineEnd, Tape> {
            tape.set( transition.symbol )
            tape.move( transition.pos )
            return process( machine, transition.state, name )
        }
    }


}
