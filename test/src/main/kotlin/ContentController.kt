import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.squareup.moshi.Moshi
import dao.Content
import dao.Contents
import dao.Publisher
import dao.Publishers
import spark.Spark.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import java.sql.Connection

class ContentController {


    constructor() {
        try {
            Database.connect("jdbc:sqlite:./test.sqlite", "org.sqlite.JDBC")
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
        edit();
    }

    fun create() {
        val app = Application()
        get("/create") { _, _ ->
            app.render("content.vm")
        }
    }

    fun contents() {
        get("/contents") {_,_->
            var jsonString = ""
            transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                val mapper = jacksonObjectMapper()
                var list = mutableListOf<data.Item>()
                Content.all().forEach {
                    val content = data.Item(
                            content = data.Content(
                            id = it.id.toString(),
                            title = it.title,
                            imageLink =  it.imageLink,
                            isbnCode =  it.isbnCode,
                            publisher = it.publisher.name)

                    )
                    list.add(content)
                }
                jsonString = mapper.writeValueAsString(list)
            }
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
                }?:  throw Exception()
            }
        }
    }
}