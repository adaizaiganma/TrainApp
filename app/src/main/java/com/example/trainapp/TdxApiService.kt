package com.example.trainapp

import retrofit2.Call
import retrofit2.http.*

interface TdxApiService {

    // 1. Login (Get Token)
    @FormUrlEncoded
    @POST("auth/realms/TDXConnect/protocol/openid-connect/token")
    fun getToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Call<TokenResponse>

    // 2. Get Train Data (Live Board)
    @GET("api/basic/v2/Rail/TRA/LiveBoard/Station/{StationID}")
    fun getStationLiveBoard(
        @Header("Authorization") auth: String,
        @Path("StationID") stationId: String,
        @Query("\$format") format: String = "JSON"
    ): Call<List<TdxTrainInfo>>


    // 3. Get All Stations
    @GET("api/basic/v2/Rail/TRA/Station")
    fun getStationList(
        @Header("Authorization") auth: String,
        @Query("\$format") format: String = "JSON"
    ): Call<List<TdxStation>>

    // 4. Search Trains (Origin -> Destination)
    @GET("api/basic/v2/Rail/TRA/DailyTimetable/OD/{OriginID}/to/{DestID}/{Date}")
    fun getTrainTimetable(
       @Header("Authorization") auth: String,
       @Path("OriginID") originId: String,
       @Path("DestID") destId: String,
       @Path("Date") date: String,
       @Query("\$format") format: String = "JSON"
    ): Call<List<TdxODResponse>>

    // 5. Get TRA Alerts
    @GET("api/basic/v2/Rail/TRA/Alert")
    fun getAlerts(
        @Header("Authorization") auth: String,
        @Query("\$format") format: String = "JSON"
    ): Call<List<TdxAlert>>
}