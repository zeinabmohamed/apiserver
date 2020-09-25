package com.ktorapidemo.tables

import org.jetbrains.exposed.sql.Table

object Products : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val title = varchar("title", 100)
    val description = varchar("description", 500)
    val price = integer("price")
}