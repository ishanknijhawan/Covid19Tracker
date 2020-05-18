package com.ishanknijhawan.covid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ishanknijhawan.covid.client.Country
import com.ishanknijhawan.covid.R
import com.ishanknijhawan.covid.SpannableDelta
import kotlinx.android.synthetic.main.item_list.view.*
import kotlin.math.abs

class CountryAdapter(val list: List<Country>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        val item = list[position]

        view.confirmedTv.apply {
            val temp = String.format("%,d",item.totalConfirmed)
            text = SpannableDelta(
                "${String.format("%,d",item.totalConfirmed)}\n" +
                        " ↑${String.format("%,d",item.newConfirmed)}",
                "#AAAAAA",
                temp.length
            )
        }

//        if(item.newConfirmed-item.newDeaths-item.newRecovered >= 0){
//            view.activeTv.text = SpannableDelta(
//                "${item.totalConfirmed - item.totalDeaths - item.totalRecovered}\n ↑${item.newConfirmed - item.newDeaths - item.newRecovered}",
//                "#1976D2",
//                item.totalConfirmed.toString().length
//            )
//        }
//        else {
//            val mod = abs(item.newConfirmed-item.newDeaths-item.newRecovered)
//            view.activeTv.text = SpannableDelta(
//                "${item.totalConfirmed - item.totalDeaths - item.totalRecovered}\n ↓$mod",
//                "#1976D2",
//                item.totalConfirmed.toString().length
//            )
//        }

        val temp = String.format("%,d",item.totalRecovered)
        view.recoveredTv.text = SpannableDelta(
            "${String.format("%,d",item.totalRecovered)}\n" +
                    " ↑${String.format("%,d",item.newRecovered)}",
            "#AAAAAA",
            temp.length
        )

        val temp2 = String.format("%,d",item.totalDeaths)
        view.deceasedTv.text = SpannableDelta(
            "${String.format("%,d",item.totalDeaths)}\n" +
                    " ↑${String.format("%,d",item.newDeaths)}",
            "#AAAAAA",
            temp2.length
        )
        view.stateTv.text = item.country
        return view
    }

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount(): Int = list.size

}