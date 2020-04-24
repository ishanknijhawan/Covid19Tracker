package com.ishanknijhawan.covid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ishanknijhawan.covid.R
import com.ishanknijhawan.covid.SpannableDelta
import com.ishanknijhawan.covid.client.StatewiseItem
//import kotlinx.android.synthetic.main.activity_main.view.activeTv
//import kotlinx.android.synthetic.main.activity_main.view.confirmedTv
//import kotlinx.android.synthetic.main.activity_main.view.deceasedTv
//import kotlinx.android.synthetic.main.activity_main.view.recoveredTv
import kotlinx.android.synthetic.main.item_list.view.*

class StateListAdapter(val list: List<StatewiseItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        val item = list[position]

        if (item.deltaconfirmed == "0"){
            view.confirmedTv.apply {
                text = String.format("%,d",item.confirmed!!.toInt())
            }
        }
        else {
            view.confirmedTv.apply {
                val temp = String.format("%,d",item.confirmed!!.toInt())
                text = SpannableDelta(
                    "${String.format("%,d",item.confirmed.toInt())}\n " +
                            "↑${String.format("%,d",item.deltaconfirmed!!.toInt()) ?: ""}",
                    "#D32F2F",
                    temp.length
                )
            }
        }
//        view.activeTv.text = SpannableDelta(
//            "${item.active}\n ↑ ${item.deltaactive ?: ""}",
//            "#1976D2",
//            item.confirmed?.length ?: 0
//        )

        if (item.deltarecovered == "0"){
            view.recoveredTv.text = String.format("%,d",item.recovered!!.toInt())
        }
        else {
            val temp = String.format("%,d",item.recovered!!.toInt())
            view.recoveredTv.text = SpannableDelta(

                "${String.format("%,d",item.recovered!!.toInt())}\n" +
                        " ↑${String.format("%,d",item.deltarecovered!!.toInt()) ?: ""}",
                "#388E3C",
                temp.length
            )
        }

        if (item.deltadeaths == "0"){
            view.deceasedTv.text = String.format("%,d",item.deaths!!.toInt())
        }
        else {
            val temp = String.format("%,d",item.deaths!!.toInt())
            view.deceasedTv.text = SpannableDelta(
                "${String.format("%,d",item.deaths.toInt())}\n" +
                        " ↑${String.format("%,d",item.deltadeaths!!.toInt())}",
                "#000000",
                temp.length
            )
        }

        view.stateTv.text = item.state
        return view
    }

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount(): Int = list.size

}