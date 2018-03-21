package sagsaguz.brillavedicmaths.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import sagsaguz.brillavedicmaths.R
import java.util.ArrayList


class VideosListAdapter(private var context: Context, private var list: ArrayList<String>) : BaseAdapter() {

    private var inflater: LayoutInflater? = null

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {

        val holder = Holder()
        val rowView = inflater!!.inflate(R.layout.list_items, null)
        holder.tvSLno = rowView.findViewById(R.id.tvSLno)
        holder.tvSLno!!.text = (position+1).toString()
        holder.tvVideoName = rowView.findViewById(R.id.tvVideoName)
        holder.tvVideoName!!.text = list[position]

        return rowView
    }

    private inner class Holder {
        internal var tvSLno: TextView? = null
        internal var tvVideoName: TextView? = null
    }

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

}
