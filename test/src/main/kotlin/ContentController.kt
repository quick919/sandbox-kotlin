import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dao.Content
import dao.Contents
import dao.Publisher
import dao.Publishers
import spark.Spark.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.sql.Connection
import javax.servlet.MultipartConfigElement

class ContentController {


    constructor() {
        try {
            Database.connect("jdbc:sqlite:./test2.sqlite", "org.sqlite.JDBC")
            transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                val c = create(Publishers, Contents)
                if (Publisher.all().empty()) {
                    mutableListOf("Amazon", "O'Reilly", "gihyo", "Leanpub").forEach { pub ->
                        Publishers.insert {
                            it[name] = pub
                        }
                    }
                }
            }

        } catch (e: Exception) {
            print(e)
        }
        create()
        contents()
        content()
        edit()
        delete()
        search()
        output()
        upload()
        done()
    }

    fun create() {
        val app = Application()
        get("/create") { _, _ ->
            app.render("content.vm")
        }
    }

    fun contents() {
        get("/contents") {req,_->
            var jsonString = outputJsonFromPublisher(req.queryParams("publisher"), req.queryParams("readingState"))
            jsonString
        }
    }

    fun content() {
        post("/content") {req,res ->
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(data.Item::class.java)
            val obj = adapter.fromJson(req.body())
            transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                try {
                    val pub = Publisher.find { Publishers.name eq obj?.content?.publisher }.first()
                    Content.new {
                        obj?.let {
                            title = it.content.title
                            imageLink = it.content.imageLink
                            isbnCode = it.content.isbnCode
                            publisher = pub
                        }
                    }
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
    }

    fun edit() {
        post("/edit") {req, res ->
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(data.Content::class.java)
            val obj = adapter.fromJson(req.body())
            transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                obj?.let {
                    val content = Content.findById(it.id.toInt())
                    content?.title = it.title
                    content?.publisher = Publisher.find { Publishers.name eq it.publisher }.first()
                }?:  throw Exception()
            }
        }
    }

    fun delete() {
        post("/delete") {req, res ->
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(data.Content::class.java)
            val obj = adapter.fromJson(req.body())
            transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                obj?.let {
                    Contents.deleteWhere { Contents.id eq obj?.let { it.id.toInt() } }
                }?:  throw Exception()
            }
        }
    }

    fun search() {
        get("/search") { req, res ->
            var jsonString = outputJsonFromSearchTitle(req.queryParams("searchTitle"))
            jsonString
        }
    }

    fun output() {
        get("/output") { req, res ->
            val searchTitle = req.queryParams("searchTitle")
            val readingState = req.queryParams("readingState")
            if (searchTitle.isEmpty()) {
                val publisher =req.queryParams("publisher")
                outputJsonFromPublisher(publisher, readingState)
            } else {
                outputJsonFromSearchTitle(searchTitle)
            }
        }
    }

    fun upload() {
        post("/upload") { req, res ->
            val multipartConfigElement = MultipartConfigElement(System.getProperty("java.io.tmpdir"))
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement)
            req.raw().parts.forEach {
                println(it.contentType)
                println(it.size)
                println(it.headerNames)
            }
            val inputStream  =req.raw().getPart("file").inputStream
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, data.Content::class.java)
            val adapter:JsonAdapter<List<data.Content>> = moshi.adapter(type)

            inputStream.bufferedReader().use {
                val contentList = adapter.fromJson(it.readText())
                contentList?.forEach {
                    transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                        try {
                            if(Content.find { Contents.id eq it.id }.empty())  {
                                val pub = Publisher.find { Publishers.name eq it.publisher }.first()
                                Content.new {
                                    it.let {
                                        title = it.title
                                        imageLink = it.imageLink
                                        isbnCode = it.isbnCode
                                        publisher = pub
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            println(e)
                        }
                    }
                }
            }
        }
    }

    fun done() {
        post("done") { req, res ->
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, data.Content::class.java)
            val listAdapter: JsonAdapter<List<data.Content>> = moshi.adapter(type)
            val contents = listAdapter.fromJson(req.body())
            transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                contents?.forEach {
                    //Contentsにカラム追加
                    var content = Content.find { Contents.id eq it.id }.first()
                    content.done = 1
                }
            }
        }
    }


    fun outputJsonFromSearchTitle(searchTitle: String): String {
        var json = ""
        transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
            //                logger.addLogger(StdOutSqlLogger)
            var list = mutableListOf<data.Content>()
            Content.find { Contents.title like "%"+ "${searchTitle}"+"%" }.sortedBy { it.title }.forEach {
                val content = data.Content(it)
                list.add(content)
            }
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, data.Content::class.java)
            val listAdapter: JsonAdapter<List<data.Content>> = moshi.adapter(type)
            json = listAdapter.toJson(list)
        }
        return json
    }

    fun  outputJsonFromPublisher(publisher: String, readingstate: String): String {
        var json = ""
        transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
            //                logger.addLogger(StdOutSqlLogger)
            var list = mutableListOf<data.Content>()

            when(publisher) {
                "all" -> {
                    Content.find { Contents.done eq readingstate.toInt() }.sortedBy { it.title }.forEach {
                        val content = data.Content(it)
                        list.add(content)
                    }
                }
                else -> {
                    val pub = Publisher.find { Publishers.name eq publisher }.first()
                    Content.find { Contents.publisher eq pub.id and (Contents.done eq readingstate.toInt()) }.sortedBy { it.title }.forEach {
                        val content = data.Content(it)
                        list.add(content)
                    }
                }
            }
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, data.Content::class.java)
            val listAdapter: JsonAdapter<List<data.Content>> = moshi.adapter(type)
            json = listAdapter.toJson(list)
        }
        return json
    }
}