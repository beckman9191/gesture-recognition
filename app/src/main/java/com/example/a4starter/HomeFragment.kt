package com.example.a4starter

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class HomeFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle? ): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val canvasView: CanvasView = root.findViewById(R.id.recognition_canvas)

        canvasView.maxHeight = 400
        canvasView.maxWidth = 300
        val pic = BitmapFactory.decodeResource(resources, R.drawable.bg)

        canvasView.setImage(pic)

        val buttonSearch: Button = root.findViewById(R.id.recognition_SearchButton)

        val buttonClear: Button = root.findViewById(R.id.recognition_clearButton)


        buttonSearch.setOnClickListener { v ->
            val path = canvasView.pts!!


            var resample_path = mViewModel!!.resample(path)



//            Log.d("after search", resample_path.size.toString())
//            Log.d("pts0x", resample_path[0].x.toString())
//            Log.d("pts0y", resample_path[0].y.toString())

//            for (i in 0 until resample_path.size) {
//
//                Log.d("this is ith iteration: ", i.toString())
//                Log.d("x is : ", resample_path[i].x.toString())
//                Log.d("x is : ", resample_path[i].y.toString())
//            }


//            Log.d("total distance", total.toString())


//            for (i in 1 until resample_path.size) {
//
//                Log.d("each distance is", mViewModel!!.distance(resample_path[i - 1], resample_path[i]).toString())
//            }

            var rotate_path = mViewModel!!.rotate(resample_path)

//            Log.d("pts0x after rotation", rotate_path[0].x.toString())
//            Log.d("pts0y after rotation", rotate_path[0].y.toString())

            var transition_path = mViewModel!!.transition(rotate_path)
            var scale_path = mViewModel!!.scale(transition_path)

            // var matching_result = mViewModel!!.get_top_three(scale_path)
            var matching_result = mViewModel!!.get_top_three(scale_path)


            val adapter = GestureArrayAdapter(
                this.requireContext(),
                matching_result
            )
            val lsView: ListView = root.findViewById(R.id.matching_result) as ListView
            lsView.adapter = adapter


        }

        buttonClear.setOnClickListener { v ->
            canvasView.is_drawn = false
            canvasView.paths.clear()
            canvasView.pts.clear()
            canvasView.clear()

        }




//        mViewModel!!.desc.observe(viewLifecycleOwner, { s:String -> textView.text = "$s - Recognition" })
//        mViewModel!!.strokeGestures.observe(viewLifecycleOwner, { s:ArrayList<Path> -> textView.text = "stroke count: ${s.size}"})

        return root
    }
}