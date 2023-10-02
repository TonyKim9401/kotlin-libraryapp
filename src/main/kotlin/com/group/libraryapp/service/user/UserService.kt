package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.dto.user.response.UserResponse
import com.group.libraryapp.util.fail
import com.group.libraryapp.util.findByIdOrThrow
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository
){

    @Transactional
    fun saveUser(request: UserCreateRequest) {
        /**
         * 자바와는 다르게 default parameter 가 적용 된다
         */
        val newUser = User(request.name, request.age)
        userRepository.save(newUser)
    }

    @Transactional(readOnly = true)
    fun getUsers(): List<UserResponse> {
        return userRepository.findAll()
                .map { user -> UserResponse.of(user) } // 평범한 문법
//                .map { UserResponse(it) } // Kotlin 문법으로 같은 param 사용시 it으로 대체
//                .map(::UserResponse) // 각 객체를 바로 생성자에 넣어주기
    }

    @Transactional
    fun updateUserName(request: UserUpdateRequest) {
//        val user = userRepository.findByIdOrNull(request.id) ?: fail()// IllegalArgumentException::new
        val user = userRepository.findByIdOrThrow(request.id)
        user.updateName(request.name)
    }

    @Transactional
    fun deleteUser(name: String) {
        val user = userRepository.findByName(name) ?: fail() // IllegalArgumentException::new
        userRepository.delete(user)
    }

}