package com.example.alarm_puzzle

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.alarm_puzzle.data.Alarm
import com.example.alarm_puzzle.data.AlarmDao
import com.example.alarm_puzzle.data.AlarmDatabase
import com.example.alarm_puzzle.data.AlarmViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class RvAdapter(context: Context) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    private lateinit var alarmList: ArrayList<Alarm>
    private lateinit var mDao: AlarmDao
    private val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val filledView =
            LayoutInflater.from(parent.context).inflate(R.layout.alarm_card, parent, false)
        return ViewHolder(filledView)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = alarmList[position].time
        holder.desc.text = alarmList[position].desc
        holder.switch.isChecked = alarmList[position].isActive!!

       setupSwitch(holder.switch.isChecked, holder.switch, holder.cardview, holder.time, holder.desc)


        holder.switch.setOnCheckedChangeListener { switch_, isChecked ->
            val context = holder.switch.context
            val id = getId(holder.layoutPosition)
            CoroutineScope(Dispatchers.IO).launch {
                mDao = AlarmDatabase.getDatabase(context).alarmDao()
            }
            if (isChecked) {
                Toast.makeText(context, "Будильник опять активен", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    holder.cardview.setCardBackgroundColor(Color.WHITE)
                    holder.time.setTextColor(Color.BLACK)
                    holder.desc.setTextColor(Color.GRAY)
                    mDao.updateActive(
                        isActive = true,
                        id = id)
                    val i = Intent(context, AlarmBroadcastReceiver::class.java)
                    val pi = PendingIntent.getBroadcast(context, id!!, i, 0)
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar.set(Calendar.HOUR_OF_DAY, alarmList[position].hour!!)
                    calendar.set(Calendar.MINUTE, alarmList[position].minute!!)
                    calendar.set(Calendar.SECOND, 0)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        am.setAlarmClock(AlarmManager.AlarmClockInfo(calendar.timeInMillis, null), pi)

                    }


                }
            } else {
                Toast.makeText(context, "Будильние неактивен", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val context2 = holder.switch.context
                    val color = ContextCompat.getColor(context2, R.color.alarm_noactive)
                    holder.cardview.setCardBackgroundColor(color)
                    holder.time.setTextColor(Color.WHITE)
                    holder.desc.setTextColor(ContextCompat.getColor(context2, R.color.desc_noactive))
                    mDao.updateActive(
                        isActive = false,
                        id = id
                    )
                    val i = Intent(context, AlarmBroadcastReceiver::class.java)
                    val pi = PendingIntent.getBroadcast(context, id!!, i,0)
                    am.cancel(pi)
                }
            }
        }

        }

    private fun setupSwitch(
        checked: Boolean,
        switch: SwitchMaterial,
        cardview: CardView,
        time: TextView,
        desc: TextView
    ) {
        if (checked) {
            CoroutineScope(Dispatchers.IO).launch {
                cardview.setCardBackgroundColor(Color.WHITE)
                time.setTextColor(Color.BLACK)
                desc.setTextColor(Color.GRAY)
            }

        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val context = switch.context
                val color = ContextCompat.getColor(context, R.color.alarm_noactive)
                cardview.setCardBackgroundColor(color)
                time.setTextColor(Color.WHITE)
                desc.setTextColor(ContextCompat.getColor(context, R.color.desc_noactive))
            }
        }
    }


    fun getId(position: Int): Int? {
        return alarmList[position].id
    }

    fun getAlarmAt(position: Int): Alarm = alarmList[position]


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time = itemView.findViewById<TextView>(R.id.card_tv_time)!!
        val desc = itemView.findViewById<TextView>(R.id.card_tv_desc)!!
        var switch = itemView.findViewById<SwitchMaterial>(R.id.btn_switch)!!
        val cardview = itemView.findViewById<CardView>(R.id.alarmcardview)!!

    }


    fun setData(alarm: List<Alarm>) {
        this.alarmList = alarm as ArrayList<Alarm>
        notifyDataSetChanged()
    }

}


