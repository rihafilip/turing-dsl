package cz.cvut.fit.kot.rihafili.turingDsl.type

import kotlin.collections.ArrayDeque

// Primary constructor is private, it is used only by clone
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

    fun move ( offset: Int ){
        check ( offset )
        index += offset
    }

    fun get( offset: Int = 0 ) : Char {
        check ( offset )
        return data[index + offset]
    }

    fun set( input: Char, offset: Int = 0 ) {
        check ( offset )
        if ( input != SYMBOL_CONST.KEEP )
            data[index + offset] = input
    }

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
        else if ( diff > data.size ){
            for ( i in 0 until diff )
               data.addLast( SYMBOL_CONST.BLANK )
        }
    }

    fun copy() : Tape = Tape( ArrayDeque(data), index )

    override fun toString(): String {
        var flag = false
        val builder = StringBuilder()

        for ( i in data.indices ) {
            if ( i == index || data[i] != SYMBOL_CONST.BLANK ){
                if ( !flag ){
                    flag = true
                    builder.append( '[' )
                        .append( i - index )
                        .append ( "] " )
                }

                if ( i == index )
                    builder.append('>')

                builder.append( data[i] )

                if ( i == index )
                    builder.append('<')

            } else if ( flag ){
                flag = false
                builder.append( '\n' )
            }
        }

        return builder.toString()
    }
}
