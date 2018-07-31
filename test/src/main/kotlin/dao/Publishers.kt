package dao

import org.jetbrains.exposed.dao.IntIdTable

object Publishers : IntIdTable() {
    val name = varchar("name", 100).index()
}