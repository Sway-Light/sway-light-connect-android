package com.swaylight


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.SeekBar
import com.swaylight.data.GradientColor
import com.swaylight.data.RgbColor

class Utils {
    companion object {
        fun setSeekBarColor(seekBar: SeekBar, gradientColor: GradientColor) {
            val colors = if(gradientColor.centerColor == null) {
                intArrayOf(gradientColor.startColor!!,
                        gradientColor.endColor!!)
            }else {
                intArrayOf(gradientColor.startColor!!,
                        gradientColor.centerColor!!,
                        gradientColor.endColor!!)
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

        fun setBgColor(view: View, gradientColor: GradientColor, orientation: GradientDrawable.Orientation) {
            val colors = if(gradientColor.centerColor == null) {
                intArrayOf(gradientColor.startColor!!,
                        gradientColor.endColor!!)
            }else {
                intArrayOf(gradientColor.startColor!!,
                        gradientColor.centerColor!!,
                        gradientColor.endColor!!)
            }
            val gradDrawable = GradientDrawable().apply {
                this.colors = colors
                this.orientation = orientation
            }
            view.background = gradDrawable
        }

        fun setBgColor(view: View, rgbColor: RgbColor, orientation: GradientDrawable.Orientation) {
            val colors = intArrayOf(rgbColor.color!!, Color.WHITE)

            val gradDrawable = GradientDrawable().apply {
                this.colors = colors
                this.orientation = orientation
            }
            view.background = gradDrawable
        }
    }
}
