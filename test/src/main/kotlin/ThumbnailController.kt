import spark.Spark.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi


class ThumbnailController {

    constructor() {
        get("/thumbnail") { _, _ ->
            val requestURL = "https://www.googleapis.com/books/v1/volumes?q=kotlin&country=JP&maxResults=40"
            requestURL.httpGet().responseString { request, response, result ->
                 when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                    }
                    is Result.Success -> {
                        val json = result.get()
                        try {
                            val moshi = Moshi.Builder().build()
                            val adapter = moshi.adapter(data.ojmapper.JsonData::class.java)
                            val fromJson = adapter.fromJson(json)
                            fromJson.let {
                                it?.items?.forEach { a -> println(a.volumeInfo.imageLinks) }
                            }
                        }catch (e: Exception) {
                            println(e)
                        }
                    }
                }
            }
        }
    }
}