import spark.Spark.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import data.ojmapper.Content


class ThumbnailController {

    constructor() {
        get("/thumbnail") { req, _ ->
            val reqParams = req.queryParams("name")
            println(req.queryParams("name"))
            val requestURL = "https://www.googleapis.com/books/v1/volumes?q=${reqParams}&country=JP&maxResults=40".httpGet().response()
            val json = String(requestURL.second.data)
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(data.ojmapper.JsonData::class.java)
            val fromJson = adapter.fromJson(json)
            var imageLinks = mutableListOf<Content>()
            fromJson.let {
                    it?.items?.forEach { item ->
                        val volumeInfo = item.volumeInfo
                        val subTitle = volumeInfo.subtitle?.let { it } ?: ""
                        val title = "${volumeInfo.title} ${subTitle}"
                        val imageLink = volumeInfo.imageLinks?.let { it.smallThumbnail } ?: "../image/noimage.png"
                        val isbnCode = volumeInfo.industryIdentifiers?.let { it[0].identifier } ?: ""
                        val content = Content(title, imageLink, isbnCode)
                        imageLinks.add(content)
                    }
            }
            val type = Types.newParameterizedType(List::class.java, Content::class.java)
            val listAdapter: JsonAdapter<List<Content>> = moshi.adapter(type)
            val multiImageLinkJson = listAdapter.toJson(imageLinks)
            multiImageLinkJson
        }
    }
}