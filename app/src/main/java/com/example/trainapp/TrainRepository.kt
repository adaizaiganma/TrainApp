package com.example.trainapp

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TrainRepository {
    private var currentAccessToken: String? = null

    // Helper to clear token if credentials change
    fun resetToken() {
        currentAccessToken = null
    }

    private fun withToken(onToken: (String) -> Unit, onFailure: () -> Unit) {
        if (currentAccessToken != null) {
            onToken(currentAccessToken!!)
            return
        }

        val clientId = TrainApp.instance.clientId
        val clientSecret = TrainApp.instance.clientSecret

        if (clientId.isBlank() || clientSecret.isBlank()) {
            Log.e("TDX", "Missing Credentials")
            onFailure()
            return
        }

        RetrofitClient.authService.getToken(
            clientId = clientId,
            clientSecret = clientSecret
        ).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    currentAccessToken = "Bearer ${response.body()?.accessToken}"
                    onToken(currentAccessToken!!)
                } else {
                    Log.e("TDX", "Login Failed: ${response.code()}")
                    onFailure()
                }
            }
            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("TDX", "Login Error: ${t.message}")
                onFailure()
            }
        })
    }

    fun fetchAlerts(onResult: (List<TdxAlert>) -> Unit) {
        withToken({ token ->
            RetrofitClient.dataService.getAlerts(token)
                .enqueue(object : Callback<List<TdxAlert>> {
                    override fun onResponse(call: Call<List<TdxAlert>>, response: Response<List<TdxAlert>>) {
                        if (response.isSuccessful) {
                            onResult(response.body() ?: emptyList())
                        } else {
                            onResult(emptyList())
                        }
                    }
                    override fun onFailure(call: Call<List<TdxAlert>>, t: Throwable) {
                        onResult(emptyList())
                    }
                })
        }, { onResult(emptyList()) })
    }

    fun fetchTrains(stationId: String, onResult: (List<TrainSchedule>) -> Unit) {
        withToken({ token ->
            RetrofitClient.dataService.getStationLiveBoard(token, stationId)
                .enqueue(object : Callback<List<TdxTrainInfo>> {
                    override fun onResponse(call: Call<List<TdxTrainInfo>>, response: Response<List<TdxTrainInfo>>) {
                        if (response.isSuccessful) {
                            val rawList = response.body() ?: emptyList()
                            val uiList = rawList.map { raw ->
                                TrainSchedule(
                                    trainNo = raw.trainNo,
                                    trainType = raw.trainTypeName.zh,
                                    depTime = raw.depTime?.take(5) ?: "--:--",
                                    arrTime = raw.arrTime?.take(5) ?: "--:--",
                                    destination = raw.destination.zh,
                                    delayMinutes = raw.delayMinutes
                                )
                            }
                            onResult(uiList)
                        } else {
                            onResult(emptyList())
                        }
                    }
                    override fun onFailure(call: Call<List<TdxTrainInfo>>, t: Throwable) {
                        onResult(emptyList())
                    }
                })
        }, { onResult(emptyList()) })
    }

    fun fetchAllStations(onResult: (Map<String, List<Station>>) -> Unit) {
        withToken({ token ->
            RetrofitClient.dataService.getStationList(token)
                .enqueue(object : Callback<List<TdxStation>> {
                    override fun onResponse(call: Call<List<TdxStation>>, response: Response<List<TdxStation>>) {
                        if (response.isSuccessful) {
                            val rawList = response.body() ?: emptyList()
                            val map = rawList
                                .filter { !it.city.isNullOrBlank() }
                                .groupBy { it.city!! }
                                .mapValues { entry ->
                                    entry.value.map { Station(it.stationId, it.stationName.zh) }
                                }
                            onResult(map)
                        } else {
                            onResult(emptyMap())
                        }
                    }
                    override fun onFailure(call: Call<List<TdxStation>>, t: Throwable) {
                        onResult(emptyMap())
                    }
                })
        }, { onResult(emptyMap()) })
    }

    fun searchTrains(
        originId: String,
        destId: String,
        date: String,
        startTime: String,
        carTypeKeyword: String,
        onResult: (List<TrainSchedule>) -> Unit
    ) {
        withToken({ token ->
            RetrofitClient.dataService.getTrainTimetable(token, originId, destId, date)
                .enqueue(object : Callback<List<TdxODResponse>> {
                    override fun onResponse(call: Call<List<TdxODResponse>>, response: Response<List<TdxODResponse>>) {
                        if (response.isSuccessful) {
                            val rawList = response.body() ?: emptyList()
                            val fullList = rawList.mapNotNull { item ->
                                val tInfo = item.trainInfo
                                val oTime = item.originTime
                                val dTime = item.destTime

                                if (tInfo == null || oTime == null || dTime == null) return@mapNotNull null

                                TrainSchedule(
                                    trainNo = tInfo.trainNo ?: "",
                                    trainType = tInfo.trainTypeName?.zh ?: "",
                                    depTime = oTime.departureTime?.take(5) ?: "--:--",
                                    arrTime = dTime.arrivalTime?.take(5) ?: "--:--",
                                    destination = tInfo.endingStationName?.zh ?: "",
                                    delayMinutes = 0
                                )
                            }

                            val timeFilteredList = fullList.filter { it.depTime >= startTime }

                            val filteredList = if (carTypeKeyword.contains("All") || carTypeKeyword.contains("所有")) {
                                timeFilteredList
                            } else {
                                val zhKeyword = when {
                                    carTypeKeyword.contains("Tze-Chiang") || carTypeKeyword.contains("自強") -> "自強"
                                    carTypeKeyword.contains("Puyuma") || carTypeKeyword.contains("普悠瑪") || carTypeKeyword.contains("Taroko") || carTypeKeyword.contains("太魯閣") -> "太魯閣"
                                    carTypeKeyword.contains("Local") || carTypeKeyword.contains("區間") -> "區間"
                                    else -> ""
                                }

                                timeFilteredList.filter { 
                                    it.trainType.contains(zhKeyword) || 
                                    (zhKeyword == "太魯閣" && it.trainType.contains("普悠瑪")) 
                                }
                            }
                            onResult(filteredList.sortedBy { it.depTime })
                        } else {
                            onResult(emptyList())
                        }
                    }
                    override fun onFailure(call: Call<List<TdxODResponse>>, t: Throwable) {
                        onResult(emptyList())
                    }
                })
        }, { onResult(emptyList()) })
    }
}
