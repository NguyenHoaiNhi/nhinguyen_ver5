package com.nhinguyen.translate

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.nhinguyen.translate.Fragment.FavoriteFragment
import com.nhinguyen.translate.Fragment.FoldersFragment

class TabSlideAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FavoriteFragment()
            }
            else -> {
                return FoldersFragment()
            }
        }
    }

    override fun getCount(): Int { // tra ve so luong cac view co san
        //TODO:
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Favorite"
            else -> {
                return "Folders"
            }
        }
    }
}