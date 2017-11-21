package de.roland_illig.loopplayer

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class AppState(
        var uri: String,
        val cuePoints: ArrayList<CuePoint>)
    : Serializable

fun loadAppState(ctx: Context): AppState {
    try {
        ctx.openFileInput("state").use {
            ObjectInputStream(it).use {
                return it.readObject() as AppState
            }
        }
    } catch (e: Exception) {
        return AppState("", ArrayList())
    }
}

fun withAppState(ctx: Context, action: (appState: AppState) -> Unit) {

    fun saveAppState(ctx: Context, state: AppState) {
        ctx.openFileOutput("state", Context.MODE_PRIVATE).use {
            ObjectOutputStream(it).use {
                it.writeObject(state)
            }
        }
    }

    val appState = loadAppState(ctx)
    action(appState)
    saveAppState(ctx, appState)
}
