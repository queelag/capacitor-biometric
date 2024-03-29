package com.aracna.capacitor.biometric.definitions

enum class BiometricPromptCallbackStatus {
    ERROR,
    FAILED,
    SUCCESS
}

enum class BiometricPromptCallbackType {
    ANY,
    DECRYPT,
    ENCRYPT
}