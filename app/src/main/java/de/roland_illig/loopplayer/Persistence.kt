package de.roland_illig.loopplayer

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class AppState(
        var uri: String,
        var fileName: String) : Serializable

data class FileState(
        val sections: ArrayList<Section>) : Serializable

data class Section(
        val start: Int,
        val end: Int) : Serializable

object Persistence {

    fun withAppState(ctx: Context, action: (AppState) -> Unit) {
        modify(ctx, "appState", action, { AppState("", "") })
    }

    fun withFileState(ctx: Context, action: (AppState, FileState) -> Unit) {
        withAppState(ctx) { appState ->
            if (appState.fileName != "") {
                modify(ctx, appState.fileName, { action(appState, it) }, { FileState(ArrayList()) })
            }
        }
    }

    private fun <T : Serializable> modify(ctx: Context, fileName: String, action: (T) -> Unit, new: () -> T) {
        fun load(): T {
            try {
                ctx.openFileInput(fileName).use {
                    ObjectInputStream(it).use {
                        return it.readObject() as T
                    }
                }
            } catch (e: Exception) {
                return new()
            }
        }

        fun save(state: T) {
            ctx.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                ObjectOutputStream(it).use {
                    it.writeObject(state)
                }
            }
        }

        val obj = load()
        action(obj)
        save(obj)
    }
}
