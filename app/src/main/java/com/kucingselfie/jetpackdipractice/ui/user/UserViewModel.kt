package com.kucingselfie.jetpackdipractice.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kucingselfie.jetpackdipractice.repository.RepoRepository
import com.kucingselfie.jetpackdipractice.repository.UserRepository
import com.kucingselfie.jetpackdipractice.util.AbsentLiveData
import com.kucingselfie.jetpackdipractice.vo.Repo
import com.kucingselfie.jetpackdipractice.vo.Resource
import com.kucingselfie.jetpackdipractice.vo.User
import javax.inject.Inject

//TODO: Add @openfortesting
class UserViewModel @Inject constructor(userRepository: UserRepository, repoRepository: RepoRepository) : ViewModel() {

    private val _login = MutableLiveData<String>()
    val login: LiveData<String>
        get() = _login

    val user: LiveData<Resource<User>> = Transformations.switchMap(_login) {
        login ->
        if (login == null) {
            AbsentLiveData.create()
        } else {
            userRepository.loadUser(login)
        }
    }

    val repositories: LiveData<Resource<List<Repo>>> = Transformations
        .switchMap(_login) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repoRepository.loadRepos(login)
            }
        }

    fun retry() {
        _login.value?.let {
            _login.value = it
        }
    }

    fun setLogin(login: String) {
        if (_login.value != login) {
            _login.value = login
        }
    }
}
