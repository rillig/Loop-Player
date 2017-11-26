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

class LoopPlayerActivity : LifecycleLoggingActivity(), SectionListFragment.Callback {

    private val openRequestCode = 1
    private var sectionsFragment: SectionListFragment? = null
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

        Persistence.withFileState(this) { appState, fileState ->
            if (appState.uri != "") {
                loadAudio(Uri.parse(appState.uri))
                if (justOpened) {
                    justOpened = false
                    sectionsFragment!!.replaceAll(fileState.sections)
                    if (fileState.sections.isEmpty()) {
                        mediaPlayer!!.start()
                    }
                } else {
                    sectionsFragment?.replaceAll(fileState.sections)
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
        handler?.postDelayed(this::onTimer, 20)
    }

    override fun init(fragment: SectionListFragment) {
        this.sectionsFragment = fragment
        Persistence.withFileState(this) { _, fileState ->
            fragment.replaceAll(fileState.sections)
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
            Persistence.withAppState(this) {
                val uri = resultData!!.data
                it.uri = uri.toString()
                it.fileName = sha256Hex(this, uri)
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

    fun onMarkClick(view: View) {
        val sectionsFragment = sectionsFragment!!
        val mediaPlayer = mediaPlayer!!

        val last = sectionsFragment.getLast()
        if (last == null && !mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
            return
        }

        val start = last?.end ?: 0
        val end = mediaPlayer.currentPosition
        if (start == end) {
            return
        }

        val section = Section(start, end)
        Persistence.withFileState(this, { _, fileState ->
            fileState.sections.add(section)
        })
        sectionsFragment.add(section)
    }

    fun onPauseClick(view: View) {
        val mediaPlayer = mediaPlayer!!

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    fun onClearClick(view: View) {
        Persistence.withFileState(this) { _, fileState ->
            fileState.sections.clear()
            sectionsFragment!!.replaceAll(fileState.sections)
        }
    }

    override fun onSectionClick(section: Section) {
        val mediaPlayer = mediaPlayer!!
        mediaPlayer.seekTo(section.start)
        mediaPlayer.start()
        pauseAt = section.end
    }
}
