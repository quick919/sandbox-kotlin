fun main(args: Array<String>) {
    val application = Application(8080)
    application.ignite()
    ContentController()
    ThumbnailController()
}