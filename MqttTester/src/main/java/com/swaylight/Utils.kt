package com.swaylight


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.SeekBar
import androidx.core.graphics.red
import androidx.core.graphics.toColor
import com.swaylight.data.GradientColor
import com.swaylight.data.RgbColor
import kotlin.math.roundToInt

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

        fun setSeekBarColor(sbRed: SeekBar, sbGreen: SeekBar, sbBlue: SeekBar, rgbColor: RgbColor) {
            sbRed.progress = rgbColor.color!!.shr(16).and(0xFF)
            sbGreen.progress = rgbColor.color!!.shr(8).and(0xFF)
            sbBlue.progress = rgbColor.color!!.shr(0).and(0xFF)
        }
    }
}
