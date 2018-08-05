import spark.Spark.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import data.ojmapper.ImageLink


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
            val adapter2 = moshi.adapter(data.ojmapper.ImageLink::class.java)
            var imageLinks = mutableListOf<ImageLink>()
            fromJson.let {

                it?.items?.forEach { item ->

                    imageLinks.add(item.volumeInfo.imageLinks)
                }
            }
            val type = Types.newParameterizedType(List::class.java, ImageLink::class.java)
            val listAdapter: JsonAdapter<List<ImageLink>> = moshi.adapter(type)
            val multiImageLinkJson = listAdapter.toJson(imageLinks)
            multiImageLinkJson
        }
    }
}