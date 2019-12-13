package com.kucingselfie.jetpackdipractice.testing

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.kucingselfie.jetpackdipractice.R

class SingleFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = FragmentContainerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            id = R.id.container
        }
        setContentView(content)
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment, "TEST")
            .commit()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}