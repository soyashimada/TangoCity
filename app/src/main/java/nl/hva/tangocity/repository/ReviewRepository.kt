package nl.hva.tangocity.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import nl.hva.tangocity.model.Review
import nl.hva.tangocity.resetTime
import java.util.*
import kotlin.collections.ArrayList

class ReviewRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var reviewDocument =
        firestore.collection("Reviews")

    private val _reviews: MutableLiveData<ArrayList<Review>> = MutableLiveData()
    val reviews: LiveData<ArrayList<Review>>
        get() = _reviews

    private val lastReviewId: MutableLiveData<Int> = MutableLiveData(1)

    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val createSuccess: LiveData<Boolean>
        get() = _createSuccess

    suspend fun initialize() {
        try {
            withTimeout(5_000) {
                val reviewData = reviewDocument
                    .get()
                    .await()

                val reviewList = arrayListOf<Review>()

                reviewData.forEach {
                    val id = it.id.toInt()
                    val deckId = it.get("deck_id").toString()
                    val cardId = it.get("card_id").toString()
                    val result = it.get("result").toString()
                    val dateStamp = it.get("date") as Timestamp

                    val date: Calendar = Calendar.getInstance()
                    date.time = dateStamp.toDate()
                    date.resetTime()

                    if ((lastReviewId.value!!) < id){ lastReviewId.value = id }

                    reviewList.add(Review(id, deckId.toInt(), cardId.toInt(), result.toInt(), date))
                    reviewList.sortBy { review -> review.date }
                }
                _reviews.value = reviewList
            }
        } catch (e: Exception) {
            throw RetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }

    suspend fun createReview(deckId: Int, cardId: Int, result: Int, date: Timestamp, reviewId: Int? = null): Int{
        try {
            val id = reviewId ?: lastReviewId.value?.plus(1) ?: 1
            val review = hashMapOf(
                "deck_id" to deckId,
                "card_id" to cardId,
                "result" to result,
                "date" to date
            )

            withTimeout(5_000) {
                reviewDocument
                    .document(id.toString())
                    .set(review)
                    .await()

                _createSuccess.value = true
                lastReviewId.value = id
            }

            return id
        } catch (e: Exception) {
            throw SaveError(e.message.toString(), e)
        }
    }

    class SaveError(message: String, cause: Throwable) : Exception(message, cause)
    class RetrievalError(message: String) : Exception(message)
}