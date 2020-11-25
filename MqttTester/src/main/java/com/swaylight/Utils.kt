package com.swaylight


import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.swaylight.data.FilePath
import com.swaylight.data.GradientColor
import com.swaylight.data.RgbColor
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext

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
        
        @SuppressLint("SetTextI18n")
        fun setTextInPercentage(tv: TextView, value: Int, maxValue: Int) {
            tv.text = value.times(100).div(maxValue).toString() + "%"
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
                endColor.shr(8).and(0xFF).minus(deltaG.times(1f - weight).toInt())
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

        fun writeToFile(root: String, path: FilePath, arrayList: ArrayList<*>) {
            try {
                val writeStream: FileOutputStream = FileOutputStream(root + "/" + path.file)
                val oos = ObjectOutputStream(writeStream)
                for (detail in arrayList) {
                    Log.d("Utils", "write:\n" + path.file + detail.toString())
                    oos.writeObject(detail)
                }
                writeStream.flush()
                writeStream.close()
            } catch (e: IOException) {
                Log.e("Exception", "File write failed: $e")
            }
        }

        fun readFromFile(root: String, path: FilePath): ArrayList<*>? {
            val rtn: ArrayList<Any> = ArrayList()
            try {
                val inputStream: FileInputStream = FileInputStream(root + "/" + path.file)
                val ois = ObjectInputStream(inputStream)
                var hasNext = true
                while (hasNext) {
                    val temp = ois.readObject()
                    if (temp == null) {
                        hasNext = false
                    } else {
                        rtn.add(temp)
                    }
                }
                inputStream.close()
            } catch (e: FileNotFoundException) {
                Log.e("login activity", "File not found: $e")
            } catch (e: IOException) {
                Log.e("login activity", "Can not read file: $e")
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            Log.d("Utils", "readFromFile:${path.file}\n$rtn".trimIndent())
            return rtn
        }
    }
}
