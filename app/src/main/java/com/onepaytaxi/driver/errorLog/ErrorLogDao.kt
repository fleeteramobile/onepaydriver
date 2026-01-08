package com.onepaytaxi.driver.errorLog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ErrorLogDao {
    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLocationLog(vararg models: LocationModel)

    @Query("DELETE FROM locationLog where timeStamp NOT IN (SELECT timeStamp from locationLog ORDER BY timeStamp DESC LIMIT 5 )")
    abstract fun deleteLocationLog()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertNetworkLog(vararg models: NetworkModel)

    @Query("DELETE FROM networkLog where timeStamp NOT IN (SELECT timeStamp from networkLog ORDER BY timeStamp DESC LIMIT 5 )")
    abstract fun deleteNetworkLog()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGpsLog(vararg models: GpsModel)

    @Query("DELETE FROM gpsLog where timeStamp NOT IN (SELECT timeStamp from gpsLog ORDER BY timeStamp DESC LIMIT 5 )")
    abstract fun deleteGpsLog()*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertApiErrorLog(vararg models: ApiErrorModel)
/*
    @Query("DELETE FROM apiErrorModel where timeStamp NOT IN (SELECT timeStamp from apiErrorModel ORDER BY timeStamp DESC LIMIT 5 )")
    abstract fun deleteApiErrorLog()

    @Query("SELECT * FROM gpsLog")
    abstract fun getAllGpsErrorLogs(): List<GpsModel>

    @Query("SELECT * FROM networkLog")
    abstract fun getAllNetworkErrorLogs(): List<NetworkModel>

    @Query("SELECT * FROM locationLog")
    abstract fun getAllLocationErrorLogs(): List<LocationModel>*/

    @Query("SELECT * FROM apiErrorModel WHERE sendStatus = 0 LIMIT 1")
    fun getAllApiErrorLogs(): List<ApiErrorModel>

    @Query("DELETE FROM apiErrorModel WHERE timeStamp < :date ")
    fun deleteAllApiErrorLogs(date:String)

    @Query("SELECT COUNT(timeStamp) FROM apiErrorModel WHERE error = :error and timeStamp = :currTime")
    fun getCount(error:String,currTime:String):Int

    @Query("UPDATE apiErrorModel set sendStatus = :status WHERE ids = :id")
    fun updateSendStatus(status:Int,id:Int)
}