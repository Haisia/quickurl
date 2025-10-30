package dev.haisia.quickurl.domain.user

import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import dev.haisia.quickurl.domain.PasswordEncoder
import dev.haisia.quickurl.fixture.UserFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@DisplayName("User 테스트")
class UserTest {
  
  val passwordEncoder: PasswordEncoder = object : PasswordEncoder {
    override fun encode(password: String): String = "encoded_$password"
    override fun matches(password: String, hashedPassword: String): Boolean = encode(password) == hashedPassword
  }
  
  @Test
  @DisplayName("User 객체를 생성할 수 있고 패스워드가 암호화되어 저장된다")
  fun createUser() {
    val email = Email("test@test.com")
    val password = Password("mypassword")
    
    UserFixture.createUser(
      email = email,
      password = password,
      passwordEncoder = passwordEncoder
    ).let {
      assert(it.email == email)
      assert(it.hashedPassword == passwordEncoder.encode(password.value))
    }
  }
  
  @Test
  @DisplayName("올바른 비밀번호로 인증할 수 있다")
  fun verifyPasswordWithCorrectPassword() {
    val password = Password("mypassword")
    
    assert(
      UserFixture.createUser(password = password, passwordEncoder = passwordEncoder)
        .verifyPassword(password, passwordEncoder)
    )
  }
  
  @Test
  @DisplayName("잘못된 비밀번호로 인증에 실패한다")
  fun verifyPasswordWithIncorrectPassword() {
    val password = Password("mypassword")
    val anotherPassword = Password("anotherPassword")
    
    assert(
      !UserFixture.createUser(password = password)
        .verifyPassword(anotherPassword, passwordEncoder)
    )
  }
  
  @Test
  @DisplayName("비밀번호 변경 시 새 비밀번호가 암호화되어 저장된다")
  fun changePassword() {
    UserFixture.createUser()
      .let {
        val newPassword = Password("newpassword")
        it.changePassword(newPassword, passwordEncoder)
        
        assert(it.verifyPassword(newPassword, passwordEncoder))
        assert(it.hashedPassword != newPassword.value)
      }
  }
  
  @Test
  @DisplayName("비밀번호 변경 후 기존 비밀번호는 사용할 수 없고 새 비밀번호로만 인증할 수 있다")
  fun passwordChangeUpdatesAuthentication() {
    val oldPassword = Password("oldpassword")
    val newPassword = Password("newpassword")
    
    UserFixture.createUser(password = oldPassword).let {
      it.changePassword(newPassword, passwordEncoder)
      
      assert(!it.verifyPassword(oldPassword, passwordEncoder))
      assert(it.verifyPassword(newPassword, passwordEncoder))
    }
  }
  
  @Test
  @DisplayName("ID가 존재하면 getIdOrThrow로 ID를 가져올 수 있다")
  fun getIdOrThrowReturnsIdWhenExists() {
    val id = UUID.randomUUID()
    assert(UserFixture.createUser(id = id).getIdOrThrow() == id)
  }
  
  @Test
  @DisplayName("ID가 null이면 getIdOrThrow에서 예외가 발생한다")
  fun getIdOrThrowThrowsExceptionWhenIdIsNull() {
    assertThrows<UserIdMissingException> {
      UserFixture.createUser().getIdOrThrow()
    }
  }
  
  @Test
  @DisplayName("같은 ID를 가진 User 객체는 동등하다")
  fun usersWithSameIdAreEqual() {
    val id = UUID.randomUUID()
    assert(
      UserFixture.createUser(id = id)
      == UserFixture.createUser(id = id)
    )
  }
  
  @Test
  @DisplayName("다른 ID를 가진 User 객체는 동등하지 않다")
  fun usersWithDifferentIdAreNotEqual() {
    assert(
      UserFixture.createUser(id = UUID.randomUUID())
      != UserFixture.createUser(id = UUID.randomUUID())
    )
  }
  
  @Test
  @DisplayName("ID가 null인 User 객체는 다른 객체와 동등하지 않다")
  fun userWithNullIdIsNotEqual() {
    val idNullUser = UserFixture.createUser()
    val idNullAnotherUser = UserFixture.createUser()
    val idNotNullUser = UserFixture.createUser(id = UUID.randomUUID())
    
    assert(idNullUser != idNullAnotherUser)
    assert(idNullUser != idNotNullUser)
  }
}