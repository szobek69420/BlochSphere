package main.kotlin.maths

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

data class Complex(var rl: Float, var img: Float)
{
    constructor(rl: Float) : this(rl,0.0f);

    fun length():Float
    {
        var temp: Float=(this*this.adjugate()).rl;
        if(temp<0.00001f)
            return 0.0f;
        return temp.pow(0.5f);
    }

    fun phase(): Float
    {
        var temp:Float=atan2(img,rl);
        if(rl<0)
        {
            if(img<0)
                temp+= PI.toFloat();
            else
                temp-=PI.toFloat();
        }
        return temp;
    }

    fun adjugate():Complex
    {
        return Complex(rl,-img);
    }

    operator fun plusAssign(other: Complex)
    {
        rl+=other.rl;
        img+=other.img;
    }

    operator fun minusAssign(other: Complex)
    {
        rl-=other.rl;
        img-=other.img;
    }

    operator fun timesAssign(other: Complex)
    {
        rl=rl*other.rl-img*other.img;
        img=rl*other.img+img*other.rl;
    }

    operator fun timesAssign(other: Float)
    {
        rl*=other;
        img*=other;
    }


    operator fun divAssign(other: Complex)
    {
        rl=(rl*other.rl+img*other.img)/(other.rl*other.rl+other.img*other.img);
        img=(img*other.rl-rl*other.img)/(other.rl*other.rl+other.img*other.img);
    }

    operator fun plus(other: Complex):Complex
    {
        return Complex(rl+other.rl,img+other.img);
    }

    operator fun minus(other: Complex) : Complex
    {
        return Complex(rl-other.rl,img-other.img);
    }

    operator fun times(other: Complex):Complex
    {
        return Complex(rl*other.rl-img*other.img,rl*other.img+img*other.rl);
    }

    operator fun times(value: Float):Complex{
        return Complex(rl*value,img*value);
    }

    operator fun div(other: Complex):Complex
    {
        return Complex((rl*other.rl+img*other.img)/(other.rl*other.rl+other.img*other.img),(img*other.rl-rl*other.img)/(other.rl*other.rl+other.img*other.img));
    }
}