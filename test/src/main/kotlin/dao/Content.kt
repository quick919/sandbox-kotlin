package dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Content(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Content>(Contents)

    var title by Contents.title
    var imageLink by Contents.imageLink
    var isbnCode by Contents.isbnCode
    var done by Contents.done
    var publisher by Publisher referencedOn Contents.publisher
}