package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import comexampleproject.Person
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.controller.ClientController
import org.example.project.controller.responseHandler
import org.example.project.logging.AppLogger
import org.example.project.logging.ProcessTags
import org.example.project.model.dto.LoginDto
import org.example.project.model.dto.PersonDto
import org.example.project.persistence.database.PersonDao
import org.example.project.persistence.database.PersonDaoImpl
import org.example.project.persistence.preferences.PreferencesDaoImpl

data class PersonUiState(
    val persons: List<Person> = emptyList(),
    val isLoading: Boolean = false,
    val messageEvent: MessageEvent? = null,
    val success: Boolean = true,
)

class PersonViewModel(
    database: PrintStainDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val personDao: PersonDao = PersonDaoImpl(database)

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
        // Receive response from server and return it
        viewModelScope.launch(dispatcher) {
            try {
                _personUiState.update { it.copy(isLoading = true) }
                val serverResponse = responseHandler(
                    "Register user",
                    ProcessTags.UserRegistration.name,
                    "String"
                ) { ClientController.userController.registerUser(personDto) }
                when (serverResponse.success) {
                    false -> _personUiState.update {
                        it.copy(
                            isLoading = false,
                            success = false,
                            messageEvent = MessageEvent(serverResponse.response)
                        )
                    }

                    true -> {
                        _personUiState.update {
                            it.copy(
                                isLoading = false,
                                success = true,
                                messageEvent = MessageEvent(serverResponse.response)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                AppLogger.e(
                    "",
                    """
                        Process: ${ProcessTags.Userlogin.name}.
                        Status: Unexpected internal error registering user.
                    """.trimIndent(),
                    e
                )
                _personUiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}")
                    )
                }
            }
        }
    }

    fun loginUser(loginDto: LoginDto, rememberMe: Boolean) {
        viewModelScope.launch(dispatcher) {
            _personUiState.update { it.copy(isLoading = true) }
            try {
                // Receive response from server and return it
                val serverResponse = responseHandler(
                    "Login user",
                    ProcessTags.Userlogin.name,
                    "String"
                ) { ClientController.userController.loginUser(loginDto) }
                when (serverResponse.success) {
                    false -> _personUiState.update {
                        it.copy(
                            isLoading = false,
                            success = false,
                            messageEvent = MessageEvent(if (serverResponse.response != "Unauthorized") serverResponse.response else "Wrong username or password")
                        )
                    }

                    true -> {
                        _personUiState.update {
                            it.copy(
                                isLoading = false,
                                success = true,
                                messageEvent = MessageEvent(serverResponse.response)
                            )
                        }
                        // Save user
                        if (rememberMe) {
                            PreferencesDaoImpl.saveUser(
                                username = loginDto.username!!,
                                password = loginDto.password!!,
                                token = serverResponse.data
                            )
                        } else {
                            PreferencesDaoImpl.saveToken(serverResponse.data)
                        }
                    }
                }
            } catch (e: Exception) {
                AppLogger.e(
                    "",
                    """
                        Process: ${ProcessTags.Userlogin.name}.
                        Status: Unexpected internal error login user.
                    """.trimIndent(),
                    e
                )
                _personUiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}")
                    )
                }
                throw e
            }
        }
    }

    private fun autologin() {
        viewModelScope.launch(dispatcher) {
            _personUiState.update { it.copy(isLoading = true) }
            // Get saved preferences
            val username = PreferencesDaoImpl.getUsername()
            val password = PreferencesDaoImpl.getPassword()

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

    fun insertPerson(
        personId: Long,
        name: String,
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                _personUiState.update { it.copy(isLoading = true) }
                // Insert new person
                AppLogger.i(
                    ProcessTags.InsertUser.name,
                    """
                    Process: ${ProcessTags.InsertUser.name}.
                    Status: Attempting to insert user.""".trimIndent()
                )
                if (!_personUiState.value.persons.contains(
                        Person(
                            personId,
                            name
                        )
                    ) or _personUiState.value.persons.isEmpty()
                ) {
                    personDao.insertPerson(
                        personId = personId,
                        name = name
                    )
                    _personUiState.update {
                        it.copy(
                            isLoading = false,
                            success = true,
                            messageEvent = MessageEvent("Successfully inserted user.")
                        )
                    }
                    AppLogger.i(
                        ProcessTags.InsertUser.name,
                        """
                    Process: ${ProcessTags.InsertUser.name}.
                    Status: Person inserted successfully.""".trimIndent()
                    )
                } else {
                    _personUiState.update {
                        it.copy(
                            isLoading = false,
                            success = false,
                            messageEvent = MessageEvent("Person already contained in database.")
                        )
                    }
                    AppLogger.i(
                        ProcessTags.InsertUser.name,
                        """
                    Process: ${ProcessTags.InsertUser.name}.
                    Status: Person already contained in database.""".trimIndent()
                    )
                }
            } catch (e: Exception) {
                AppLogger.e(
                    ProcessTags.InsertUser.name,
                    """
                        Process: ${ProcessTags.InsertUser.name}.
                        Status: Internal sql error inserting user.
                    """.trimIndent(),
                    e
                )
                _personUiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}")
                    )
                }
            }
        }
    }

    fun getAllPersons() {
        viewModelScope.launch(dispatcher) {
            try {
                _personUiState.update { it.copy(isLoading = true) }
                // Insert new person
                AppLogger.i(
                    ProcessTags.GetAllPersons.name,
                    """
                    Process: ${ProcessTags.GetAllPersons.name}.
                    Status: Attempting to get all persons.""".trimIndent()
                )

                // Get all persons and update ui
                val persons = personDao.getAllPersons().first()
                _personUiState.update {
                    it.copy(
                        isLoading = false,
                        success = true,
                        persons = persons
                    )
                }

            } catch (e: Exception) {
                AppLogger.e(
                    ProcessTags.GetAllPersons.name,
                    """
                        Process: ${ProcessTags.GetAllPersons.name}.
                        Status: Internal sql error getting all users.
                    """.trimIndent(),
                    e
                )
                _personUiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        messageEvent = MessageEvent("Error: ${e.localizedMessage}")
                    )
                }
            }
        }
    }
}