package nl.hva.tangocity.ui.home.review.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nl.hva.tangocity.R
import nl.hva.tangocity.convertToDateText
import nl.hva.tangocity.databinding.ItemEditDeckBinding
import nl.hva.tangocity.model.Card
import java.util.*

class CardListAdapter(private val cards: ArrayList<Card>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<CardListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val binding = ItemEditDeckBinding.bind(itemView)
        fun databind(position: Int, clickListener: (Int) -> Unit) {
            val card = cards[position]
            binding.deckQuestion.text = card.question
            binding.deckAnswer.text = card.answer
            binding.deckDate.text = card.nextDate.convertToDateText()

            binding.cardListItem.setOnClickListener{
                clickListener(position)
            }
        }
    }

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_deck, parent, false)
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return cards.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: CardListAdapter.ViewHolder, position: Int) {
        holder.databind(position, clickListener)
    }

}