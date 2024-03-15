package com.notsatria.githubusers.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.notsatria.githubusers.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userEntities: List<UserEntity>)

    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM user WHERE is_favorite = 0")
    fun searchUser(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM user WHERE login = :login")
    fun getUser(login: String): LiveData<UserEntity>

    @Query("SELECT * FROM user WHERE is_favorite = 1")
    fun getFavoriteUsers(): LiveData<List<UserEntity>>

    @Query("SELECT EXISTS(SELECT * FROM user WHERE login = :login AND is_favorite = 1)")
    fun isUserFavorite(login: String): Boolean

    @Update
    fun update(userEntity: UserEntity)

    @Query("UPDATE user SET followers = :followers, following = :following, name = :name, is_favorite = :isFavorite WHERE login = :login")
    fun updateDetailUser(login: String, followers: Int, following: Int, name: String, isFavorite: Boolean)

    @Query("DELETE FROM user WHERE is_favorite = 0")
    fun deleteAll()

    @Delete
    fun delete(userEntity: UserEntity)
}