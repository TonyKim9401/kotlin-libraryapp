package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.dto.user.response.UserLoanHistoryResponse
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
        private val userRepository: UserRepository,
        private val userService: UserService,
        private val userLoanHistoryRepository: UserLoanHistoryRepository
) {

    @AfterEach
    fun clean() {
        println("클린 시작")
        userRepository.deleteAll()
    }

    @DisplayName("유저 저장이 정상 동작한다")
    @Test
    fun saveUserTest() {
        // given
        val request = UserCreateRequest("user_name", null)

        // when
        userService.saveUser(request)

        // then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)

        assertThat(results[0].name).isEqualTo("user_name")
        assertThat(results[0].age).isNull()
    }

    @DisplayName("유저 조회가 정상 동작한다.")
    @Test
    fun getUsersTest() {
        // given
        userRepository.saveAll(listOf(
                User("A", 20),
                User("B", null),
        ))

        // when
        val results = userService.getUsers()

        // then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name")
                .containsExactlyInAnyOrder("A","B")
        assertThat(results).extracting("age")
                .containsExactlyInAnyOrder(20, null)
    }

    @DisplayName("유저 업데이트가 정상 동작한다.")
    @Test
    fun updateUserNameTest() {
        // given
        val savedUser = userRepository.save(User("A", null))
        val request = UserUpdateRequest(savedUser.id!!, "B")

        // when
        userService.updateUserName(request)

        // then
        val result = userRepository.findAll()[0]
        assertThat(result.name).isEqualTo(request.name)
    }

    @DisplayName("유저 삭제가 정상 동작한다.")
    @Test
    fun deleteUserTest() {
        // given
        val savedUser = userRepository.save(User("A", null))

        // when
        userService.deleteUser("A")

        // then
        assertThat(userRepository.findAll()).isEmpty()
    }

    @DisplayName("대출 기록이 없는 유저도 응답에 포함된다")
    @Test
    fun getUserLoanHistoriesTest1() {
        // given
        userRepository.save(User("A", null))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).isEmpty()
    }

    @DisplayName("대출 기록이 많은 유저의 응답이 정상 동작한다")
    @Test
    fun getUserLoanHistoriesTest2() {
        // given
        val savedUser = userRepository.save(User("A", null))
        userLoanHistoryRepository.saveAll(listOf(
                UserLoanHistory.fixture(savedUser, "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책3", UserLoanStatus.RETURNED),
        ))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).hasSize(3)

        assertThat(results[0].books).extracting("name")
                .containsExactlyInAnyOrder("책1","책2","책3")

        assertThat(results[0].books).extracting("isReturn")
                .containsExactlyInAnyOrder(false, false, true)
    }


    @DisplayName("방금 두 경우가 합쳐진 테스트")
    @Test
    fun getUserLoanHistoriesTest3() {
        // given
        val savedUsers = userRepository.saveAll(listOf(
            User("A", null),
            User("B", null)
        ))
        userLoanHistoryRepository.saveAll(listOf(
                UserLoanHistory.fixture(savedUsers[0], "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUsers[0], "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUsers[0], "책3", UserLoanStatus.RETURNED),
        ))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(2)

        val userAResult = results.first{ it.name == "A" }
        assertThat(userAResult.name).isEqualTo("A")
        assertThat(userAResult.books).hasSize(3)

        assertThat(userAResult.books).extracting("name")
                .containsExactlyInAnyOrder("책1","책2","책3")

        assertThat(userAResult.books).extracting("isReturn")
                .containsExactlyInAnyOrder(false, false, true)


        val userBResult = results.first{ it.name == "B" }
        assertThat(userBResult.books).isEmpty()

    }




}