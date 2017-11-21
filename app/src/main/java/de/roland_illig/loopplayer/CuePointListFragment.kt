package de.roland_illig.loopplayer

import android.app.ListFragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class CuePointListFragment : ListFragment() {

    interface Callback {
        fun onCueClick(cuePoint: CuePoint)
        fun init(fragment: CuePointListFragment)
    }

    override fun onResume() {
        super.onResume()
        listAdapter = Adapter()
        (activity as Callback).init(this)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val cuePoint = adapter().getItem(position)

        (activity as Callback).onCueClick(cuePoint)
    }

    internal inner class Adapter
        : ArrayAdapter<CuePoint>(activity, android.R.layout.simple_list_item_1, ArrayList()) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val row = super.getView(position, convertView, parent)
            val title = row.findViewById<TextView>(android.R.id.text1)
            fun formatTime(millis: Int) = String.format("%d:%02d.%01d", millis / 60_000, millis / 1000 % 60, millis / 100 % 10)
            val cuePoint = getItem(position)
            title.text = String.format("%s until %s", formatTime(cuePoint.start), formatTime(cuePoint.end))
            return row
        }
    }

    fun getLast(): CuePoint? {
        val cuePoints = adapter()
        return if (cuePoints.isEmpty) null else cuePoints.getItem(cuePoints.count - 1)
    }

    fun add(cuePoint: CuePoint) {
        adapter().add(cuePoint)
    }

    fun replaceAll(cuePoints: List<CuePoint>) {
        val adapter = adapter()
        adapter.clear()
        adapter.addAll(cuePoints)
    }

    private fun adapter() = (listAdapter as ArrayAdapter<CuePoint>)
}
