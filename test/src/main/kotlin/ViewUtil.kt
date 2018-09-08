import spark.ModelAndView
import spark.Request
import spark.TemplateEngine
import spark.template.velocity.VelocityTemplateEngine

class ViewUtil {

    companion object {
        private val renderEngine: TemplateEngine
            get() = VelocityTemplateEngine()

        fun render(request: Request, model: Map<String, Any>, templatePath: String): String {
            return renderEngine.render(ModelAndView(model, templatePath))
        }
    }
}