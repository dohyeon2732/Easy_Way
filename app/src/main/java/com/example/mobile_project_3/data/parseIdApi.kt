package com.example.mobile_project_3.data

import android.util.Log
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

data class FacilityEvalInfo(
    val name: String,
    val welfacilityId: String,
    val evalInfo: String
)

fun parseEvalXml(xml: String): FacilityEvalInfo {
    Log.d("PARSE_XML", "📥 XML 파싱 시작")

    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val doc = builder.parse(ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8)))

    val itemNode = doc.getElementsByTagName("servList").item(0)
        ?: throw IllegalStateException("❌ servList 태그를 찾을 수 없습니다. 응답 XML 일부: ${xml.take(200)}")

    Log.d("PARSE_XML", "✅ servList 태그 발견")

    val item = itemNode as? Element
        ?: throw IllegalStateException("❌ servList 태그가 Element 타입이 아닙니다.")

    fun get(tag: String): String {
        val value = item.getElementsByTagName(tag).item(0)?.textContent ?: ""
        Log.d("PARSE_XML", "📌 태그 <$tag>: '$value'")
        return value
    }

    val result = FacilityEvalInfo(
        name = get("faclNm"),
        welfacilityId = get("wfcltId"),
        evalInfo = get("evalInfo")
    )

    Log.d("PARSE_XML", "✅ FacilityEvalInfo 파싱 완료: $result")

    return result
}