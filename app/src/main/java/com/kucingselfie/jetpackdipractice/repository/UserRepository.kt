package com.kucingselfie.jetpackdipractice.repository

import androidx.lifecycle.LiveData
import com.kucingselfie.jetpackdipractice.AppExecutors
import com.kucingselfie.jetpackdipractice.api.GithubService
import com.kucingselfie.jetpackdipractice.db.UserDao
import com.kucingselfie.jetpackdipractice.vo.Resource
import com.kucingselfie.jetpackdipractice.vo.User
import javax.inject.Inject
import javax.inject.Singleton

//TODO: Added @Openfortesting
@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubService: GithubService
) {
    fun loadUser(login: String) : LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                userDao.insert(item)
            }
            override fun shouldFetch(data: User?) = data == null
            override fun loadFromDb() = userDao.findByLogin(login)
            override fun createCall() = githubService.getUser(login)
        }.asLiveData()
    }
}