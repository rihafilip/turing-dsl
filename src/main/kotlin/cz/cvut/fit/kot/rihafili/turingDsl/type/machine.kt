package cz.cvut.fit.kot.rihafili.turingDsl.type

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.InvalidTransitionEnd
import javax.crypto.Mac

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
    private val tape: Tape, // workingTape
    private val initialState: String,
    var stopOnFirst: Boolean = false,
    private val states: MutableSet<String> = mutableSetOf(), // Set of all possible states
    private val machines: MutableMap<String, TuringMachine> = mutableMapOf(),
    val transFun: TransitionFunction = TransitionFunction() // Transition function
) {
    init {
        addState( initialState )
        addState( END_STATE )
    }

    // Adds state to states pool
    fun addState( state: String ) = states.add(state)

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

    fun run() = process( initialState )

    private fun process ( state: String ) : MachineEnd {
        if ( state == END_STATE )
            return MachineEnd.END

        val nextList = transFun( TransitionStart( state, tape.get() ) )

        if ( nextList.size == 1 && nextList[0] == Halt )
            return MachineEnd.HALT

        var returnFromProcess = MachineEnd.HALT;

        for ( (index, next) in nextList.withIndex() ) {
            val forked =
                if ( index == nextList.lastIndex ) this // Last nextTransition is not forked
                else fork()

            val ret = when( next ){
                Halt -> throw InvalidTransitionEnd( "Halt should not be among other state transition ends" )
                is NextMachine -> {
                    val innerReturn = forked.machines[next.name]?.run()
                        ?: throw InvalidTransitionEnd("Machine with name ${next.name} not found within TuringMachine context")

                    // If called machine halted, current machine is also halted
                    // else current (
                    when( innerReturn ){
                        MachineEnd.HALT -> MachineEnd.HALT
                        MachineEnd.END -> forked.process(state)
                    }
                }
                is NextState -> forked.moveToNextState(next)
                is PrintTransition -> {
                    print(forked.tape.get())
                    forked.moveToNextState( next.toNextState() )
                }
            }

            if ( stopOnFirst && ret == MachineEnd.END )
                return MachineEnd.END
            else
                returnFromProcess = ret
        }

        return returnFromProcess
    }

    private fun moveToNextState( transition: NextState ) : MachineEnd {
        tape.set( transition.symbol )
        tape.move( transition.pos )
        return process( transition.state )
    }

    private fun fork( tapeArg: Tape? = null ) : TuringMachine {
        val nextTape = tapeArg ?: tape.copy()
        val nextMachines = machines.mapValues { it.value.fork(nextTape) }.toMutableMap()
        return TuringMachine(nextTape, initialState, stopOnFirst, states.toMutableSet(), nextMachines, transFun)
    }
}
