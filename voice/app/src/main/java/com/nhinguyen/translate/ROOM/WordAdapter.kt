package com.nhinguyen.translate.ROOM

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nhinguyen.translate.R
import kotlinx.android.synthetic.main.word_item.view.*
import java.util.*

class WordAdapter(var items: ArrayList<Word>, val context: Context) : RecyclerView.Adapter<WordViewHolder>(){

    lateinit var mListener: WordItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WordViewHolder {
        return WordViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.word_item,
                p0,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: WordViewHolder, p1: Int) {
        p0.tvEnglish.text = items[p1].content_language1
        p0.tvVietnamese.text = items[p1].content_language2
        p0.language1.text = items[p1].language1
        p0.language2.text = items[p1].language2
        p0.btnRemove.setOnClickListener{
            mListener.onTrashIconClicked(p1)
        }
    }

    fun setListener(listener: WordItemClickListener) {
        this.mListener = listener
    }

    fun removeItem(userRemove: Word, i: Int){
        this.items.remove(userRemove)
        notifyItemRemoved(i)
    }

//    fun setData(items: ArrayList<Word>) {
//        this.items = items
//    }

//    fun appenData(word: Word) {
//        this.items.add(word)
//        notifyItemInserted(items.size - 1)
//    }
//    fun availableItem(word : Word):Boolean {
//        val size = this.items.size
//
//        if (size > 0)
//        {
//            for (i in 0 until size)
//            {
//                if(this.items[i].english == word.english && this.items[i].vietnamese == word.vietnamese)
//                {
//                    return false
//                }
//            }
//        }
//
//        return true
//    }

}

class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var tvEnglish = view.tvEnglish
    var tvVietnamese = view.tvVietnamese
    var btnRemove = view.btnRemove
    var language1 = view.language1
    var language2 = view.language2
}