import spark.Spark.*
import util.Path

class LoginController {
    constructor(){
        get("/login") { req, res ->
            ViewUtil.render(req, mapOf(), Path.Template.LOGIN)
        }
    }
}