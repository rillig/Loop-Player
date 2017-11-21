package de.roland_illig.loopplayer

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log

abstract class LifecycleLoggingActivity : Activity() {
    init {
        log("constructor")
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        log("onCreate")
    }

    public override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    public override fun onStart() {
        super.onStart()
        log("onStart")
    }

    public override fun onResume() {
        super.onResume()
        log("onResume")
    }

    public override fun onPause() {
        log("onPause")
        super.onPause()
    }

    public override fun onStop() {
        log("onStop")
        super.onStop()
    }

    public override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    protected fun log(msg: String) {
        Log.d(javaClass.simpleName, msg)
    }
}
