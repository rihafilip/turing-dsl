package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.InvalidTransitionEnd
import cz.cvut.fit.kot.rihafili.turingDsl.misc.offset
import cz.cvut.fit.kot.rihafili.turingDsl.type.MachineEnd
import cz.cvut.fit.kot.rihafili.turingDsl.type.SYMBOL_CONST
import cz.cvut.fit.kot.rihafili.turingDsl.type.Tape
import cz.cvut.fit.kot.rihafili.turingDsl.type.TuringMachine

// Classes used for printing output
sealed class TuringMachineOutput
data class StringOutput ( val message: String ) : TuringMachineOutput()
data class TapeOutput ( val offset: Int, val lenght: Int, val printBlank: Boolean ) : TuringMachineOutput()

/**
 * Wrapper for processing and printing from Turing machine
 */
class TuringMachineWrapper(
    private val mainMachine: TuringMachine,
    private val tape: Tape,
    private val allMachines: Map<String, TuringMachine>,
    private val printOnEnd: List<TuringMachineOutput>,
    private val printOnHalt: List<TuringMachineOutput>
) {
    // Start execution of Turing Machine
    fun start( debug: Boolean = false) {
        val (state, tape) = try{
          mainMachine.start(tape, debug)
        } catch ( e: InvalidTransitionEnd ){
            println( "Error while running machine: ${e.message}" )
            return
        }

        val list = when( state ){
            MachineEnd.HALT -> printOnHalt
            MachineEnd.END -> printOnEnd
        }

        for ( i in list )
            printOutput( i, tape )
    }

    // Print final output
    private fun printOutput ( data: TuringMachineOutput, printTape: Tape ): Unit = when( data ){
        is StringOutput -> print( data.message )
        is TapeOutput -> {
            val endIndex = data.offset + data.lenght

            for ( i in data.offset until endIndex ) {
                val ch = printTape.get(i)
                // Blank is printed if printBlank is true
                // otherwise it is omitted
                if ( ch == SYMBOL_CONST.BLANK ) {
                    if ( data.printBlank )
                        print( ' ' )
                }
                else print(ch)
            }

        }
    }

    // Pretty print this wrapper
    fun print() = apply { print("$this\n") }

    override fun toString() = buildString {
        append( "[Main machine]\n" )
        append( mainMachine.toString().offset() )
        append( '\n' )

        for ( (name, machine) in allMachines ) {
            append( "[$name]\n" )
            append( machine.toString().offset() )
            append( '\n' )
        }

        append( "Initial tape state:\n" )
        append( tape.toString().offset() )
    }


}
