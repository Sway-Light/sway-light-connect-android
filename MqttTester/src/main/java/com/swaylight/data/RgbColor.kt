package com.swaylight.data

import java.io.Serializable

class RgbColor : Serializable {
    var color: Int? = null

    constructor(color: Int) {
        this.color = color
    }

    override fun toString(): String {
        return """
            RgbColor:{color:{R:${color?.shr(16)?.and(0xff)}, G:${color?.shr(8)?.and(0xff)}, B:${color?.shr(0)?.and(0xff)}}}
            
            """.trimIndent()
    }
}