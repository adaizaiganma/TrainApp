package com.example.trainapp

data class TrainSchedule(
    val trainNo: String,       // 車次
    val trainType: String,     // 車種
    val depTime: String,       // 出發時間
    val arrTime: String,       // 抵達時間
    val destination: String,   // 終點站
    val delayMinutes: Int      // 誤點分鐘
)