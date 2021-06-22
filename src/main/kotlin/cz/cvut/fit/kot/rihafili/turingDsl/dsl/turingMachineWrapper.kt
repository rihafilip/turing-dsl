package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.InvalidTransitionEnd
import cz.cvut.fit.kot.rihafili.turingDsl.misc.offset
import cz.cvut.fit.kot.rihafili.turingDsl.type.MachineEnd
import cz.cvut.fit.kot.rihafili.turingDsl.type.SYMBOL_CONST
import cz.cvut.fit.kot.rihafili.turingDsl.type.Tape
import cz.cvut.fit.kot.rihafili.turingDsl.type.TuringMachine

sealed class TuringMachineOutput

data class StringOutput ( val message: String ) : TuringMachineOutput()

data class TapeOutput ( val offset: Int, val lenght: Int, val printBlank: Boolean ) : TuringMachineOutput()

// TODO pretty print the whole construct
class TuringMachineWrapper(
    private val mainMachine: TuringMachine,
    private val tape: Tape,
    private val allMachines: Map<String, TuringMachine>,
    private val printOnEnd: List<TuringMachineOutput>,
    private val printOnHalt: List<TuringMachineOutput>
) {

    fun start( stopOnFirst: Boolean = false, debug: Boolean = false) {
        val ret = try{
          mainMachine.start(stopOnFirst, tape, debug)
        } catch ( e: InvalidTransitionEnd){
            println( "Error while running machine: ${e.message}" )
            return
        }

        when( ret.first ){
            MachineEnd.HALT -> for ( i in printOnHalt ) printOutput( i, ret.second )
            MachineEnd.END -> for ( i in printOnEnd ) printOutput( i, ret.second )
        }
    }

    private fun printOutput ( data: TuringMachineOutput, printTape: Tape ): Unit = when( data ){
        is StringOutput -> print( data.message )
        is TapeOutput -> {
            val endIndex = data.offset + data.lenght
            for ( i in data.offset until endIndex ){
                val ch = printTape.get(i)
                if ( data.printBlank && ch == SYMBOL_CONST.BLANK )
                    print( ' ' )
                else print(ch)
            }

        }
    }

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
