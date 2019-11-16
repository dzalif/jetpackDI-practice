package com.kucingselfie.jetpackdipractice.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kucingselfie.jetpackdipractice.AppExecutors
import com.kucingselfie.jetpackdipractice.api.ApiResponse
import com.kucingselfie.jetpackdipractice.api.GithubService
import com.kucingselfie.jetpackdipractice.api.RepoSearchResponse
import com.kucingselfie.jetpackdipractice.db.GithubDb
import com.kucingselfie.jetpackdipractice.db.RepoDao
import com.kucingselfie.jetpackdipractice.util.AbsentLiveData
import com.kucingselfie.jetpackdipractice.util.RateLimiter
import com.kucingselfie.jetpackdipractice.vo.Repo
import com.kucingselfie.jetpackdipractice.vo.RepoSearchResult
import com.kucingselfie.jetpackdipractice.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: GithubDb,
    private val repoDao: RepoDao,
    private val githubService: GithubService
) {
    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            override fun saveCallResult(item: List<Repo>) {
                repoDao.insertRepos(item)
            }

            override fun loadFromDb() = repoDao.loadRepositories(owner)

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner)
            }

            override fun createCall(): LiveData<ApiResponse<List<Repo>>> = githubService.getRepos(owner)

            override fun onFetchFailed() {
                repoListRateLimit.reset(owner)
            }
        }.asLiveData()
    }

    fun search(query: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {
            override fun saveCallResult(item: RepoSearchResponse) {
                val repoIds = item.items.map { it.id }
                val repoSearchResult = RepoSearchResult(
                    query = query,
                    repoIds = repoIds,
                    totalCount = item.total,
                    next = item.nextPage
                )
                db.runInTransaction {
                    repoDao.insertRepos(item.items)
                    repoDao.insert(repoSearchResult)
                }
            }

            override fun loadFromDb(): LiveData<List<Repo>> {
                return Transformations.switchMap(repoDao.search(query)) {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else {
                        repoDao.loadOrdered(it.repoIds)
                    }
                }
            }

            override fun shouldFetch(data: List<Repo>?) = data == null

            override fun createCall(): LiveData<ApiResponse<RepoSearchResponse>> = githubService.searchRepos(query)
        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>>? {
        val fetchnextSearchPageTask = FetchNextPageSearchTask(
            query = query,
            githubService = githubService,
            db = db
        )
        appExecutors.networkIO().execute(fetchnextSearchPageTask)
        return fetchnextSearchPageTask.liveData
    }

}