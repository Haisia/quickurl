package dev.haisia.quickurl.application.user

import dev.haisia.quickurl.application.shared.out.TokenProvider
import dev.haisia.quickurl.application.shared.out.TokenResolver
import dev.haisia.quickurl.application.user.out.RefreshTokenRepository
import dev.haisia.quickurl.application.user.out.UserRepository
import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import dev.haisia.quickurl.domain.PasswordEncoder
import dev.haisia.quickurl.fixture.UserFixture
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@DisplayName("UserService 테스트")
class UserServiceTest {
  
  private val passwordEncoder: PasswordEncoder = mockk()
  private val userRepository: UserRepository = mockk()
  private val tokenProvider: TokenProvider = mockk()
  private val tokenResolver: TokenResolver = mockk()
  private val refreshTokenRepository: RefreshTokenRepository = mockk()
  private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)
  
  private lateinit var userService: UserService
  
  @BeforeEach
  fun setUp() {
    userService = UserService(
      passwordEncoder = passwordEncoder,
      userRepository = userRepository,
      tokenProvider = tokenProvider,
      tokenResolver = tokenResolver,
      refreshTokenRepository = refreshTokenRepository,
      eventPublisher = eventPublisher
    )
  }
  
  @Test
  @DisplayName("새로운 사용자를 생성할 수 있다")
  fun createUser() {
    val email = Email("test@example.com")
    val password = Password("password123")
    val user = UserFixture.createUser(id = UUID.randomUUID(), email = email)
    
    every { userRepository.existsByEmail(email) } returns false
    every { passwordEncoder.encode(password.value) } returns "encoded_password"
    every { userRepository.save(any()) } returns user
    
    val result = userService.createUser(email, password)
    
    assert(result == user)
    verify(exactly = 1) { userRepository.save(any()) }
    verify(exactly = 1) { eventPublisher.publishEvent(any<UserEvent.UserCreated>()) }
  }
  
  @Test
  @DisplayName("이미 존재하는 이메일로 사용자 생성 시 예외가 발생한다")
  fun createUserWithExistingEmailThrowsException() {
    val email = Email("existing@example.com")
    val password = Password("password123")
    
    every { userRepository.existsByEmail(email) } returns true
    
    assertThrows<EmailAlreadyRegisteredException> {
      userService.createUser(email, password)
    }
    
    verify(exactly = 0) { userRepository.save(any()) }
  }
  
  @Test
  @DisplayName("사용자 생성 시 UserCreated 이벤트가 발행된다")
  fun createUserPublishesEvent() {
    val email = Email("test@example.com")
    val password = Password("password123")
    val user = UserFixture.createUser(id = UUID.randomUUID(), email = email)
    
    every { userRepository.existsByEmail(email) } returns false
    every { passwordEncoder.encode(password.value) } returns "encoded_password"
    every { userRepository.save(any()) } returns user
    
    userService.createUser(email, password)
    
    verify { eventPublisher.publishEvent(match<UserEvent.UserCreated> { it.userEmail == email }) }
  }
  
  @Test
  @DisplayName("올바른 이메일과 비밀번호로 로그인할 수 있다")
  fun loginUser() {
    val email = Email("test@example.com")
    val password = Password("password123")
    val userId = UUID.randomUUID()
    val user = UserFixture.createUser(id = userId, email = email)
    val accessToken = "access_token"
    val refreshToken = "refresh_token"
    
    every { userRepository.findByEmail(email) } returns user
    every { user.verifyPassword(password, passwordEncoder) } returns true
    every { tokenProvider.generateAccessToken(userId, email) } returns accessToken
    every { tokenProvider.generateRefreshToken(userId) } returns refreshToken
    every { refreshTokenRepository.save(refreshToken, user) } returns refreshToken
    
    val result = userService.loginUser(email, password)
    
    assert(result.first == accessToken)
    assert(result.second == refreshToken)
    verify { refreshTokenRepository.save(refreshToken, user) }
  }
  
  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
  fun loginUserWithNonExistingEmailThrowsException() {
    val email = Email("nonexisting@example.com")
    val password = Password("password123")
    
    every { userRepository.findByEmail(email) } returns null
    
    assertThrows<LoginFailedException> {
      userService.loginUser(email, password)
    }
  }
  
  @Test
  @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
  fun loginUserWithWrongPasswordThrowsException() {
    val email = Email("test@example.com")
    val password = Password("wrongpassword")
    val user = UserFixture.createUser(email = email)
    
    every { userRepository.findByEmail(email) } returns user
    every { user.verifyPassword(password, passwordEncoder) } returns false
    
    assertThrows<LoginFailedException> {
      userService.loginUser(email, password)
    }
  }
  
  @Test
  @DisplayName("유효한 리프레시 토큰으로 액세스 토큰을 갱신할 수 있다")
  fun accessTokenRefresh() {
    val refreshToken = "valid_refresh_token"
    val userId = UUID.randomUUID()
    val email = Email("test@example.com")
    val user = UserFixture.createUser(id = userId, email = email)
    val newAccessToken = "new_access_token"
    
    every { tokenResolver.validateAsRefreshToken(refreshToken) } returns true
    every { tokenResolver.getUserIdFromToken(refreshToken) } returns userId
    every { userRepository.findById(userId) } returns Optional.of(user)
    every { refreshTokenRepository.increaseIssueCount(refreshToken) } returns true
    every { tokenProvider.generateAccessToken(userId, email) } returns newAccessToken
    
    val result = userService.accessTokenRefresh(refreshToken)
    
    assert(result == newAccessToken)
    verify { refreshTokenRepository.increaseIssueCount(refreshToken) }
  }
  
  @Test
  @DisplayName("존재하지 않는 사용자의 리프레시 토큰으로 갱신 시 예외가 발생한다")
  fun accessTokenRefreshWithNonExistingUserThrowsException() {
    val refreshToken = "valid_refresh_token"
    val userId = UUID.randomUUID()
    
    every { tokenResolver.validateAsRefreshToken(refreshToken) } returns true
    every { tokenResolver.getUserIdFromToken(refreshToken) } returns userId
    every { userRepository.findById(userId) } returns Optional.empty()
    
    assertThrows<UserNotFoundException> {
      userService.accessTokenRefresh(refreshToken)
    }
  }
  
  @Test
  @DisplayName("리프레시 토큰을 만료시킬 수 있다")
  fun expireRefreshToken() {
    val refreshToken = "refresh_token"
    
    every { refreshTokenRepository.disableRefreshToken(refreshToken) } returns 1
    
    userService.expireRefreshToken(refreshToken)
    
    verify(exactly = 1) { refreshTokenRepository.disableRefreshToken(refreshToken) }
  }
}