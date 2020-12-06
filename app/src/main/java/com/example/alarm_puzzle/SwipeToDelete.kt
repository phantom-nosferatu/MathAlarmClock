package com.example.alarm_puzzle

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.alarm_puzzle.data.AlarmViewModel


class SwipeToDelete(private var adapter: RvAdapter, private var viewModel: AlarmViewModel, private var context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

private val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
       val id = adapter.getId(viewHolder.layoutPosition)
        val i = Intent(context,AlarmBroadcastReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, id!!, i,0)
        am.cancel(pi)
        viewModel.delete(adapter.getAlarmAt(viewHolder.layoutPosition))
    }

}