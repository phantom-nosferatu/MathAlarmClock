package com.example.alarm_puzzle

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alarm_puzzle.data.AlarmDatabase
import kotlinx.android.synthetic.main.activity_alarm_on_active.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random


class AlarmOnActive : AppCompatActivity() {

    private lateinit var path: String
    var mp = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_on_active)

        CoroutineScope(Dispatchers.IO).launch {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute =  calendar.get(Calendar.MINUTE)

            val dao = AlarmDatabase.getDatabase(application).alarmDao()
            val audioPath = dao.loadAudioPath(hour = hour, minute = minute).map { it.path }
            path = audioPath[0].toString()
            startMusic(path)
        }


        val random1 = Random.nextInt(100)
        val random2 = Random.nextInt(100)
        val result = random1 + random2

        text_random_number1.text = random1.toString()
        text_random_number2.text = random2.toString()



        stop_button.setOnClickListener {
            stopAlarm(result)
        }
    }

    private fun startMusic(path: String) {
        if(path == "default") {
            val uri = Uri.parse("android.resource://com.example.alarm_puzzle/raw/alarm_mus")
            mp.setDataSource(this, uri)
            mp.prepare()
            mp.start()
        } else {
            mp.setDataSource(path)
            mp.prepare()
            mp.start()
        }
    }

    private fun stopAlarm(result: Int) {
        val answer = et_result.text.toString()
        if (answer.toIntOrNull() == result) {
            mp.stop()
            Toast.makeText(this, "Будильник отключен", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Повторите снова!", Toast.LENGTH_SHORT).show()
        }
    }
}
