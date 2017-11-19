package de.roland_illig.loopplayer

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast

class LoopPlayerActivity : Activity(), CuePointListFragment.Callback {

    private val openRequestCode = 1
    private lateinit var audioFile: Uri
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var cuePointsFragment: CuePointListFragment
    private var pauseAt = Int.MAX_VALUE
    private var finished = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop_player)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onTimer()
    }

    override fun onDestroy() {
        finished = true
        super.onDestroy()
    }

    private fun onTimer() {
        val mediaPlayer = mediaPlayer
        if (mediaPlayer != null && mediaPlayer.isPlaying && mediaPlayer.currentPosition >= pauseAt) {
            mediaPlayer.pause()
        }
        if (!finished) {
            handler.postDelayed(this::onTimer, 50)
        }
    }

    override fun init(fragment: CuePointListFragment) {
        this.cuePointsFragment = fragment
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

    fun onSetCueClick(view: View) {
        val cuePoints = cuePointsFragment.listAdapter as ArrayAdapter<CuePoint>
        val start = if (cuePoints.isEmpty) 0 else cuePoints.getItem(cuePoints.count - 1).end
        val end = mediaPlayer!!.currentPosition
        cuePoints.add(CuePoint(start, end))
    }

    fun onPauseClick(view: View) {
        val mediaPlayer = mediaPlayer!!
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    override fun onCueClick(cuePoint: CuePoint) {
        val mediaPlayer = mediaPlayer!!
        mediaPlayer.seekTo(cuePoint.start)
        mediaPlayer.start()
        pauseAt = cuePoint.end
    }

    fun playAudio() {
        try {
            val assetFileDescriptor = contentResolver.openAssetFileDescriptor(audioFile, "r")
            val fileDescriptor = assetFileDescriptor.fileDescriptor

            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(fileDescriptor)
            mediaPlayer.prepare()
            mediaPlayer.start()
            this.mediaPlayer = mediaPlayer
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot play audio: " + e, Toast.LENGTH_LONG).show()
        }
    }
}
