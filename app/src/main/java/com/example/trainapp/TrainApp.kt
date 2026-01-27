package com.example.trainapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrainApp : Application() {
    companion object {
        lateinit var instance: TrainApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    val prefs: SharedPreferences by lazy {
        getSharedPreferences("tdx_prefs", Context.MODE_PRIVATE)
    }

    var clientId: String
        get() = prefs.getString("client_id", "") ?: ""
        set(value) = prefs.edit().putString("client_id", value).apply()

    var clientSecret: String
        get() = prefs.getString("client_secret", "") ?: ""
        set(value) = prefs.edit().putString("client_secret", value).apply()

    // --- Favorites Logic ---
    private val gson = Gson()

    fun getFavorites(): List<FavoriteRoute> {
        val json = prefs.getString("favorites", "[]")
        val type = object : TypeToken<List<FavoriteRoute>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addFavorite(route: FavoriteRoute) {
        val current = getFavorites().toMutableList()
        // Avoid duplicates
        if (!current.any { it.originId == route.originId && it.destId == route.destId }) {
            current.add(route)
            prefs.edit().putString("favorites", gson.toJson(current)).apply()
        }
    }

    fun removeFavorite(route: FavoriteRoute) {
        val current = getFavorites().toMutableList()
        current.removeAll { it.originId == route.originId && it.destId == route.destId }
        prefs.edit().putString("favorites", gson.toJson(current)).apply()
    }
}
