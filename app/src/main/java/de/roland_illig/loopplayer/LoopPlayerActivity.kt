package de.roland_illig.loopplayer

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

class LoopPlayerActivity : Activity() {

    private val openRequestCode = 1
    private val cuePoints = ArrayList<String>()
    private lateinit var audioFile: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop_player)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()
        cuePoints().adapter = ArrayAdapter<String>(this, R.layout.string_list_item, cuePoints)
    }

    fun onOpenPressed(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "audio/*"
        startActivityForResult(intent, openRequestCode)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int,
                                         resultData: Intent?) {

        if (requestCode == openRequestCode && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                audioFile = resultData.data
                playAudio()
            }
        }
    }

    fun onCuePointPressed(view: View) {
        (cuePoints().adapter as ArrayAdapter<String>).add(Math.random().toString())
    }

    fun playAudio() {
        try {
            val assetFileDescriptor = contentResolver.openAssetFileDescriptor(audioFile, "r")
            val fileDescriptor = assetFileDescriptor.fileDescriptor

            val mp = MediaPlayer()
            mp.setDataSource(fileDescriptor)
            mp.prepare()
            mp.start()
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot play audio: " + e, Toast.LENGTH_LONG).show()
        }
    }

    private fun cuePoints() = findViewById<ListView>(R.id.cuePointsList)
}
