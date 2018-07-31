package dao

import org.jetbrains.exposed.dao.IntIdTable

object Contents : IntIdTable() {
    val name = varchar("name", 100).index()
    val publisher = reference("publisher", Publishers)
}