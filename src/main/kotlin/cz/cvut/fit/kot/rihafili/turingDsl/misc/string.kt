package cz.cvut.fit.kot.rihafili.turingDsl.misc

// Package with string helpers
fun <T> Iterable<T>.joinToSet () = this.joinToString( separator = ", ", prefix = "{ ", postfix = " }" )
fun String.offset() : String = this.lines().joinToString ( separator = "\n\t", prefix = "\t", postfix = "" )

