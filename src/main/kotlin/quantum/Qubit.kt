package main.kotlin.quantum

import main.kotlin.maths.Complex
import main.kotlin.maths.Matrix

data class Qubit(var a:Complex, var b: Complex) {
    operator fun times(other: Matrix):Qubit
    {
        if(other.size!=2)
            throw Exception("Qubit: invalid matrix size");

        return Qubit(a*other[0,0]+b*other[0,1],a*other[1,0]+b*other[1,1]);
    }
}