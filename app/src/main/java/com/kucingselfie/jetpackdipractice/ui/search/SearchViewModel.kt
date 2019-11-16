package com.kucingselfie.jetpackdipractice.ui.search

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.kucingselfie.jetpackdipractice.repository.RepoRepository
import com.kucingselfie.jetpackdipractice.util.AbsentLiveData
import com.kucingselfie.jetpackdipractice.vo.Repo
import com.kucingselfie.jetpackdipractice.vo.Resource
import com.kucingselfie.jetpackdipractice.vo.Status
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(repoRepository: RepoRepository) : ViewModel() {

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> = _query

    private val nextPageHandler = NextPageHandler(repoRepository)

    val results: LiveData<Resource<List<Repo>>> = Transformations.switchMap(_query) {
        if (it.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            repoRepository.search(it)
        }
    }

    val loadMoreStatus: LiveData<LoadMoreState> get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        _query.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.queryNextPage(it)
            }
        }
    }

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == _query.value) {
            return
        }
        nextPageHandler.reset()
        _query.value = input
    }

    fun refresh() {
        _query.value?.let {
            _query.value = it
        }
    }
}

class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
    private var handleError = false
    val errorMessageIfNotHandled: String?
        get() {
            if (handleError) return null
            handleError = true
            return errorMessage
        }
}

class NextPageHandler(private val repository: RepoRepository) : Observer<Resource<Boolean>> {
    private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
    val loadMoreState = MutableLiveData<LoadMoreState>()
    private var query: String? = null
    private var _hasMore: Boolean = false
    val hasMore get() = _hasMore

    init {
        reset()
    }

    fun queryNextPage(query: String) {
        if (this.query == query) return
        unregister()
        this.query = query
        nextPageLiveData = repository.searchNextPage(query)
        loadMoreState.value = LoadMoreState(
            isRunning = true,
            errorMessage = null
        )
    }

    override fun onChanged(result: Resource<Boolean>?) {
        if (result == null) reset()
        else {
            when(result.status) {
                Status.SUCCESS -> {
                    _hasMore = result.data == true
                    unregister()
                    loadMoreState.setValue(LoadMoreState(isRunning = false, errorMessage = null))
                }
                Status.ERROR -> {
                    _hasMore = true
                    unregister()
                    loadMoreState.setValue(LoadMoreState(isRunning = false, errorMessage = result.message))
                }
                Status.LOADING -> { //ignore
                    //ignore
                }
            }
        }
    }

    private fun unregister() {
        nextPageLiveData?.removeObserver(this)
        nextPageLiveData = null
        if (_hasMore) {
            query = null
        }
    }

    fun reset() {
        unregister()
        _hasMore = true
        loadMoreState.value = LoadMoreState(
            isRunning = false,
            errorMessage = null
        )
    }
}

