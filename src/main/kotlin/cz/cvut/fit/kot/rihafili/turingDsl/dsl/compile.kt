package cz.cvut.fit.kot.rihafili.turingDsl.dsl

import cz.cvut.fit.kot.rihafili.turingDsl.exceptions.TuringCompilationError
import cz.cvut.fit.kot.rihafili.turingDsl.type.*

// Extended machine with its builder
typealias ExtMachine = Pair<TuringMachine, MachineBuilder>

class TuringCompiler( private val builder: TuringBuilder ){
    // Map of all machines, null is placeholder
    private val machinesMap = mutableMapOf<String, ExtMachine?>()

    // Compiles builder to TuringMachineBuilder
    fun compile() : TuringMachineWrapper {
        val mainMachine : ExtMachine = createMachine(
            "Main",
            builder.mainMachine
                ?: throw TuringCompilationError("Main", "Missing main machine")
        )

        for ( (name, machineBuilder) in builder.allMachines ){
            machinesMap[name] = createMachine( name, machineBuilder )
        }

        val nonNullMap = assertMap()

        finalize( "Main", mainMachine, nonNullMap )
        for ( (name, machineBuilder) in nonNullMap ) {
            finalize(name, machineBuilder, nonNullMap)
        }

        return TuringMachineWrapper(
            mainMachine.first,
            nonNullMap.mapValues { (_, data) -> data.first },
            printOnEnd = builder.printOnEnd,
            printOnHalt = builder.printOnHalt
        )
    }

    private fun createMachine( name: String, machineBuilder: MachineBuilder ) : ExtMachine {
        for ( calledName in machineBuilder.calledMachines ) {
            // place dummy null in map to signalize that this machines implementation is expected
            if (calledName !in machinesMap)
                machinesMap[calledName] = null
        }

        val initial = machineBuilder.initialState
            ?: throw TuringCompilationError(name, "Machine misses initial state")

        return TuringMachine(
            builder.commonTape,
            initial,
            machineBuilder.states
        ) to machineBuilder
    }

    private fun finalize ( name: String, extMachine: ExtMachine, map: Map<String, ExtMachine>) {
        val ( machine, machineBuilder ) = extMachine

        // Add implementation of machines
        for ( called in machineBuilder.calledMachines ) {
            machine.addMachine(
                called,
                map[called]?.first
                    ?: throw TuringCompilationError( name, "Called machine $called not registered in context")
            )
        }

        val states = machineBuilder.states

        // Add transitions to machine
        for ( (start, end) in machineBuilder.transitions ) {
            // Helper inline function for throwing the same error message
            fun throwState ( state: String ) : Nothing =
                throw TuringCompilationError( name,"State $state used, but not defined" )

            if ( start.state !in states )
                throwState(start.state)

            when(end){
                Halt -> throw TuringCompilationError( name, "Explicit Halt as Transition end is not allowed" )
                is NextState -> if ( end.state !in states ) throwState(end.state)
                is PrintTransition -> if ( end.state !in states ) throwState(end.state)
                is NextMachine -> Unit
            }

            if ( !machine.addTransition( start, end ) )
                throw TuringCompilationError( name, "Unknown error when adding '$start -> $end' transition" )
        }

    }

    // ensures all called machines will have implementation
    private fun assertMap() : Map<String, ExtMachine> =
        machinesMap.mapValues { (name, machine) ->
            machine ?: throw TuringCompilationError( name, "Machine is called, but not implemented" )
        }
}
