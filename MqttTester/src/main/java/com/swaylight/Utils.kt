package com.swaylight


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.swaylight.data.GradientColor
import com.swaylight.data.RgbColor
import kotlin.math.absoluteValue

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
//            (seekBar.progressDrawable as GradientDrawable).apply {
//                this.colors = colors
//                this.orientation = GradientDrawable.Orientation.LEFT_RIGHT
//            }
            val gradDrawable = GradientDrawable().apply {
                this.colors = colors
                this.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            }
            seekBar.progressDrawable = gradDrawable
            seekBar.progressDrawable.invalidateSelf()
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

        fun getColorFromRgbSeekBar(sbRed: SeekBar, sbGreen: SeekBar, sbBlue: SeekBar): Int {
            return sbRed.progress.shl(16)
                    .plus(sbGreen.progress.shl(8))
                    .plus(sbBlue.progress.shl(0))
                    .plus(255.shl(24))
        }

        fun getColorFromGradient(startColor: Int, endColor: Int, progress: Int, maxProgress: Int): Int {
            val weight = progress.div(maxProgress.toFloat())
            val deltaR = endColor.shr(16).and(0xFF).minus(startColor.shr(16).and(0xFF))
            val deltaG = endColor.shr(8).and(0xFF).minus(startColor.shr(8).and(0xFF))
            val deltaB = endColor.shr(0).and(0xFF).minus(startColor.shr(0).and(0xFF))
            val red = if(deltaR > 0) {
                startColor.shr(16).and(0xFF).plus(deltaR.times(weight).toInt())
            }else {
                endColor.shr(16).and(0xFF).minus(deltaR.times(1f - weight).toInt())
            }
            val green = if(deltaG > 0){
                startColor.shr(8).and(0xFF).plus(deltaG.times(weight).toInt())
            }else {
                endColor.shr(8).and(0xFF).minus(deltaG.times(1f- weight).toInt())
            }

            val blue = if(deltaB > 0) {
                startColor.shr(0).and(0xFF).plus(deltaB.times(weight).toInt())
            }else {
                endColor.shr(0).and(0xFF).minus(deltaB.times(1f - weight).toInt())
            }
            return 0xFF000000
                    .plus(red.shl(16))
                    .plus(green.shl(8))
                    .plus(blue.shl(0)).toInt()
        }
    }
}
