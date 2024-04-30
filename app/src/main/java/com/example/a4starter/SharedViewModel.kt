package com.example.a4starter

import android.graphics.Bitmap
import android.graphics.Path
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.jvm.internal.Ref


class SharedViewModel : ViewModel() {
    val desc: MutableLiveData<String> = MutableLiveData()
    val strokeGestures: ArrayList<Gesture?> = ArrayList<Gesture?>()

    init {
        desc.value = "Shared model"

    }

    fun addStroke(name: String, path: ArrayList<Double_Point>, canvas: Bitmap) {
        strokeGestures.add(Gesture(name, path, canvas))
    }



    // ... more methods added here

    class Double_Point internal constructor(mx: Double, my:Double) {
        var x = 0.0
        var y = 0.0
        init {
            x = mx
            y = my
        }
    }


    fun distance(p1: Double_Point, p2: Double_Point): Double {
        var diff_x = Math.abs(p1.x - p2.x)
        var diff_y = Math.abs(p1.y - p2.y)
        var dis = Math.sqrt(Math.pow(diff_x, 2.0) + Math.pow(diff_y, 2.0))
        return dis
    }


//    fun resample(pts: ArrayList<Double_Point>): ArrayList<Double_Point> {
//        var target = 128
//        var size = pts.size
//        var res: ArrayList<Double_Point> = pts // work or nah?
//
//
//        while(size > target) {
//            var tmp: ArrayList<Double_Point> = ArrayList()
//            for(i in 0 until size) {
//                if(i % 2 == 0) {
//                    tmp.add(res[i])
//                }
//            }
//            res.clear()
//            res = tmp
//            size = res.size
//        }
//
//        while(size < target) {
//            var tmp: ArrayList<Double_Point> = ArrayList()
//            for(i in 0 until size) {
//
//                if(i < (size - 1)) { // assume size is odd
//                    tmp.add(res[i])
//                    var new_x = (res[i].x + res[i + 1].x) / 2
//                    var new_y = (res[i].y + res[i + 1].y) / 2
//                    var tmp_point = Double_Point(new_x, new_y)
//                    tmp.add(tmp_point)
//                } else {
//                    tmp.add(res[i])
//                }
//            }
//            res.clear()
//            res = tmp
//            size = res.size
//        }
//        return res
//    }

    fun resample(pts: ArrayList<Double_Point>): ArrayList<Double_Point> {
        var target = 128
        var total_distance = 0.0
        var size = pts.size
        var D = 0.0
        var res: ArrayList<Double_Point> = ArrayList()
        if(size == 0) {
            return res
        }
        for (i in 0 until size - 1) {
            total_distance += distance(pts[i], pts[i + 1])
        }
        var interval = total_distance / (target - 1)

        res.add(pts[0])
        var i = 1
        while (i < size) {
            var d = distance(pts[i - 1], pts[i])
            if(D + d > interval) {
                var qx = pts[i - 1].x + ((interval - D) / d) * (pts[i].x - pts[i - 1].x)
                var qy = pts[i - 1].y + ((interval - D) / d) * (pts[i].y - pts[i - 1].y)
                var q = Double_Point(qx, qy)
                res.add(q)
                pts[i - 1] = q
                D = 0.0

            } else {
                D += d
                i++
            }
        }

        return res
    }

    fun rotate(pts: ArrayList<Double_Point>): ArrayList<Double_Point> {
        var total_x = 0.0
        var total_y = 0.0
        var size = pts.size
        var res: ArrayList<Double_Point> = ArrayList()
        if(size == 0) {
            return res
        }
        for(i in 0 until size) {
            total_x += pts[i].x
            total_y += pts[i].y
        }
        var centroid_x = total_x / size
        var centroid_y = total_y / size
        var centroid = Double_Point(centroid_x, centroid_y)
        Log.d("centroidx is ", centroid_x.toString())
        Log.d("centroidy is ", centroid_y.toString())
        var r = distance(pts[0], centroid)

        var pivot_x = pts[0].x
        var pivot_y = pts[0].y

        var sin_degree = - Math.abs((centroid_y - pivot_y)) / r
        var cos_degree = Math.abs((centroid_x - pivot_x)) / r

        for(i in 0 until size) {
            var new_x = (pts[i].x - centroid_x) * cos_degree - (pts[i].y - centroid_y) * sin_degree + centroid_x
            var new_y = (pts[i].x - centroid_x) * sin_degree + (pts[i].y - centroid_y) * cos_degree + centroid_y
            var new_point = Double_Point(new_x, new_y)
            res.add(new_point)
        }

        return res

    }

