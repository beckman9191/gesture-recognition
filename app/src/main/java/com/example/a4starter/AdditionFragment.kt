package com.example.a4starter

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Path

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class AdditionFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        val root: View = inflater.inflate(R.layout.fragment_addition, container, false)

        val canvasView: CanvasView = root.findViewById(R.id.canvas)

        canvasView.maxHeight = 400
        canvasView.maxWidth = 300
        val pic = BitmapFactory.decodeResource(resources, R.drawable.bg)

        canvasView.setImage(pic)


        val buttonOK: Button = root.findViewById(R.id.okButton)

        val buttonClear: Button = root.findViewById(R.id.clearButton)

        buttonOK.setOnClickListener { v ->

            val builder: AlertDialog.Builder = AlertDialog.Builder(canvasView.context)
            builder.setTitle("Gesture Name")
            val input = EditText(canvasView.context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            Log.d("myTag", "This is my message")
            builder.setPositiveButton("OK") { dialog, which ->
                val path = canvasView.pts!!
                val bmap = canvasView.drawToBitmap()
                val name = input.text.toString()

                var duplicate = false
                for(ges in mViewModel!!.strokeGestures) {
                    if(ges?.name == name) {
                        var resample_path = mViewModel!!.resample(path)

                        var rotate_path = mViewModel!!.rotate(resample_path)
                        var transition_path = mViewModel!!.transition(rotate_path)
                        var scale_path = mViewModel!!.scale(transition_path)

                        var final = scale_path
                        // var final = path
                        ges?.path = final
                        ges?.bmap = bmap
                        duplicate = true
                    }
                }

                if(!duplicate) {
                    var resample_path = mViewModel!!.resample(path)

                    Log.d("ar", resample_path.size.toString())

                    var rotate_path = mViewModel!!.rotate(resample_path)
                    var transition_path = mViewModel!!.transition(rotate_path)
                    var scale_path = mViewModel!!.scale(transition_path)

                    var final = scale_path
                    // var final = path

                    var Newges = Gesture(name, final, bmap)
                    mViewModel!!.strokeGestures.add(Newges)

                }

                canvasView.is_drawn = false
                canvasView.paths.clear()
                canvasView.pts.clear()
                canvasView.clear()

            }

            builder.setNegativeButton("Cancel") { dialog, which ->


            }



            builder.show()

        }

        buttonClear.setOnClickListener { v ->
            canvasView.is_drawn = false
            canvasView.paths.clear()
            canvasView.pts.clear()
            canvasView.clear()

        }










//        mViewModel!!.desc.observe(viewLifecycleOwner, { s:String -> textView.text = "$s - Addition" })
//        mViewModel!!.strokeGestures.observe(viewLifecycleOwner, { s:ArrayList<Path> -> textView.text = "stroke count: ${s.size}"})

        return root
    }
}
