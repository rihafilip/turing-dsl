package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.type.*

fun buildTuring ( block: TuringBuilder.() -> Unit ) : TuringMachineWrapper {
    val builder = TuringBuilder()
    builder.block()

}

class TuringBuilder {
    // Common tape used by all the machines
    val commonTape = Tape()

    fun mainMachine ( block: MachineBuilder.() -> Unit) {
        TODO()
    }

    fun machine ( name: String, block: MachineBuilder.() -> Unit ) {
        TODO()
    }

    fun input ( block: InputBuilder.() -> Unit ) {
        TODO()
    }

    fun printOutput ( showBlank: Boolean = false, block: PrintBuilder.() -> Unit ){

    }

    fun printOnHalt ( showBlank: Boolean = false, block: PrintBuilder.() -> Unit ){

    }
}

// Container that collects information about turing machine
class MachineBuilder ( val name: String, tape: Tape ) {
    val states = mutableSetOf<String>()
    val transitions = mutableListOf<Pair<TransitionStart, TransitionEnd>>()
    val calledMachines = mutableSetOf<String>()
    var initialState: String? = null

    fun states ( vararg args: String ) {
        states.addAll(args)
    }

    fun tr( state: String, symbol: Char ) = TransitionStart( state, symbol )

    fun tr( position: Int, state: String, symbol: Char ) = NextState( state, symbol, position )

    infix fun TransitionStart.goto ( end: NextState ) = transitions.add( this to end )

    infix fun TransitionStart.printGoto ( end: NextState ) = transitions.add( this to PrintTransition( end.state, end.symbol, end.pos )  )

    infix fun TransitionStart.call ( end: String ) {
        calledMachines.add( end )
        transitions.add( this to NextMachine( end ) )
    }
}

class InputBuilder {
    infix fun Int.set ( other: String ){
        TODO()
    }
}

class PrintBuilder {
    val outputs = mutableListOf<Output>()

    infix fun Int.lenght ( other: Int ) = outputs.add( TapeOutput(this, other ) )

    fun message ( message: String ) = outputs.add( StringOutput( message ) )
}
