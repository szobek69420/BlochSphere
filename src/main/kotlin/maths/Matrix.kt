package main.kotlin.maths

import main.kotlin.quantum.Qubit

data class Matrix(val size: Int) {
    private val data:Array<Complex>;//row-major

    init {
        data=Array<Complex>(size*size,{x->Complex(0.0,0.0)});
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

    operator fun times(other: Qubit): Qubit
    {
        if(size!=2)
            throw Exception("Matrix: invalid matrix size");

        return Qubit(other.a*data[0]+other.b*data[1],other.a*data[2]+other.b*data[3]);
    }
}