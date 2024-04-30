package com.example.a4starter

import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView


// refrence from sample code 09.Android/8.ListView

class GestureArrayAdapter(context: Context?, gestures: ArrayList<Gesture?>?) :
    ArrayAdapter<Gesture?>(context!!, 0, gestures!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        var view: View? = convertView
        val ges = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.library_gesture, parent, false)
        }

        // Lookup view for data population
        val name = view?.findViewById(R.id.gesture_name) as TextView

        val image = view?.findViewById(R.id.gesture_canvas) as ImageView

        val del = view?.findViewById(R.id.gesture_delete) as Button

        // Populate the data into the template view using the data object
        name.text = ges!!.name

        image.setImageBitmap(ges!!.bmap)

        del.setOnClickListener{ v ->
            this.remove(ges)
        }



        // Return the completed view to render on screen
        return view
    }
}