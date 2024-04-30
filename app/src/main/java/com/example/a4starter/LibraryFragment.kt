package com.example.a4starter

import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class LibraryFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_library, container, false)


        val adapter = GestureArrayAdapter(
            this.requireContext(),
            mViewModel!!.strokeGestures
        )

        val listview: ListView = root.findViewById(R.id.my_list)


        listview.adapter = adapter // works or nah



        // val textView = root.findViewById<TextView>(R.id.text_library)

        // mViewModel!!.desc.observe(viewLifecycleOwner, { s:String -> textView.text = "$s - Library" })
        //mViewModel!!.strokeGestures.observe(viewLifecycleOwner, { s:ArrayList<Path> -> textView.text = "stroke count: ${s.size}"})

        return root
    }
}