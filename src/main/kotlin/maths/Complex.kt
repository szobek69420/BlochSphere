package main.kotlin.maths

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

data class Complex(var rl: Double, var img: Double)
{
    constructor(rl: Double) : this(rl,0.0);

    fun length():Double
    {
        var temp=(this*this.adjugate()).rl;
        if(temp<0.000001)
            return 0.0;
        return temp.pow(0.5);
    }

    fun phase(): Double
    {
        var temp=0.0;
        if(rl>0)
        {
            temp=atan2(img,rl);
        }
        else
        {
            temp=atan2(-img,-rl)+PI;
            if(temp>PI)
                temp-=2.0*PI;
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

    operator fun timesAssign(other:Double)
    {
        rl*=other;
        img*=other;
    }


    operator fun divAssign(other: Complex)
    {
        rl=(rl*other.rl+img*other.img)/(other.rl*other.rl+other.img*other.img);
        img=(img*other.rl-rl*other.img)/(other.rl*other.rl+other.img*other.img);
    }

    operator fun divAssign(other:Double)
    {
        rl/=other;
        img/=other;
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