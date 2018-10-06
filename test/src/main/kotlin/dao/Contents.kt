package dao

import org.jetbrains.exposed.dao.IntIdTable

object Contents : IntIdTable() {
    val title = varchar("title", 100).index()
    val imageLink = varchar("imageLink", 1000)
    val isbnCode = varchar("isbnCode", 100)
    val done = integer("done").default(defaultValue = 0)
    val publisher = reference("publisher", Publishers)
}