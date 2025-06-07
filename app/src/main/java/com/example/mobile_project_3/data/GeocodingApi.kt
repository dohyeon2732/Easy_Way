package com.example.mobile_project_3.data
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

object GeocodingApi {

    private val client = OkHttpClient()
    private const val clientId = "MY_KEY" // 네이버 콘솔에서 발급받은 Client ID
    private const val clientSecret = "SECRET" // 네이버 콘솔에서 발급받은 Client Secret

    suspend fun geocodeAddress(address: String): LatLng? {
        val encoded = URLEncoder.encode(address, "UTF-8")
        val url =
            "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$encoded"

        val request = Request.Builder()
            .url(url)
            .header("X-NCP-APIGW-API-KEY-ID", clientId)
            .header("X-NCP-APIGW-API-KEY", clientSecret)
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (body == null) {
                    android.util.Log.e("GEOCODE_RES", "❌ 응답 body가 null입니다")
                    return@withContext null
                }

                android.util.Log.d("GEOCODE_RES", "🔄 주소: $address")
                android.util.Log.d("GEOCODE_RES", "📦 응답 바디: $body")

                val json = JSONObject(body)

                // 에러 메시지 체크
                if (json.has("error")) {
                    val message = json.optString("errorMessage", "에러 메시지 없음")
                    android.util.Log.e("    private const val clientId = \"GeocodingApi\" // 네이버 콘솔에서 발급받은 Client ID\n" +
                            "    private const val clientSecret = \"0zxM0GjHyoRdxlaNfBfNatb5EMfjbFcTlWAgiLFp\" // 네이버 콘솔에서 발급받은 Client Secret\n", "❌ 에러 발생: $message")
                    return@withContext null
                }

                val addresses = json.optJSONArray("addresses") ?: return@withContext null
                if (addresses.length() > 0) {
                    val first = addresses.getJSONObject(0)
                    val lat = first.getDouble("y")
                    val lng = first.getDouble("x")
                    LatLng(lat, lng)
                } else {
                    android.util.Log.w("GEOCODE_RES", "⚠️ 변환된 주소 없음: $address")
                    null
                }
            }
        }
    }


}
