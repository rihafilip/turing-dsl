package cz.cvut.fit.kot.rihafili.turingDsl.example

import cz.cvut.fit.kot.rihafili.turingDsl.dsl.MachineBuilder
import cz.cvut.fit.kot.rihafili.turingDsl.dsl.buildTuring
import cz.cvut.fit.kot.rihafili.turingDsl.type.END_STATE

// Finds substring 'abba' in input string
fun main() {
    buildTuring {
        mainMachine{
            states( "A", "1", "2", "3")
            initialState = "A"

            tr( "A", 'a' ) goto tr( +1, "1", 'a' )
            tr( "1", 'b' ) goto tr( +1, "2", 'b' )
            tr( "2", 'b' ) goto tr( +1, "3", 'b' )
            tr( "3", 'a' ) goto tr( +1, END_STATE, 'a' )

            tr( "A", 'a' ) goto tr( +1, "A", 'a' )
            tr( "A", 'b' ) goto tr( +1, "A", 'b' )
        }

        input {
            0 set "aabababbaa"
        }

        printOutput (printBlank = false) {
            -50 lenght 50
            message( " has substring 'abba'" )
        }

        printOnHalt (printBlank = false) {
            -50 lenght 50
            message( " has no substring 'abba'" )
        }
    }?.print()?.start( debug = true )
}
