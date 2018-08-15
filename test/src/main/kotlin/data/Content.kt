package data

import dao.Content

data class Content(val id: String, val title: String, val imageLink: String, val isbnCode: String, val publisher: String) {

    constructor(content: Content) :
            this(
            content.id.toString(),
            content.title,
            content.imageLink,
            content.isbnCode,
            content.publisher.name)
}