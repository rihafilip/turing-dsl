package cz.cvut.fit.kot.rihafili.turingDsl.exceptions

class InvalidTransitionEnd( message: String ) : Exception( message )

class TuringCompilationError( val name : String, override val message: String ) : Exception()
