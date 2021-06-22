package cz.cvut.fit.kot.rihafili.turingDsl.type

import kotlin.collections.ArrayDeque

/**
 * Simulation opf endless tape
 * Primary constructor is private, it is used only by clone
 */
class Tape private constructor(
    private val data: ArrayDeque<Char>,
    private var index: Int
){

    // Public constructor of blank tape with stated capacity
    constructor(initialCapacity: Int = 20) : this(
        ArrayDeque(
            SYMBOL_CONST.BLANK.toString().repeat(initialCapacity).toList()
        ),
        initialCapacity / 2
    )

    // Move around head of the tape
    fun move ( offset: Int ){
        check ( offset )
        index += offset
    }

    // Get character around current head
    fun get( offset: Int = 0 ) : Char {
        check ( offset )
        return data[index + offset]
    }

    // Set a character around the tape
    fun set( input: Char, offset: Int = 0 ) {
        check ( offset )
        if ( input != SYMBOL_CONST.KEEP )
            data[index + offset] = input
    }

    // Checks if offset is within current size of tape and resizes it if it is not
    private fun check ( offset: Int ) {
        val diff = index + offset
        // prepend
        // diff is negative, hence the minus operations
        if ( diff < 0 ) {
            for ( i in 0 until -diff )
                data.addFirst( SYMBOL_CONST.BLANK )
            index -= diff // offset index from beginning
        }
        // append
        else if ( diff >= data.size ){
            for ( i in 0 until diff )
               data.addLast( SYMBOL_CONST.BLANK )
        }
    }

    // Creates a deep copy of this tape
    fun copy() : Tape = Tape( ArrayDeque(data), index )

    override fun toString() = buildString {
        var flag = false

        for ( i in data.indices ) {
            val current = i == index
            if ( current || data[i] != SYMBOL_CONST.BLANK ) {
                if ( !flag ){
                    flag = true
                    append( '[' )
                    append( i - index )
                    append ( "] " )
                }

                if ( current )
                    append('>')

                append( data[i] )

                if ( current )
                    append('<')

            } else if ( flag ){
                flag = false
                append( '\n' )
            }
        }
    }
}
