package de.roland_illig.loopplayer

import android.app.ListFragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class SectionListFragment : ListFragment() {

    interface Callback {
        fun onSectionClick(section: Section)
        fun init(fragment: SectionListFragment)
    }

    override fun onResume() {
        super.onResume()
        listAdapter = Adapter()
        (activity as Callback).init(this)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val section = adapter().getItem(position)

        (activity as Callback).onSectionClick(section)
    }

    internal inner class Adapter
        : ArrayAdapter<Section>(activity, android.R.layout.simple_list_item_1, ArrayList()) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val row = super.getView(position, convertView, parent)
            val title = row.findViewById<TextView>(android.R.id.text1)
            val section = getItem(position)
            title.text = String.format("%s until %s", formatTime(section.start), formatTime(section.end))
            return row
        }
    }

    fun getLast(): Section? {
        val sections = adapter()
        return if (sections.isEmpty) null else sections.getItem(sections.count - 1)
    }

    fun add(section: Section) {
        adapter().add(section)
    }

    fun replaceAll(sections: List<Section>) {
        val adapter = adapter()
        adapter.clear()
        adapter.addAll(sections)
    }

    private fun adapter() = (listAdapter as ArrayAdapter<Section>)
}
