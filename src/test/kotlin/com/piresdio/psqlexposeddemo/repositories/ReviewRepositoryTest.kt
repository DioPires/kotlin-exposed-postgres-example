package com.piresdio.psqlexposeddemo.repositories

import com.piresdio.psqlexposeddemo.config.DBConfig
import com.piresdio.psqlexposeddemo.model.Review
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class ReviewRepositoryTest {
    private val propertiesFile = ReviewRepositoryTest::class.java.getResource("/application.properties").path
    private val dbConfig = DBConfig(propertiesFile)
    private var reviewRepository = ReviewRepository(
        dbUrl = dbConfig.dbUrl,
        user = dbConfig.dbUser,
        password = dbConfig.dbPassword,
        driver = dbConfig.dbDriver
    )
    private val reviewTitle = "the best"
    private val reviewText = "This is the best snack ever"
    private var reviewRating = (1..20).shuffled().first()
    private val reviewSnackId = "54f52260-0435-44f2-b474-3ede83177ad8"

    @Test
    fun `test insert`() {
        val reviewId = reviewRepository.add(
            reviewTitle = reviewTitle,
            reviewText = reviewText,
            reviewRating = reviewRating,
            reviewSnackId = reviewSnackId
        )

        val review: Review = reviewRepository.select(reviewId)!!
        Assertions.assertAll(
            Executable { Assertions.assertEquals(review.id, reviewId) },
            Executable { Assertions.assertEquals(review.title, reviewTitle) },
            Executable { Assertions.assertEquals(review.text, reviewText) },
            Executable { Assertions.assertEquals(review.rating, reviewRating) },
            Executable { Assertions.assertEquals(review.snackId, reviewSnackId) }
        )
    }

    @Test
    fun `test update`() {
        val newRating = 4
        val newText = "This is good but not the best"

        var reviewId = reviewRepository.add(
            reviewTitle = reviewTitle,
            reviewText = reviewText,
            reviewRating = reviewRating,
            reviewSnackId = reviewSnackId
        )

        reviewId = reviewRepository.update(
            reviewId = reviewId,
            reviewText = newText,
            reviewRating = newRating
        )

        val review = reviewRepository.select(reviewId)!!
        Assertions.assertAll(
            Executable { Assertions.assertEquals(review.text, newText) },
            Executable { Assertions.assertEquals(review.rating, newRating) }
        )
    }

    @Test
    fun `test delete`() {
        val reviewId = reviewRepository.add(
            reviewTitle = reviewTitle,
            reviewText = reviewText,
            reviewRating = reviewRating,
            reviewSnackId = reviewSnackId
        )

        val deleted = reviewRepository.delete(reviewId)
        Assertions.assertTrue(deleted)
    }
}