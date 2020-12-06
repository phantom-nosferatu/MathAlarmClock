package com.example.alarm_puzzle.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "alarm_table")
data class Alarm(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "isActive")
    val isActive: Boolean?,
    @ColumnInfo(name ="hour")
    val hour: Int?,
    @ColumnInfo(name ="minute")
    val minute: Int?,
    @ColumnInfo(name ="time")
    val time: String?,
    @ColumnInfo(name = "desc")
    val desc: String?,
    @ColumnInfo(name = "audio_path")
    val path: String?
)