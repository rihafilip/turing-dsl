package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.type.MachineEnd
import cz.cvut.fit.kot.rihafili.turingDsl.type.TuringMachine

sealed class TuringMachineOutput

data class StringOutput ( val message: String ) : TuringMachineOutput()

data class TapeOutput ( val offset: Int, val lenght: Int, val printBlank: Boolean ) : TuringMachineOutput()

// TODO pretty print the whole construct
class TuringMachineWrapper {
    var machine: TuringMachine? = null
    val printOnEnd = mutableListOf<TuringMachineOutput>()
    val printOnHalt = mutableListOf<TuringMachineOutput>()

    fun run( noHalt: Boolean = false ) {
        val ret = machine?.run()

        when( ret ){
            MachineEnd.HALT -> for ( i in printOnEnd ) printOutput( i )
            MachineEnd.END -> for ( i in printOnHalt ) printOutput( i )
        }
    }

    private fun printOutput ( data: TuringMachineOutput ): Unit = when( data ){
        is StringOutput -> print( data.message )
        is TapeOutput -> {
//            val endIndex = data.offset + data.lenght
//            for ( i in data.offset until endIndex )
//                val
        }
    }
}
