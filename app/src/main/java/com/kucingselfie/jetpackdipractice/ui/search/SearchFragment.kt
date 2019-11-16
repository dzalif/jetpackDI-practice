package com.kucingselfie.jetpackdipractice.ui.search


import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kucingselfie.jetpackdipractice.AppExecutors

import com.kucingselfie.jetpackdipractice.R
import com.kucingselfie.jetpackdipractice.binding.FragmentDataBindingComponent
import com.kucingselfie.jetpackdipractice.databinding.FragmentSearchBinding
import com.kucingselfie.jetpackdipractice.di.Injectable
import com.kucingselfie.jetpackdipractice.ui.common.RepoListAdapter
import com.kucingselfie.jetpackdipractice.ui.common.RetryCallback
import com.kucingselfie.jetpackdipractice.util.autoCleared
import javax.inject.Inject

class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentSearchBinding>()

    var adapter by autoCleared<RepoListAdapter>()

    val searchViewModel: SearchViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false,
            dataBindingComponent
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        initRecyclerView()
        val rvAdapter = RepoListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            showFullname = true
        ) {
//            navController().navigate(
////                SearchFragmentDirections.showRepo(it.owner.login, it.name)
//            )
        }
        binding.query = searchViewModel.query
        binding.repoList.adapter = rvAdapter
        adapter = rvAdapter

        initSearchInputListener()

        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }
    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(view: View) {
        val query = binding.input.text.toString()
        // Dismiss keyboard
        dismissKeyboard(view.windowToken)
        searchViewModel.setQuery(query)
    }

    private fun initRecyclerView() {
        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })

        binding.searchResult = searchViewModel.results
        searchViewModel.results.observe(viewLifecycleOwner, Observer { result ->
            adapter.submitList(result?.data)
        })

        searchViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer {
            if(it == null) binding.loadingMore = false
            else {
                binding.loadingMore = it.isRunning
                val error = it.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
