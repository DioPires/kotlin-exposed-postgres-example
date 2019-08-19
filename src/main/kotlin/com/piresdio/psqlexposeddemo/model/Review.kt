package com.piresdio.psqlexposeddemo.model

import org.jetbrains.exposed.sql.Table
import java.util.*

object Reviews: Table() {
    val id = varchar("id", 36).primaryKey().default(UUID.randomUUID().toString())
    val title = varchar("name", length = 10)
    val text = text("text")
    val rating = integer("rating")
    val snackId = varchar("snacks_id", 36).references(Snacks.id)
}

data class Review (
    val id: String,
    val title: String,
    val text: String,
    val rating: Int,
    val snackId: String
)
