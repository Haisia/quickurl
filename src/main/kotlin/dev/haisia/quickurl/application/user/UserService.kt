package dev.haisia.quickurl.application.user

import dev.haisia.quickurl.application.shared.out.TokenProvider
import dev.haisia.quickurl.application.shared.out.TokenResolver
import dev.haisia.quickurl.application.user.`in`.UserAuthorizer
import dev.haisia.quickurl.application.user.out.RefreshTokenRepository
import dev.haisia.quickurl.application.user.out.UserRepository
import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import dev.haisia.quickurl.domain.PasswordEncoder
import dev.haisia.quickurl.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserService(
  private val passwordEncoder: PasswordEncoder,
  private val userRepository: UserRepository,
  private val tokenProvider: TokenProvider,
  private val tokenResolver: TokenResolver,
  private val refreshTokenRepository: RefreshTokenRepository,
): UserAuthorizer {

  override fun createUser(email: Email, password: Password): User {
    if(userRepository.existsByEmail(email)) {
      throw EmailAlreadyRegisteredException()
    }

    val saved = userRepository.save(User.of(email = email, password = password, passwordEncoder = passwordEncoder))

    return saved
  }

  override fun loginUser(email: Email, password: Password): Pair<String, String> {
    val foundUser = userRepository.findByEmail(email) ?: throw LoginFailedException()

    if(!foundUser.verifyPassword(password, passwordEncoder)) {
      throw LoginFailedException()
    }

    val accessToken = tokenProvider.generateAccessToken(foundUser.getNonNullId(), foundUser.email)
    val refreshToken = tokenProvider.generateRefreshToken(foundUser.getNonNullId())
    refreshTokenRepository.save(refreshToken, foundUser)

    return Pair(accessToken, refreshToken)
  }

  override fun accessTokenRefresh(refreshToken: String): String {
    if(!tokenResolver.validateToken(refreshToken)) {
      throw InvalidTokenAdapterException()
    }

    val userId = tokenResolver.getUserIdFromToken(refreshToken)
    val foundUser = userRepository.findById(userId).orElseThrow()

    refreshTokenRepository.increaseIssueCount(refreshToken)
    return tokenProvider.generateAccessToken(foundUser.getNonNullId(), foundUser.email)
  }

}