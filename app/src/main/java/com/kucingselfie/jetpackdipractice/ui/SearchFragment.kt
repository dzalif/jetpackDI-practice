package com.kucingselfie.jetpackdipractice.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kucingselfie.jetpackdipractice.AppExecutors

import com.kucingselfie.jetpackdipractice.R
import com.kucingselfie.jetpackdipractice.di.Injectable
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */


class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


}
