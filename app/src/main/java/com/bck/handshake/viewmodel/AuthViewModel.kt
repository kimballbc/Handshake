package com.bck.handshake.viewmodel//package com.bck.handshake.viewmodel
//
//import SupabaseHelper
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
///**
// * Authentication state for the UI to observe
// */
//sealed class AuthState {
//    object Loading : AuthState()
//    object SignedIn : AuthState()
//    object SignedOut : AuthState()
//    data class Error(val message: String) : AuthState()
//}
//
///**
// * ViewModel for managing authentication state and operations
// */
//class AuthViewModel : ViewModel() {
//
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
//    val authState: StateFlow<AuthState> = _authState.asStateFlow()
//
//    init {
//        checkAuthState()
//    }
//
//    /**
//     * Checks the current authentication state and updates the StateFlow
//     */
//    fun checkAuthState() {
//        if (SupabaseHelper.isSignedIn()) {
//            _authState.value = AuthState.SignedIn
//        } else {
//            _authState.value = AuthState.SignedOut
//        }
//    }
//
//    /**
//     * Attempts to sign in a user with email and password
//     */
//    fun signIn(email: String, password: String) {
//        viewModelScope.launch {
//            _authState.value = AuthState.Loading
//
//            SupabaseHelper.signInWithEmail(email, password)
//                .onSuccess {
//                    _authState.value = AuthState.SignedIn
//                }
//                .onFailure { exception ->
//                    _authState.value = AuthState.Error(exception.message ?: "Sign in failed")
//                }
//        }
//    }
//
//    /**
//     * Attempts to register a new user with email and password
//     */
//    fun signUp(email: String, password: String) {
//        viewModelScope.launch {
//            _authState.value = AuthState.Loading
//
//            SupabaseHelper.signUpWithEmail(email, password)
//                .onSuccess {
//                    _authState.value = AuthState.SignedIn
//                }
//                .onFailure { exception ->
//                    _authState.value = AuthState.Error(exception.message ?: "Sign up failed")
//                }
//        }
//    }
//
//    /**
//     * Signs out the current user
//     */
//    fun signOut() {
//        viewModelScope.launch {
//            _authState.value = AuthState.Loading
//
//            SupabaseHelper.signOut()
//                .onSuccess {
//                    _authState.value = AuthState.SignedOut
//                }
//                .onFailure { exception ->
//                    _authState.value = AuthState.Error(exception.message ?: "Sign out failed")
//                }
//        }
//    }
//
//    /**
//     * Gets the current user ID or null if not signed in
//     */
//    fun getCurrentUserId(): String? {
//        return SupabaseHelper.getCurrentUserId()
//    }
//}