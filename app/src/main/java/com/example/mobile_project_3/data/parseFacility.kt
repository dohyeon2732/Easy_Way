
import com.example.mobile_project_3.data.FacilityItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader


data class FacilityParseResult(
    val totalCount: Int,
    val items: List<FacilityItem>
)

fun parseFacilityXml(xml: String): FacilityParseResult {
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(StringReader(xml))

    val items = mutableListOf<FacilityItem>()
    var tag = ""
    var eventType = parser.eventType

    // totalCount는 XML상에 있어도 무시하고 item 수로 세기
    var name = ""
    var type = ""
    var latitude = ""
    var longitude = ""
    var address = ""
    var businessStatus = ""
    var estbDate = ""
    var facilityId = ""
    var welfacilityId = ""

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> tag = parser.name
            XmlPullParser.TEXT -> {
                val text = parser.text.trim()
                when (tag) {
                    "faclNm" -> name = text
                    "faclTyCd" -> type = text
                    "faclLat" -> latitude = text
                    "faclLng" -> longitude = text
                    "lcMnad" -> address = text
                    "salStaNm" -> businessStatus = text
                    "estbDate" -> estbDate = text
                    "faclInfId" -> facilityId = text
                    "wfcltId" -> welfacilityId = text
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "servList") {
                    items.add(
                        FacilityItem(
                            name, type, latitude, longitude, address,
                            businessStatus, estbDate, facilityId, welfacilityId
                        )
                    )
                    name = ""; type = ""; latitude = ""; longitude = ""
                    address = ""; businessStatus = ""; estbDate = ""
                    facilityId = ""; welfacilityId = ""
                }
                tag = ""
            }
        }
        eventType = parser.next()
    }

    return FacilityParseResult(totalCount = items.size, items = items)
}