    fun transition(pts: ArrayList<Double_Point>): ArrayList<Double_Point> {
        var total_x = 0.0
        var total_y = 0.0
        var size = pts.size
        var res: ArrayList<Double_Point> = ArrayList()
        if(size == 0) {
            return res
        }
        for(i in 0 until size) {
            total_x += pts[i].x
            total_y += pts[i].y
        }
        var centroid_x = total_x / size
        var centroid_y = total_y / size

        var diff_x = 0.0 - centroid_x
        var diff_y = 0.0 - centroid_y

        Log.d("centroidx is ", (centroid_x + diff_x).toString())
        Log.d("centroidy is ", (centroid_y + diff_y).toString())

        for(i in 0 until size) {
            var new_x = pts[i].x + diff_x
            var new_y = pts[i].y + diff_y
            var new_point = Double_Point(new_x, new_y)
            res.add(new_point)
        }

        return res

    }

    fun scale(pts: ArrayList<Double_Point>): ArrayList<Double_Point> {
        var total_x = 0.0
        var total_y = 0.0
        var size = pts.size
        var res: ArrayList<Double_Point> = ArrayList()
        if(size == 0) {
            return res
        }
        for(i in 0 until size) {
            total_x += pts[i].x
            total_y += pts[i].y
        }
        var centroid_x = total_x / size
        var centroid_y = total_y / size
        var centroid = Double_Point(centroid_x, centroid_y)

        var r = distance(pts[0], centroid)

        var coeff = 300 / r

        for(i in 0 until size) {
            var new_x = pts[i].x * coeff
            var new_y = pts[i].y * coeff
            var new_point = Double_Point(new_x, new_y)
            res.add(new_point)
        }

        return res
    }

    fun get_match(gesture: Gesture, pts: ArrayList<Double_Point>): Double {
        var evaluation = 0.0

        var size = Math.min(pts.size, gesture.path.size)

        for(i in 0 until size) {
            evaluation += distance(pts[i], gesture.path[i])
            Log.d("Gesture name is: ", gesture.name)
            Log.d("i is ", i.toString())
            Log.d("Gesture[i].x is: ", gesture.path[i].x.toString())
            Log.d("Gesture[i].y is: ", gesture.path[i].y.toString())
            Log.d("pts[i].x is: ", pts[i].x.toString())
            Log.d("pts[i].y is: ", pts[i].y.toString())
            Log.d("evaluation is: ", evaluation.toString())
        }
        return (evaluation / size)
    }


    fun get_top_three(recog_image: ArrayList<Double_Point>): ArrayList<Gesture?> {
        var gestures: ArrayList<Gesture?> = ArrayList()
        var scores: ArrayList<Double> = ArrayList()
        var origin: ArrayList<Double> = ArrayList()
        var size = strokeGestures.size

        for(i in 0 until size) {
            var tmp = get_match(strokeGestures[i]!!, recog_image)
            scores.add(tmp)
            origin.add(tmp)
        }
        if(size == 0) {
            return gestures
        } else if(size == 1) {
            gestures.add(strokeGestures[0])
        } else if(size == 2) {
            if(scores[1] < scores[0]) {
                gestures.add(strokeGestures[1])
                gestures.add(strokeGestures[0])
            } else {
                gestures.add(strokeGestures[0])
                gestures.add(strokeGestures[1])
            }
        } else {

            scores.sortDescending()
            var total = scores.size
            var first = scores[total - 1]
            var second = scores[total - 2]
            var third = scores[total - 3]
            for(i in 0 until size) { // find the first one
                if(origin[i] == first) {
                    gestures.add(strokeGestures[i])
                    Log.d("first place", "hello1")
                    Log.d("first which", strokeGestures[i]!!.name)
                    Log.d("first score", origin[i].toString())
                }
            }


            for(i in 0 until size) { // find the second one
                if(origin[i] == second) {
                    gestures.add(strokeGestures[i])
                    Log.d("second place", "hello2")
                    Log.d("second which", strokeGestures[i]!!.name)
                    Log.d("second score", origin[i].toString())
                }
            }


            for(i in 0 until size) { // find the third one
                if(origin[i] == third) {
                    gestures.add(strokeGestures[i])
                    Log.d("third place", "hello3")
                    Log.d("third which", strokeGestures[i]!!.name)
                    Log.d("third score", origin[i].toString())
                }
            }
        }

        return gestures

    }

}


