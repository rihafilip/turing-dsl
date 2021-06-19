package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.type.MachineEnd
import cz.cvut.fit.kot.rihafili.turingDsl.type.TuringMachine

sealed class Output

data class StringOutput ( val message: String ) : Output()

data class TapeOutput ( val index: Int, val lenght: Int ) : Output()

class TuringMachineWrapper ( private val machine: TuringMachine ){
    val printOnEnd = mutableListOf<Output>()
    val printOnHalt = mutableListOf<Output>()

    fun run( noHalt: Boolean = false ) {
        val ret = machine.run()

        when( ret ){
            MachineEnd.HALT -> for ( i in printOnEnd ) printOutput( i )
            MachineEnd.END -> for ( i in printOnHalt ) printOutput( i )
        }
    }

    private fun printOutput ( data: Output ): Unit = when( data ){
        is StringOutput -> print( data.message )
//        is TapeOutput -> {
//            val endIndex = data.index + data.length
//            for ( i in data.index until endIndex )
//                val char = machine
//        }
        else -> Unit
    }
}
