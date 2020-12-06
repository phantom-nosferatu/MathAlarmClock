package com.example.alarm_puzzle.data

import androidx.lifecycle.LiveData

class AlarmRepository(private val alarmDao: AlarmDao) {

    val readAllData: LiveData<List<Alarm>> = alarmDao.AllReadAlarm()

    suspend fun addAlarm(alarm: Alarm) {
        alarmDao.AddAlarm(alarm)
    }

    suspend fun delete(alarm: Alarm) {
        alarmDao.delete(alarm)
    }

}