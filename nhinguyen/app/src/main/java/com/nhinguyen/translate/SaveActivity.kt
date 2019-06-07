package com.nhinguyen.translate


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_save.*

class SaveActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)



        val fragmentAdapter = TabSlideAdapter(supportFragmentManager)
        viewpager.adapter = fragmentAdapter
        save_tab.setupWithViewPager(viewpager)

    }


}