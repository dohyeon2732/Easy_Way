import com.example.mobile_project_3.data.FacilityApi
import com.example.mobile_project_3.data.FacilityItem
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


fun parseFacilityXml2(xml: String): FacilityParseResult {
    val items = mutableListOf<FacilityItem>()
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val doc = builder.parse(ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8)))

    val servList = doc.getElementsByTagName("servList")
    for (i in 0 until servList.length) {
        val el = servList.item(i) as Element

        fun get(tag: String) = el.getElementsByTagName(tag).item(0)?.textContent ?: ""

        items.add(
            FacilityItem(
                name = get("faclNm"),
                type = get("faclTyCd"),
                latitude = get("faclLat"),
                longitude = get("faclLng"),
                address = get("lcMnad"),
                businessStatus = get("salStaNm"),
                estbDate = get("estbDate"),
                facilityId = get("faclInfId"),
                welfacilityId = get("wfcltId")
            )
        )
    }

    return FacilityParseResult(totalCount = items.size, items = items)
}

val facilityTypeNames = listOf(
    "격리병원", "경로당", "고등학교", "공공도서관", "공연장", "공장", "공중화장실", "관광숙박시설", "관람장", "교도소·구치소",
    "교육원(연수원등)·직업훈련소·학원(자동차학원, 무도학원 제외) 등", "국가 또는 지자체 청사", "국민건강보험공단 및 지사",
    "국민연금공단 및 지사", "근로복지공단 및 지사", "금융업소 등 일반업무시설", "기숙사", "노인복지시설", "다세대주택",
    "대피소", "대학교", "대형마트", "도매시정·소매시장", "도서관", "도시공원", "동식물원", "목욕장", "방송국",
    "병원·치과병원·한방병원·정신병원·요양병원", "보건소", "봉안당(종교시설에 해당하는 것은 제외)", "생활권수련시설",
    "생활숙박시설", "수영장", "수퍼마켓·일용품 등의 소매점", "아동복지시설", "아파트", "아파트 부대복리시설", "안마시술소",
    "야외음악당·야외극장·어린이회관", "어린이집", "연립주택", "우체국",
    "운동장(육상·구기·볼링·수영·스케이트·롤러스케이트·승마·사격·궁도·골프)과 부수되는 건축물", "운전학원", "유치원",
    "의원·치과의원·한의원·조산소·산후조리원", "이외 사회복지시설", "이용원·미용원", "일반숙박시설", "일반음식점", "자연공원",
    "자연권수련시설", "장례식장", "장애인복지시설", "전문대학", "전시장", "전신전화국", "종교집회장", "종합병원", "주차장",
    "중학교", "지역아동센터", "지역자치센터", "집회장", "체육관", "초등학교", "특수학교", "파출소, 지구대",
    "한국장애인고용공단 및 지사", "화장시설", "휴게소", "휴게음식점·제과점", "휴게음식점·제과점 등"
)

fun main7() = runBlocking {
    val allItems = mutableListOf<List<String>>()

    for (i in 1..74) {
        val typeCode = i.toString()
        val typeName = facilityTypeNames.getOrNull(i - 1) ?: "알 수 없음"

        println("[$i/74] 시설유형 코드 요청 중: $typeCode ($typeName)")
        try {
            val xml = FacilityApi.fetchByFacilityTypeRaw(typeName)
            println(xml)
            val result = parseFacilityXml2(xml)
            println(result)
            result.items.forEach { item ->
                println(" → ${item.name}, ${item.address}, ${item.latitude}, ${item.longitude}")
                allItems.add(
                    listOf(
                        item.name,
                        "$typeCode - $typeName",
                        item.latitude,
                        item.longitude,
                        item.address,
                        item.businessStatus,
                        item.estbDate,
                        item.facilityId,
                        item.welfacilityId
                    )
                )
            }

            println(" ✅ ${result.items.size}개 수집 완료\n")

        } catch (e: Exception) {
            println("⚠️ 오류 발생 (코드 $i): ${e.localizedMessage}\n")
        }
    }

    val csvHeader = listOf(
        "name", "type", "latitude", "longitude", "address",
        "businessStatus", "estbDate", "facilityId", "welfacilityId"
    )

    File("facility_all.csv").outputStream().bufferedWriter(Charsets.UTF_8).use { out ->
        out.write("\uFEFF")  // UTF-8 BOM 추가
        out.write(csvHeader.joinToString(",") + "\n")
        allItems.forEach { row ->
            out.write(row.joinToString(",") + "\n")
        }
    }
    println("✅ 모든 작업 완료: facility_all2.csv 로 저장됨")
}

