package com.kucingselfie.jetpackdipractice.ui.repo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentRepoBinding>()

    private val params by navArgs<RepoFragmentArgs>()
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
                repoViewModel.retry()
            }
        }
        binding = dataBinding
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = RepoFragmentArgs.fromBundle(arguments!!)
        repoViewModel.setId(params.owner, params.name)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.repo = repoViewModel.repo

        val adapter = ContributorAdapter(dataBindingComponent, appExecutors) {
            contributor, imageView ->
            val extras = FragmentNavigatorExtras(imageView to contributor.login)
//            navController().navigate(
//                RepoFragmentDirections.showUser(contributor.login, contributor.avatarUrl), extras
//            )
        }
        this.adapter = adapter
        binding.contributorList.adapter = adapter
        postponeEnterTransition()
        binding.contributorList.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
        initContributorList(repoViewModel)
    }

    private fun initContributorList(vm: RepoViewModel) {
        vm.contributors.observe(viewLifecycleOwner, Observer { listResource ->
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
