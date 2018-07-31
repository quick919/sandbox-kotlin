package dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Content(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Content>(Contents)

    var name by Contents.name
    var publisher by Publisher referencedOn Contents.publisher
}