package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import comexampleproject.Person
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.model.MessageEvent
import org.example.project.model.dto.LoginDto
import org.example.project.model.dto.PersonDto
import org.example.project.service.PersonService

data class PersonUiState(
    val persons: List<Person> = emptyList(),
    val isLoading: Boolean = false,
    val messageEvent: MessageEvent? = null,
    val success: Boolean = true,
)

class PersonViewModel(
    database: PrintStainDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val personService = PersonService()

    private val _personUiState = MutableStateFlow(PersonUiState(isLoading = true))
    val personUiState: StateFlow<PersonUiState> = _personUiState.asStateFlow()

    init {
        autologin()
    }

    fun consumeMessage() {
        _personUiState.update { currentState ->
            currentState.copy(
                messageEvent = currentState.messageEvent?.consume()
            )
        }
    }

    fun registerUser(personDto: PersonDto) {
        viewModelScope.launch(dispatcher) {
            _personUiState.update { it.copy(isLoading = true) }
            val serverResponse = personService.registerUser(personDto)

            _personUiState.update {
                it.copy(
                    isLoading = false,
                    success = serverResponse.success,
                    messageEvent = MessageEvent(serverResponse.response!!)
                )
            }
        }
    }

    fun loginUser(loginDto: LoginDto, rememberMe: Boolean) {
        viewModelScope.launch(dispatcher) {
            _personUiState.update { it.copy(isLoading = true) }
            val serverResponse = personService.loginUser(loginDto)

            _personUiState.update {
                it.copy(
                    isLoading = false,
                    success = serverResponse.success,
                    messageEvent = MessageEvent(
                        if (serverResponse.response!!.contains("Unauthorized") && !serverResponse.success)
                            "Wrong username or password"
                        else
                            serverResponse.response
                    )
                )
            }

            if (serverResponse.success && serverResponse.data != null) {
                // Save user
                if (rememberMe) {
                    personService.saveUserCredentials(
                        username = loginDto.username!!,
                        password = loginDto.password!!,
                        token = serverResponse.data
                    )
                } else {
                    personService.saveToken(serverResponse.data)
                }
            }

            // Clear message after 3 seconds to avoid issues with login screen
            delay(3000)
            _personUiState.update { it.copy(messageEvent = MessageEvent(null)) }
        }
    }

    fun resetPassword(username: String, newPassword: String) {
        viewModelScope.launch(dispatcher) {
            _personUiState.update { it.copy(isLoading = true) }
            val serverResponse = personService.resetPassword(username, newPassword)

            _personUiState.update {
                it.copy(
                    isLoading = false,
                    success = serverResponse.success,
                    messageEvent = MessageEvent(serverResponse.response!!)
                )
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch(dispatcher) {
            _personUiState.update { it.copy(isLoading = true) }
            val serverResponse = personService.deleteUser()

            _personUiState.update {
                it.copy(
                    isLoading = false,
                    success = serverResponse.success,
                    messageEvent = MessageEvent(serverResponse.response!!)
                )
            }
        }
    }

    private fun autologin() {
        viewModelScope.launch(dispatcher) {
            _personUiState.update { it.copy(isLoading = true) }

            val (username, password) = personService.getUserCredentials()

            // If credentials are not saved, return false
            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                _personUiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                    )
                }
            } else {
                loginUser(LoginDto(username = username, password = password), rememberMe = true)
            }
        }
    }
}