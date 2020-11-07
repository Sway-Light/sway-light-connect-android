package com.swaylight.data

import android.graphics.Color

class GradientColor {
    var startColor: Int? = null
    var endColor: Int? = null
    var centerColor: Int? = null

    constructor()

    constructor(startColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
    }

    constructor(startColor: Int, centerColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
        this.centerColor = centerColor
    }
}