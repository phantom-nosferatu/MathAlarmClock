package com.example.alarm_puzzle

import android.Manifest
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.alarm_puzzle.data.Alarm
import com.example.alarm_puzzle.data.AlarmDatabase
import com.example.alarm_puzzle.data.AlarmViewModel
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import kotlinx.android.synthetic.main.activity_add_alarm.*
import kotlinx.android.synthetic.main.fragment_time_picker.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern


class AddAlarmActivity : AppCompatActivity() {
    private lateinit var mAlarmViewModel: AlarmViewModel

    private var audioPath: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        mAlarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)

        requestPermission()

        btn_save_alarm.setOnClickListener {
            initAlarm()
            insertDataToDatabase()
        }

        btn_aud_picker.setOnClickListener {
            setAudio()
        }
    }


    private fun hasReadExternalStoragePermission() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        val permissionToRequest = mutableListOf<String>()
        if (!hasReadExternalStoragePermission()) {
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionToRequest.toTypedArray(), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("request permission", "${permissions[i]} granted.")
                }
            }

        }
    }

    private fun setAudio() {
        MaterialFilePicker()
            .withActivity(this)
            .withCloseMenu(true).withHiddenFiles(true)
            .withFilter(Pattern.compile(".*\\.(mp3|wav)$"))
            .withFilterDirectories(false)
            .withTitle("Выберите аудио")
            .withRequestCode(100)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            audioPath = data?.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)!!
        }
    }


    private fun initAlarm() {
        CoroutineScope(Dispatchers.IO).launch {
            val id = getLastId() + 1
            val am: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val i = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
            val pi = PendingIntent.getBroadcast(applicationContext, id, i, 0)

            val hour = num_picker_hour.value
            val minute = num_picker_minute.value

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                am.setAlarmClock(AlarmClockInfo(calendar.timeInMillis, null), pi)

            }
        }
    }


    private fun insertDataToDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val id = getLastId() + 1
            val desc = et_desc.text.toString()
            val hour = num_picker_hour.value
            val minute = num_picker_minute.value
            val time = hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')
            val alarm =
                Alarm(
                    desc = desc,
                    hour = hour,
                    minute = minute,
                    time = time,
                    id = id,
                    isActive = true,
                    path = audioPath
                )
            mAlarmViewModel.addAlarm(alarm)
        }
        Toast.makeText(this, "Будильник успешно добавлен", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun getLastId(): Int {
        val dao = AlarmDatabase.getDatabase(application).alarmDao().getId().map { it.id }
        return dao.lastOrNull() ?: 0
    }


}
