package main.kotlin.maths

data class Matrix(val size: Int) {
    private val data:Array<Complex>;

    init {
        data=Array<Complex>(size*size,{x->Complex(0.0f,0.0f)});
    }

    operator fun get(i:Int,j:Int) : Complex
    {
        if(i>=size||j>=size||i<0||j<0)
            throw Exception("Matrix: invalid indices used");
        return data[i*size+j];
    }

    operator fun set(i:Int,j:Int, value:Complex)
    {
        if(i>=size||j>=size||i<0||j<0)
            throw Exception("Matrix: invalid indices used");
        data[i*size+j]=value.copy();
    }
}