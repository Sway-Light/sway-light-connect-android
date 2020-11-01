package com.swaylight.data

import android.graphics.Color

class GradientColor {
    var startColor: Color = Color()
    var endColor: Color = Color()

    constructor()

    constructor(startColor: Color, endColor: Color) {
        this.startColor = startColor
        this.endColor = endColor
    }
}