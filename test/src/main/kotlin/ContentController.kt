import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
                create(Publishers, Contents)
            }

        } catch (e: Exception) {
            print(e)
        }
        create()
        contents()
        content()
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
                var list = mutableListOf<data.Content>()
                Content.all().forEach {
                    val content = data.Content(
                            name = it.name,
                            publisher = it.publisher.name
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
            val mapper = jacksonObjectMapper()
            val content = mapper.readValue<data.Content>(req.body())
            transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 3) {
                val mapper = jacksonObjectMapper()
                var list = mutableListOf<data.Content>()
                Content.all().forEach {
                    try {
                        val content = data.Content(
                                name = it.name,
                                publisher = it.publisher.name
                        )
                        list.add(content)
                        val jsonString = mapper.writeValueAsString(content)

                    }catch (e: Exception) {
                        print(e)
                    }
                }
                try {
                    val jsonString = mapper.writeValueAsString(list)
                    print(jsonString)
                } catch (e: Exception) {
                    print(e.message)
                }


                val pub = Publisher.new {
                    name = content.publisher
                }
                Content.new {
                    name = content.name
                    publisher = pub
                }
            }
        }
    }


}