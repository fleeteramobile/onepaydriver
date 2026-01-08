package com.onepaytaxi.driver.errorLog

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onepaytaxi.driver.data.ModelDriverInfo
import org.json.JSONObject

@Entity(tableName = "apiErrorModel")
data class ApiErrorModel(@PrimaryKey(autoGenerate = true) val ids:Int = 0,val timeStamp: String, val apiCase: String, val error: String, val driverData: ModelDriverInfo,val inputParams:JSONObject,val classContext:String,val sendStatus:Int)