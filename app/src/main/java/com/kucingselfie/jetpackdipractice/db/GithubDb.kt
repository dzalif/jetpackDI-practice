package com.kucingselfie.jetpackdipractice.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kucingselfie.jetpackdipractice.vo.Contributor
import com.kucingselfie.jetpackdipractice.vo.Repo
import com.kucingselfie.jetpackdipractice.vo.RepoSearchResult
import com.kucingselfie.jetpackdipractice.vo.User

@Database(
    entities = [
    User::class,
    Repo::class,
    Contributor::class,
    RepoSearchResult::class],
    version = 3,
    exportSchema = false
)

abstract class GithubDb : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun repoDao(): RepoDao
}