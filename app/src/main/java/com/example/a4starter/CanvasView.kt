package com.example.a4starter;


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class CanvasView: ImageView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    var is_drawn = false
    var path: Path? = null
    var pts: ArrayList<SharedViewModel.Double_Point> = ArrayList()
    var paths: ArrayList<Path?> = ArrayList()
    var paintbrush = Paint(Color.BLUE)
    var background: Bitmap? = null

    // we save a lot of points because they need to be processed
    // during touch events e.g. ACTION_MOVE
    var x1 = 0f
    var y1 = 0f
    var p1_id = 0
    var p1_index = 0



    // store cumulative transformations
    // the inverse matrix is used to align points with the transformations - see below
    var currentMatrix = Matrix()
    var inverse = Matrix()

    init {
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeWidth = 5f
    }

    override fun setOnTouchListener(l: OnTouchListener) {
        super.setOnTouchListener(l)
    }

    // capture touch events (down/move/up) to create a path/stroke that we draw later
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var inverted = floatArrayOf()

        when (event.pointerCount) {
            1 -> {
                p1_id = event.getPointerId(0)
                p1_index = event.findPointerIndex(p1_id)

                // invert using the current matrix to account for pan/scale
                // inverts in-place and returns boolean
                inverse = Matrix()
                currentMatrix.invert(inverse)

                // mapPoints returns values in-place
                inverted = floatArrayOf(event.getX(p1_index), event.getY(p1_index))
                inverse.mapPoints(inverted)
                x1 = inverted[0]
                y1 = inverted[1]
                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        Log.d("actiondown", "Action Down")
                        path = Path()
                        if(!is_drawn) {

                            paths.add(path)
                        }
                        is_drawn = true

                        path!!.moveTo(x1, y1)

                    }
                    MotionEvent.ACTION_MOVE -> {
                        Log.d("actionmove", "Action move")


                        var double_x1: Double = x1.toDouble()
                        var double_y1: Double = y1.toDouble()
                        var p: SharedViewModel.Double_Point = SharedViewModel.Double_Point(double_x1, double_y1)
                        pts.add(p)
                        Log.d("during drawing", pts.size.toString())
                        path!!.lineTo(x1, y1)





                    }
                    // MotionEvent.ACTION_UP -> Log.d(LOGNAME, "Action up")
                }
            }
        }
        return true
    }

    // set image as background
    fun setImage(bitmap: Bitmap?) {
        background = bitmap
    }

    fun clear() {
        paths.clear()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // apply transformations from the event handler above
        canvas.concat(currentMatrix)

        // draw background
        if (background != null) {
            setImageBitmap(background)
        }

        // draw lines over it
        for (path in paths) {
            canvas.drawPath(path!!, paintbrush)
        }
    }


}