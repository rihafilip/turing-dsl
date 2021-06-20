package cz.cvut.fit.kot.rihafili.turingDsl

import cz.cvut.fit.kot.rihafili.turingDsl.dsl.buildTuring
import cz.cvut.fit.kot.rihafili.turingDsl.type.END_STATE

fun main() {
    buildTuring {
        mainMachine {
            states ("Init", "a", "b", "c", "d" )

            initialState = "Init"

            tr( "a", 'c' ) goto tr(+10, "b", 'h')
            tr( "a", 'c') goto tr( +10 ,  "b" , 'h')
            tr( "a", 'd') call "add"
            tr( "a", '#') goto tr( 0 ,  "c", 'h')

            tr( "a", 'k' ) printGoto tr( 10, "a", '&' )
        }

        machine ( "add" ) {
            states( "initial" )
            initialState = "initial"

            tr( "a", 'c' ) goto tr( 0, END_STATE, 'x' )
        }

        input {
            0 set "65"
        }


        printOutput (printBlank = true) {
            64 lenght 10
            message( "Successful" )
        }

        printOnHalt{
            message( "Unsuccessful" )
        }

    }
        //?.printGraph()?.run( noHalt = true ) // TODO
}
