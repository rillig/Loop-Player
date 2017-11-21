package de.roland_illig.loopplayer

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast

class LoopPlayerActivity : LifecycleLoggingActivity(), CuePointListFragment.Callback {

    private val openRequestCode = 1
    private var cuePointsFragment: CuePointListFragment? = null
    private var handler: Handler? = null
    private var mediaPlayer: MediaPlayer? = null

    private var pauseAt = Int.MAX_VALUE
    private var justOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop_player)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()

        handler = Handler()
        mediaPlayer = MediaPlayer()
        onTimer()

        withAppState(this) {
            if (it.uri != "") {
                loadAudio(Uri.parse(it.uri))
                if (justOpened) {
                    justOpened = false
                    it.cuePoints.clear()
                    cuePointsFragment!!.replaceAll(it.cuePoints)
                    mediaPlayer!!.start()
                } else {
                    cuePointsFragment?.replaceAll(it.cuePoints)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        mediaPlayer?.release()
        mediaPlayer = null
        handler = null
    }

    private fun onTimer() {
        val mediaPlayer = mediaPlayer ?: return
        if (mediaPlayer.isPlaying && mediaPlayer.currentPosition >= pauseAt) {
            mediaPlayer.pause()
        }
        handler?.postDelayed(this::onTimer, 50)
    }

    override fun init(fragment: CuePointListFragment) {
        this.cuePointsFragment = fragment
        withAppState(this) {
            fragment.replaceAll(it.cuePoints)
        }
    }

    fun onOpenClick(view: View) {
        log("onOpenClick")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "audio/*"
        startActivityForResult(intent, openRequestCode)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        log("onActivityResult")
        if (requestCode == openRequestCode && resultCode == Activity.RESULT_OK) {
            withAppState(this) {
                it.uri = resultData!!.data.toString()
            }
            justOpened = true
        }
    }

    private fun loadAudio(uri: Uri) {
        log("loadAudio " + uri)

        val mediaPlayer = mediaPlayer!!
        try {
            mediaPlayer.setDataSource(this, uri)
            mediaPlayer.prepare()
            pauseAt = Int.MAX_VALUE
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot load audio: " + e, Toast.LENGTH_LONG).show()
            Log.e(javaClass.simpleName, "Cannot play audio", e)
        }
    }

    fun onSetCueClick(view: View) {
        val cuePointsFragment = cuePointsFragment!!
        val last = cuePointsFragment.getLast()
        val start = last?.end ?: 0
        val end = mediaPlayer!!.currentPosition
        val cuePoint = CuePoint(start, end)
        withAppState(this, {
            it.cuePoints.add(cuePoint)
        })
        cuePointsFragment.add(cuePoint)
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
