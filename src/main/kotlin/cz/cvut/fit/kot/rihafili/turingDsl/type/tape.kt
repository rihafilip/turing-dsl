package cz.cvut.fit.kot.rihafili.turingDsl.type

class Tape (initialCapacity: Int = 20){
    private val data = ArrayDeque<Char>(
        BLANK_SYMBOL.toString().repeat( initialCapacity ).toList()
    )

    private var index : Int = initialCapacity / 2;

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
        if ( input != KEEP_SYMBOL )
            data[index + offset] = input
    }

    private fun check ( offset: Int ) {
        val diff = index + offset
        // prepend
        // diff is negative, hence the minus operations
        if ( diff < 0 ) {
            for ( i in 0 until -diff )
                data.addFirst( BLANK_SYMBOL )
            index -= diff // offset index from begining
        }
        // append
        else if ( diff > data.size ){
            for ( i in 0 until diff )
               data.addLast( BLANK_SYMBOL )
        }

    }
}
