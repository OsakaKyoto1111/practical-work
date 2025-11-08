package com.example.myapplication.data

import com.example.myapplication.data.local.UserDao
import com.example.myapplication.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(private val dao: UserDao) {
    val users: Flow<List<UserEntity>> = dao.observeAll()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val remote = RetrofitClient.api.getUsers()
        val entities = remote.map { dto ->
            UserEntity(
                id = dto.id,
                name = dto.name,
                username = dto.username,
                email = dto.email,
                phone = dto.phone,
                website = dto.website,
                city = dto.address?.city,
                company = dto.company?.name
            )
        }
        dao.clear()
        dao.insertAll(entities)
    }
}
