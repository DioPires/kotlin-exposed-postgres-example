package com.piresdio.psqlexposeddemo.repositories

import com.piresdio.psqlexposeddemo.model.Review
import com.piresdio.psqlexposeddemo.model.Reviews
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*

class ReviewRepository(
    dbUrl: String,
    driver: String,
    user: String,
    password: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        Database.connect(url = dbUrl, driver = driver, user = user, password = password)
        transaction {
            SchemaUtils.create(Reviews)
        }
    }

    private fun toReview(resultRow: ResultRow): Review {
        return Review(
            id = resultRow[Reviews.id],
            title = resultRow[Reviews.title],
            text = resultRow[Reviews.text],
            rating = resultRow[Reviews.rating],
            snackId = resultRow[Reviews.snackId]
        )
    }

    fun select(reviewId: String): Review? {
        return transaction {
            Reviews.select { Reviews.id eq reviewId }.mapNotNull { toReview(it) }.singleOrNull()
        }
    }

    fun add(reviewTitle: String, reviewText: String, reviewRating: Int, reviewSnackId: String): String {
        return transaction {
            Reviews.insert {
                it[id] = UUID.randomUUID().toString()
                it[title] = reviewTitle
                it[text] = reviewText
                it[rating] = reviewRating
                it[snackId] = reviewSnackId
            } get Reviews.id
        }
    }

    fun update(reviewId: String, reviewTitle: String? = null, reviewText: String? = null, reviewRating: Int? = null, reviewSnackId: String? = null): String {
        val existingResult = transaction {
            Reviews
                .select { Reviews.id eq reviewId }
                .map { toReview(it) }
                .firstOrNull()
        } ?: return "Review with ID '$reviewId' does not exist!"

        logger.warn(existingResult.toString())

        transaction {
            Reviews.update({ Reviews.id eq reviewId }) {
                it[title] = reviewTitle ?: existingResult.title
                it[text] = reviewText ?: existingResult.text
                it[rating] = reviewRating ?: existingResult.rating
                it[snackId] = reviewSnackId ?: existingResult.snackId
            }
        }
        return reviewId
    }

    fun delete(reviewId: String): Boolean {
        transaction {
            Reviews
                .select { Reviews.id eq reviewId }
                .map { it }
                .firstOrNull()
        } ?: return false

        val deletedCount = transaction {
            Reviews.deleteWhere {
                Reviews.id eq reviewId
            }
        }
        return when(deletedCount) {
            0 -> false
            else -> true
        }
    }
}
