package nl.hva.tangocity

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.roundToInt

fun Fragment.toast (text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun Calendar.resetTime () {
    this.set(Calendar.MILLISECOND, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.HOUR_OF_DAY, 0)

}