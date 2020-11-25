package com.swaylight.data

import android.graphics.Color
import java.io.Serializable

class GradientColor : Serializable{
    var startColor: Int? = null
    var endColor: Int? = null
    var centerColor: Int? = null

    constructor()

    constructor(startColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
    }

    constructor(startColor: Int, centerColor: Int?, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
        this.centerColor = centerColor
    }

    override fun toString(): String {
        return "GradColor:{\n" +
                    "startColor :{R:${startColor?.shr(16)?.and(0xff)}," +
                    "G:${startColor?.shr(8)?.and(0xff)}," +
                    "B:${startColor?.shr(0)?.and(0xff)}},\n" +
                    "centerColor:{R:${centerColor?.shr(16)?.and(0xff)}," +
                    "G:${centerColor?.shr(8)?.and(0xff)}," +
                    "B:${centerColor?.shr(0)?.and(0xff)}},\n" +
                    "endColor   :{R:${endColor?.shr(16)?.and(0xff)}," +
                    "G:${endColor?.shr(8)?.and(0xff)}," +
                    "B:${endColor?.shr(0)?.and(0xff)}}}\n"
    }
}