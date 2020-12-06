package com.example.alarm_puzzle


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.alarm_puzzle.data.AlarmViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAlarmViewModel: AlarmViewModel
    private lateinit var mAlarmRecyclerView: RecyclerView
    private lateinit var mAlarmAdapter: RvAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAlarmAdapter = RvAdapter(this)
        mAlarmRecyclerView = findViewById(R.id.recycler_alarm)
        mAlarmRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mAlarmRecyclerView.adapter = mAlarmAdapter

        mAlarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)


        mAlarmAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (mAlarmAdapter.itemCount == 0) {
                    recycler_alarm.visibility = GONE
                    img_alarm_clock.visibility = VISIBLE
                    tv_empty_alarm_list.visibility = VISIBLE
                } else {
                    recycler_alarm.visibility = VISIBLE
                    img_alarm_clock.visibility = GONE
                    tv_empty_alarm_list.visibility = GONE
                }
            }
        })

        mAlarmViewModel.readAllData.observe(this, Observer
        { alarm ->
            mAlarmAdapter.setData(alarm)
        })

        val itemtouchhelper = ItemTouchHelper(SwipeToDelete(mAlarmAdapter, mAlarmViewModel, this))
        itemtouchhelper.attachToRecyclerView(mAlarmRecyclerView)

    }


    fun fabAddAlarm(view: View) {
        val addAlarm = Intent(this, AddAlarmActivity::class.java)
        startActivity(addAlarm)
    }
}