import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.ModelAndView
import spark.Spark.*
import spark.TemplateEngine
import spark.template.velocity.VelocityTemplateEngine

val logger: Logger = LoggerFactory.getLogger("main")

class Application(val port: Int = 8080) {

    private var ignited = false

    val renderEngine: TemplateEngine
        get() = VelocityTemplateEngine()

    fun ignite() {
        staticFileLocation("/assets")

        port(port)

        get("/") { _, _ ->
            render("index.vm")
        }

        exception(Exception::class.java) { exc, _, res ->
            logger.error(exc.message)
            res.body(exc.message)
        }

        awaitInitialization()
        ignited = true
    }

    fun extinguish() {
        if(ignited) stop()
    }

    fun render(templateName: String,
               model: Any = emptyMap<String, String>()) = renderEngine.render(ModelAndView(model, "templates/$templateName"))

}