package com.code.weatherinfo.weatherforecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.code.weatherinfo.model.custom.ForcasteList
import kotlinx.android.synthetic.main.layout_item_weather_item.view.*
import android.text.format.DateFormat;
import java.text.SimpleDateFormat


class ForcasteAdapter(val context: Context, var forcasteList: ArrayList<ForcasteList>) :
    RecyclerView.Adapter<ForcasteAdapter.ForcasteAdapterViewHolder>() {

    fun updateForcateItems(list: List<ForcasteList>) {
        this.forcasteList.clear()
        forcasteList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ForcasteAdapterViewHolder(
        LayoutInflater.from(parent.context).inflate(
            com.code.weatherinfo.R.layout.layout_item_weather_item,
            parent,
            false
        )
    )

    override fun getItemCount() = forcasteList.size

    override fun onBindViewHolder(holder: ForcasteAdapterViewHolder, position: Int) {
        holder.bind(forcasteList[position])
    }

    class ForcasteAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val day = view.temp_day
        var temp = view.temp_value

        fun bind(model: ForcasteList) {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = format.parse(model.dt_txt)
                day.text = DateFormat.format("EE", date)
            }catch (e:Exception){
                e.printStackTrace()
                day.text = ""
            }

            temp.text = "" + model.main.temp


        }
    }
}
