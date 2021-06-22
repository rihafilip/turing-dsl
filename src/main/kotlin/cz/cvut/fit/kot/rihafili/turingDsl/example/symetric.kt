package cz.cvut.fit.kot.rihafili.turingDsl.example

import cz.cvut.fit.kot.rihafili.turingDsl.dsl.TuringBuilder
import cz.cvut.fit.kot.rihafili.turingDsl.dsl.buildTuring
import cz.cvut.fit.kot.rihafili.turingDsl.type.END_STATE

fun TuringBuilder.makeFind(ch: Char ){
    machine("find$ch") {
        states( "Init", "assert" )

        initialState = "Init"
        tr( "Init", '1' ) goto tr( +1, "Init", '1' )
        tr( "Init", '0' ) goto tr( +1, "Init", '0' )

        tr( "Init", '#' ) goto tr( -1, "assert", '#' )

        tr( "assert", ch ) goto tr(0, END_STATE, '#')
    }
}

fun test ( str: String ) = buildTuring {
        mainMachine{
            states( "Init", "toCall0", "toCall1" )

            initialState = "Init"
            tr("Init", '0') goto tr(+1, "toCall0", 'x')
            tr("Init", '1') goto tr(+1, "toCall1", 'x' )

            tr( "toCall0", '0' ) call "find0"
            tr( "toCall0", '1' ) call "find0"

            tr( "toCall1", '0' ) call "find1"
            tr( "toCall1", '1' ) call "find1"

            tr( "toCall0", '#' ) call "rewind"
            tr( "toCall1", '#' ) call "rewind"

            tr( "toCall0", 'x' ) goto tr( +1, "Init", '#' )
            tr( "toCall1", 'x' ) goto tr( +1, "Init", '#' )

            tr( "Init", '#' ) goto tr( 0, END_STATE, '#' )
        }

        makeFind('0')
        makeFind('1')

        machine("rewind") {
            states( "a" )
            initialState = "a"

            tr("a", '#' ) goto tr( -1, "a", '#' )

            tr("a", '0' ) goto tr( -1, "a", '0' )
            tr("a", '1' ) goto tr( -1, "a", '1' )

            tr("a", 'x') goto tr(0, END_STATE, 'x')
        }

        input {
            0 set str
        }

        printOutput {
            message( "String is symmetrical\n" )
        }

        printOnHalt {
            message( "String is not symmetrical\n" )
        }
    }?.start()

fun main() {
    test( "01")
    test( "0110")
    test( "11011")
}
