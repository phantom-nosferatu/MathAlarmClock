package com.example.alarm_puzzle.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface AlarmDao {

    @Insert
    suspend fun AddAlarm(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Query("UPDATE alarm_table SET isActive = :isActive WHERE id = :id")
    fun updateActive(isActive: Boolean, id: Int?) : Int

    @Query("SELECT * FROM alarm_table")
    fun AllReadAlarm(): LiveData<List<Alarm>>

    @Query("SELECT audio_path FROM alarm_table WHERE hour = :hour AND minute = :minute")
    fun loadAudioPath(hour:Int, minute:Int): List<Alarm>

    @Query("SELECT id FROM alarm_table")
    fun getId() : List<Alarm>


}