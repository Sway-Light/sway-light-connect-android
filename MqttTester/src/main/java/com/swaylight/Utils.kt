package com.swaylight


import android.graphics.drawable.GradientDrawable
import android.widget.SeekBar
import com.swaylight.data.GradientColor

class Utils {
    companion object {
        fun setSeekBarColor(seekBar: SeekBar, g: GradientColor) {
            var colors: IntArray? = null
            colors = if(g.centerColor == null) {
                intArrayOf(g.startColor!!, g.endColor!!)
            }else {
                intArrayOf(g.startColor!!, g.centerColor!!, g.endColor!!)
            }
            val gradDrawable = seekBar.progressDrawable as GradientDrawable
            gradDrawable.colors = colors
            gradDrawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        }
    }
}
