package com.example.trainapp

import com.google.gson.annotations.SerializedName

// 1. The Login Response
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// 2. The Train Live Board Response
data class TdxTrainInfo(
    @SerializedName("TrainNo") val trainNo: String,
    @SerializedName("TrainTypeName") val trainTypeName: TdxName,
    @SerializedName("StationName") val stationName: TdxName,
    @SerializedName("ScheduledDepartureTime") val depTime: String?,
    @SerializedName("ScheduledArrivalTime") val arrTime: String?,
    @SerializedName("DelayTime") val delayMinutes: Int,
    @SerializedName("EndingStationName") val destination: TdxName
)

// Helper for Name
data class TdxName(
    @SerializedName("Zh_tw") val zh: String,
    @SerializedName("En") val en: String
)

// 3. Station Info
data class TdxStation(
    @SerializedName("StationID") val stationId: String,
    @SerializedName("StationName") val stationName: TdxName,
    @SerializedName("StationAddress") val address: String?,
    @SerializedName("LocationCity") val city: String?
)

// 4. Origin-Destination (OD) Timetable Response
data class TdxODResponse(
    @SerializedName("DailyTrainInfo") val trainInfo: TdxODTrainInfo?,
    @SerializedName("OriginStopTime") val originTime: TdxStopTime?,
    @SerializedName("DestinationStopTime") val destTime: TdxStopTime?
)

data class TdxODTrainInfo(
    @SerializedName("TrainNo") val trainNo: String?,
    @SerializedName("TrainTypeName") val trainTypeName: TdxName?,
    @SerializedName("EndingStationName") val endingStationName: TdxName?,
    @SerializedName("Direction") val direction: Int?
)

data class TdxStopTime(
    @SerializedName("ArrivalTime") val arrivalTime: String?,
    @SerializedName("DepartureTime") val departureTime: String?,
    @SerializedName("StationName") val stationName: TdxName?
)

data class Station(
    val id: String,
    val name: String
)

// 5. TRA Alert Models
data class TdxAlert(
    @SerializedName("AlertID") val alertId: String,
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Effects") val effects: String?,
    @SerializedName("PublishTime") val publishTime: String,
    @SerializedName("StartTime") val startTime: String,
    @SerializedName("EndTime") val endTime: String,
    @SerializedName("AlertUrl") val alertUrl: String?
)
