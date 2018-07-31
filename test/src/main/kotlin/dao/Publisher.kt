package dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Publisher(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<Publisher>(Publishers)

    var name by Publishers.name

}