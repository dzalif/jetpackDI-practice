package com.kucingselfie.jetpackdipractice.ui.repo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.kucingselfie.jetpackdipractice.AppExecutors
import com.kucingselfie.jetpackdipractice.R
import com.kucingselfie.jetpackdipractice.binding.FragmentDataBindingComponent
import com.kucingselfie.jetpackdipractice.databinding.FragmentRepoBinding
import com.kucingselfie.jetpackdipractice.di.Injectable
import com.kucingselfie.jetpackdipractice.ui.common.RetryCallback
import com.kucingselfie.jetpackdipractice.util.autoCleared
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class RepoFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val repoViewModel: RepoViewModel by viewModels {
        viewModelFactory
    }

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentRepoBinding>()

    private var adapter by autoCleared<ContributorAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentRepoBinding>(
            inflater,
            R.layout.fragment_repo,
            container,
            false
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
//                repoViewModel.retry()
            }
        }
        binding = dataBinding
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        return dataBinding.root
    }


}
