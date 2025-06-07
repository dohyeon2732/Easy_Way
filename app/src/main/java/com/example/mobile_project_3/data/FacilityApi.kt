package com.example.mobile_project_3.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

object FacilityApi {
    private val client = OkHttpClient()
    private const val serviceKey = "JTftKqDq5nordNk85GvejTJcgXw88G2FilVIE46YvfVG2Io8ZkI%2FD%2BjfzcRcVTMrdqhJRSeoGaSqFpzVQb7uQQ%3D%3D" // 실제 키로 교체
    private const val baseUrl = "https://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getDisConvFaclList"
    private const val baseUrl2 = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList/getFacInfoOpenApiJpEvalInfoList.do"
    private suspend fun fetchWithParam(paramName: String, value: String): String =
        withContext(Dispatchers.IO) {
            //val encodedKey = URLEncoder.encode(serviceKey, "UTF-8")
            val encodedKey = serviceKey
            val encodedValue = URLEncoder.encode(value, "UTF-8")
            val url = "$baseUrl?serviceKey=$encodedKey&$paramName=$encodedValue"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string() ?: "Empty response"
                } else {
                    "HTTP Error: ${response.code}\n${response.message}"
                }
            }
        }

    suspend fun fetchByFacilityName(name: String) = fetchWithParam("faclNm", name)
    suspend fun fetchBySidoName(sido: String) = fetchWithParam("sidonm", sido)
    suspend fun fetchBySigunguName(sigungu: String) = fetchWithParam("sigunNm", sigungu)
    suspend fun fetchByRoadName(road: String) = fetchWithParam("roadNm", road)
    suspend fun fetchByFullAddress(addr: String) = fetchWithParam("lctNmAd", addr)

    suspend fun fetchByFacilityTypeRaw(typeName: String): String {
        return fetchWithParam("faclTyCd", typeName.trim())
    }


    suspend fun fetchEvalInfoByFacilityId(paramName: String, facilityId: String): String = withContext(Dispatchers.IO) {
        val baseUrl2 = "https://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getFacInfoOpenApiJpEvalInfoList"
        val encodedKey = serviceKey
        val encodedValue = URLEncoder.encode(facilityId, "UTF-8")
        val url = "$baseUrl2?serviceKey=$encodedKey&$paramName=$encodedValue"  // ✅ 이게 맞음

        Log.d("FACILITY_API", "🔗 요청 URL: $url")


        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("응답 실패: ${response.code} ${response.message}")
            }
            response.body?.string() ?: throw Exception("응답 본문 없음")
        }
    }

}

fun main8() = runBlocking {
    val facilityId = "4423010200-1-01840025" // 명세서에 나오는 테스트 ID
    try {
        val xml = FacilityApi.fetchEvalInfoByFacilityId("wfcltId",facilityId)
        println("xml id :$facilityId ")
        println("✅ XML 응답 데이터:\n")
        println(xml)
    } catch (e: Exception) {
        println("❌ 오류 발생: ${e.localizedMessage}")
    }
}