package de.roland_illig.loopplayer

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast

class LoopPlayerActivity : Activity(), CuePointListFragment.Callback {

    private val openRequestCode = 1
    private lateinit var cuePointsFragment: CuePointListFragment
    private lateinit var handler: Handler
    private var mediaPlayer: MediaPlayer? = null

    private var pauseAt = Int.MAX_VALUE
    private var finished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop_player)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        handler = Handler()
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

    fun onOpenClick(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "audio/*"
        startActivityForResult(intent, openRequestCode)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == openRequestCode && resultCode == Activity.RESULT_OK) {
            playAudio(resultData!!.data)
        }
    }

    private fun playAudio(uri: Uri) {
        try {
            val assetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r")
            val fileDescriptor = assetFileDescriptor.fileDescriptor

            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(fileDescriptor)
            mediaPlayer.prepare()
            mediaPlayer.start()

            this.mediaPlayer?.release()
            this.mediaPlayer = mediaPlayer
            pauseAt = Int.MAX_VALUE
            cuePointsFragment.clear()
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot play audio: " + e, Toast.LENGTH_LONG).show()
        }
    }

    fun onSetCueClick(view: View) {
        val last = cuePointsFragment.getLast()
        val start = last?.end ?: 0
        val end = mediaPlayer!!.currentPosition
        cuePointsFragment.add(CuePoint(start, end))
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
}
