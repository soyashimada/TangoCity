package nl.hva.tangocity

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

fun getColor(context: Context, color: Int, percent: Int? = null): String{
    return ("#" + percentToHex(percent) + Integer.toHexString(
        ContextCompat.getColor(
            context, color
        ) and 0x00ffffff).uppercase(Locale.getDefault()))
}

private fun percentToHex(percent: Int? = null): String{
    if (percent == null) {
        return ""
    }
    return (255.0 * (percent / 100.0)).roundToInt().toString( 16 )
        .uppercase(Locale.getDefault()).padStart(2, '0')
}