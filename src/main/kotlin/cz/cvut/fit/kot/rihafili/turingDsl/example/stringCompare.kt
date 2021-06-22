package cz.cvut.fit.kot.rihafili.turingDsl.example

import cz.cvut.fit.kot.rihafili.turingDsl.dsl.MachineBuilder
import cz.cvut.fit.kot.rihafili.turingDsl.dsl.buildTuring
import cz.cvut.fit.kot.rihafili.turingDsl.type.END_STATE

fun MachineBuilder.addTransitions ( chars: Set<Char> ) {
    for ( i in chars ){
        val state = "FIND$i"
        states( state )
        tr( "init", i ) goto tr( +10, state, i )
        tr( state, i ) goto tr( -9, "init", i )
    }
}

// Compares if two string of max length 9 are equal
fun compare (lhs: String, rhs: String) {
    if ( lhs.length > 9 || rhs.length > 9  )
        return

    val chars = lhs.toMutableList().apply { addAll( rhs.toList() ) }.toSet()

    buildTuring {
        input{
            0 set lhs
            10 set rhs
        }

        mainMachine {
            states( "init", "end" )
            initialState = "init"

            tr( "init", '#' ) goto tr( +10, "end", '#' )
            tr( "end", '#' ) goto tr( 0, END_STATE, '#' )

            addTransitions(chars)
        }

        printOutput {
            message( "Strings are equal\n")
        }

        printOnHalt {
            message("Strings are not equal\n")
        }
    }?.start()
}

fun main() {
    compare( "abcde", "abcdeg" )
    compare( "abcde", "abcde" )
    compare( "", "" )
    compare( "", "a" )
}
