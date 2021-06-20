package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.InvalidTransitionEnd
import cz.cvut.fit.kot.rihafili.turingDsl.type.MachineEnd
import cz.cvut.fit.kot.rihafili.turingDsl.type.TuringMachine

sealed class TuringMachineOutput

data class StringOutput ( val message: String ) : TuringMachineOutput()

data class TapeOutput ( val offset: Int, val lenght: Int, val printBlank: Boolean ) : TuringMachineOutput()

// TODO pretty print the whole construct
class TuringMachineWrapper(
    private val mainMachine: TuringMachine,
    private val allMachines: Map<String, TuringMachine>,
    private val printOnEnd: List<TuringMachineOutput>,
    private val printOnHalt: List<TuringMachineOutput>
) {

    fun run( noHalt: Boolean = false ) {
        val ret = try{
          mainMachine.run()
        } catch ( e: InvalidTransitionEnd){
            println( "Error while running machine: ${e.message}" )
            return
        }

        when( ret ){
            MachineEnd.HALT -> for ( i in printOnEnd ) printOutput( i )
            MachineEnd.END -> for ( i in printOnHalt ) printOutput( i )
        }
    }

    private fun printOutput ( data: TuringMachineOutput ): Unit = when( data ){
        is StringOutput -> print( data.message )
        is TapeOutput -> {
//            TODO
//            val endIndex = data.offset + data.lenght
//            for ( i in data.offset until endIndex )
//                val
        }
    }
}
