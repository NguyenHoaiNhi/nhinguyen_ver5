package com.nhinguyen.translate.Fragment

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nhinguyen.translate.DATABASE_NAME
import com.nhinguyen.translate.R
import com.nhinguyen.translate.ROOM.*
import com.nhinguyen.translate.WORD_KEY
import kotlinx.android.synthetic.main.fragment_favorite.*
import java.util.*
import kotlin.concurrent.schedule

//import android.R



class FavoriteFragment: Fragment() {

    var words: ArrayList<Word> = ArrayList()
    lateinit var wordAdapter: WordAdapter
    lateinit var dao_favorite: WordDAO


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_favorite, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         initRoomDatabase()

         setupRecycleView()

         getWords()
    }


    private fun initRoomDatabase() {
        val db_favorite = Room.databaseBuilder(
            getContext()!!
            , AppDatabase::class.java, DATABASE_NAME).allowMainThreadQueries()
            .build()
        dao_favorite = db_favorite.wordDAO()
    }

    private fun setupRecycleView() {
        rvFavorite.layoutManager = LinearLayoutManager(getContext()) as RecyclerView.LayoutManager?

        wordAdapter = WordAdapter(words, getContext()!!)

        rvFavorite.adapter = wordAdapter

        wordAdapter.setListener(wordItemClickListener)
    }

    private fun getWords()
    {
        var words = dao_favorite.getAll()
        Log.i("Get word: ", words.toString())
        this.words.addAll(words)
        wordAdapter.notifyDataSetChanged()

    }

    private val wordItemClickListener = object : WordItemClickListener {

        override fun onTrashIconClicked(position: Int) {
            dao_favorite.delete(words[position])
            wordAdapter.removeItem(words[position], position)
            wordAdapter.notifyDataSetChanged()
        }
    }


}