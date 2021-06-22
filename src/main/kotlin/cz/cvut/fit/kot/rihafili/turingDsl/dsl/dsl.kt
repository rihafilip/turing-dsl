package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.TuringCompilationError
import cz.cvut.fit.kot.rihafili.turingDsl.type.*

/**
 * Builder construct for DSL
 * All builders work only as containers and need to be then compiled with TuringCompiler
 * This is automatically done with 'buildTuring' wrapper
 */

// Intro function into TuringDSL
fun buildTuring ( block: TuringBuilder.() -> Unit ) : TuringMachineWrapper? {
    try {
        val builder = TuringBuilder().apply(block)
        return TuringCompiler.compile( builder )
    } catch (e: TuringCompilationError) {
        println("Compile error:\n\t[${e.name}]: ${e.message}")
        return null
    }
}


// Basic builder of all turing machines
class TuringBuilder {
    // Common tape used by all the machines
    internal val commonTape = Tape()
    internal val printOnEnd = mutableListOf<TuringMachineOutput>()
    internal val printOnHalt = mutableListOf<TuringMachineOutput>()
    internal val allMachines = mutableMapOf<String, MachineBuilder>()
    internal var mainMachine : MachineBuilder? = null

    // Construct the main Turing machine
    fun mainMachine ( block: MachineBuilder.() -> Unit) {
        mainMachine = MachineBuilder().apply( block )
    }

    // Construct additional Turing machine
    fun machine ( name: String, block: MachineBuilder.() -> Unit ) =
        allMachines.put( name, MachineBuilder().apply( block ) )

    fun input ( block: InputBuilder.() -> Unit ) = InputBuilder(commonTape).block()

    fun printOutput ( printBlank: Boolean = false, block: PrintBuilder.() -> Unit ) =
        PrintBuilder( printOnEnd, printBlank ).block()

    fun printOnHalt ( printBlank: Boolean = false, block: PrintBuilder.() -> Unit ) =
        PrintBuilder( printOnHalt, printBlank ).block()
}

// Container that collects information about one Turing machine
class MachineBuilder {
    internal val states = mutableSetOf<String>()
    internal val transitions = mutableListOf<Pair<TransitionStart, TransitionEnd>>()
    internal val calledMachines = mutableSetOf<String>()
    var initialState: String? = null

    // Adds states to turing machine
    fun states ( vararg args: String ) {
        states.addAll(args)
    }

    // Creates an input to transition function
    fun tr( state: String, symbol: Char ) = TransitionStart( state, symbol )

    // Creates an output to transition function
    fun tr( position: Int, state: String, symbol: Char ) = NextState( state, symbol, position )

    // Pairs a transition input to transition output
    infix fun TransitionStart.goto ( end: NextState ) = transitions.add( this to end )

    // Pairs a transition input to transition output with printing the current symbol
    infix fun TransitionStart.printGoto ( end: NextState ) = transitions.add( this to end.toPrintTransition() )

    // Calls another turing machine
    infix fun TransitionStart.call ( end: String ) {
        calledMachines.add( end )
        transitions.add( this to NextMachine( end ) )
    }
}

// Add initial state to tape used by Turing machines
class InputBuilder ( private val tape: Tape ){
    // Set a string starting on an offset of this from starting position
    infix fun Int.set ( other: String ) {
        var i = this
        for ( ch in other )
            tape.set(ch, i++)
    }
}

// Add a message to be printed at the end of Turing machine cycle
class PrintBuilder ( private val context: MutableList<TuringMachineOutput>, private val printBlank: Boolean ) {
    // Print a state of tape on offsets of this and in length of other
    infix fun Int.lenght ( other: Int ) = context.add( TapeOutput(this, other, printBlank ) )

    // Print a static message
    fun message ( message: String ) = context.add( StringOutput( message ) )
}
