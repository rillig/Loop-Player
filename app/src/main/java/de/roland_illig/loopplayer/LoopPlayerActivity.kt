package de.roland_illig.loopplayer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView


class LoopPlayerActivity : Activity() {

    val cuePoints = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop_player)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()
        cuePoints().adapter = ArrayAdapter<String>(this, R.layout.string_list_item, cuePoints)
    }

    fun onCuePointPressed(view: View) {
        (cuePoints().adapter as ArrayAdapter<String>).add(Math.random().toString())
    }

    fun cuePoints() = findViewById<ListView>(R.id.cuePointsList)
}
