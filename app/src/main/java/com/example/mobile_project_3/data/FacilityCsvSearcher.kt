package com.example.mobile_project_3.data

import android.content.Context
import com.naver.maps.geometry.LatLng
import java.io.File
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object FacilityCsvSearcher {

    private const val CSV_PATH = "C:/Users/chlwn/AndroidStudioProjects/Mobile_project_3/app/src/main/assets/facility_all.csv"

    private fun normalize(str: String): String =
        str.replace("\\s+".toRegex(), "").lowercase()

    fun searchFacilitiesNearPosition(
        context: Context,
        center: LatLng,
        limit: Int = 5
    ): List<FacilityItem> {
        val lines = try {
            context.assets.open("facility_all.csv").bufferedReader().readLines().drop(1)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        return lines.mapNotNull { line ->
            val tokens = line.split(",")
            if (tokens.size < 9) return@mapNotNull null

            val lat = tokens[2].toDoubleOrNull()
            val lng = tokens[3].toDoubleOrNull()
            if (lat != null && lng != null) {
                val dist = haversine(center.latitude, center.longitude, lat, lng)
                FacilityItem(
                    name = tokens[0],
                    type = tokens[1],
                    latitude = tokens[2],
                    longitude = tokens[3],
                    address = tokens[4],
                    businessStatus = tokens[5],
                    estbDate = tokens[6],
                    facilityId = tokens[7],
                    welfacilityId = tokens[8]
                ) to dist
            } else null
        }.sortedBy { it.second }
            .take(limit)
            .map { it.first }
    }

    // 두 좌표 간 거리 계산 (Haversine)
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // meters
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δφ = Math.toRadians(lat2 - lat1)
        val Δλ = Math.toRadians(lon2 - lon1)

        val a = sin(Δφ / 2).pow(2.0) +
                cos(φ1) * cos(φ2) *
                sin(Δλ / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    fun searchFacilitiesByKeyword(context: Context, query: String): List<FacilityItem> {
        val lines = try {
            context.assets.open("facility_all.csv").bufferedReader().readLines().drop(1)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        val normalizedQuery = normalize(query)

        val allItems = mutableListOf<FacilityItem>()
        var baseLat: Double? = null
        var baseLng: Double? = null

        lines.forEach { line ->
            val tokens = line.split(",")
            if (tokens.size < 9) return@forEach

            val item = FacilityItem(
                name = tokens[0],
                type = tokens[1],
                latitude = tokens[2],
                longitude = tokens[3],
                address = tokens[4],
                businessStatus = tokens[5],
                estbDate = tokens[6],
                facilityId = tokens[7],
                welfacilityId = tokens[8]
            )

            allItems.add(item)

            if (baseLat == null && baseLng == null) {
                val nameMatch = normalize(item.name).contains(normalizedQuery)
                val addressMatch = normalize(item.address).contains(normalizedQuery)
                if (nameMatch || addressMatch) {
                    baseLat = item.latitude.toDoubleOrNull()
                    baseLng = item.longitude.toDoubleOrNull()
                }
            }
        }

        if (baseLat == null || baseLng == null) return emptyList()

        return allItems.mapNotNull { item ->
            val lat = item.latitude.toDoubleOrNull()
            val lng = item.longitude.toDoubleOrNull()
            if (lat != null && lng != null) {
                val dist = haversine(baseLat!!, baseLng!!, lat, lng)
                item to dist
            } else null
        }.sortedBy { it.second }
            .take(20)
            .map { it.first }
    }

    fun searchFacilitiesByKeyword2(query: String): List<FacilityItem> {
        val file = File(CSV_PATH)
        if (!file.exists()) return emptyList()

        val lines = file.readLines().drop(1)
        val normalizedQuery = normalize(query)

        val allItems = mutableListOf<FacilityItem>()
        var baseLat: Double? = null
        var baseLng: Double? = null

        lines.forEach { line ->
            val tokens = line.split(",")
            if (tokens.size < 9) return@forEach

            val item = FacilityItem(
                name = tokens[0],
                type = tokens[1],
                latitude = tokens[2],
                longitude = tokens[3],
                address = tokens[4],
                businessStatus = tokens[5],
                estbDate = tokens[6],
                facilityId = tokens[7],
                welfacilityId = tokens[8]
            )

            allItems.add(item)

            if (baseLat == null && baseLng == null) {
                val nameMatch = normalize(item.name).contains(normalizedQuery)
                val addressMatch = normalize(item.address).contains(normalizedQuery)
                if (nameMatch || addressMatch) {
                    baseLat = item.latitude.toDoubleOrNull()
                    baseLng = item.longitude.toDoubleOrNull()
                }
            }
        }

        if (baseLat == null || baseLng == null) return emptyList()

        return allItems
            .mapNotNull { item ->
                val lat = item.latitude.toDoubleOrNull()
                val lng = item.longitude.toDoubleOrNull()
                if (lat != null && lng != null) {
                    val dist = haversine(baseLat!!, baseLng!!, lat, lng)
                    item to dist
                } else null
            }
            .sortedBy { it.second }
            .take(20)
            .map { it.first }
    }
    fun searchFacilitiesByIds(context: Context, ids: Set<String>): List<FacilityItem> {
        val lines = try {
            context.assets.open("facility_all.csv").bufferedReader().readLines().drop(1)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        return lines.mapNotNull { line ->
            val tokens = line.split(",")
            if (tokens.size < 9) return@mapNotNull null

            val item = FacilityItem(
                name = tokens[0],
                type = tokens[1],
                latitude = tokens[2],
                longitude = tokens[3],
                address = tokens[4],
                businessStatus = tokens[5],
                estbDate = tokens[6],
                facilityId = tokens[7],
                welfacilityId = tokens[8]
            )

            if (ids.contains(item.welfacilityId)) item else null
        }
    }
}

fun main5() {
    val results = FacilityCsvSearcher.searchFacilitiesByKeyword2("아산병원")
    println("가까운 시설 10개:\n")
    results.forEach {
        println("• ${it.name} ${it.type} ${it.latitude} ${it.longitude} ${it.address}")
    }
}