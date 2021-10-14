package com.queelag.capacitor.biometric.definitions

object ActivityCode {
    const val ACTIVITY_CODE_PROMPT = 1000
    const val ACTIVITY_CODE_PROMPT_DECRYPT = 1001
    const val ACTIVITY_CODE_PROMPT_ENCRYPT = 1002
}

object Core {
    const val ASYMMETRIC_KEYS_ALIAS = "QUEELAG_BIOMETRIC_ASYMMETRIC_KEYS"
    const val ASYMMETRIC_USER_AUTHENTICATION_TIMEOUT = 1
    const val AUTHENTICATION_TAG_LENGTH = 128
    const val IV_SEPARATOR = "_"
    const val IV_SIZE = 12
    const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    const val KEYSTORE_TYPE = "AndroidKeyStore"
    const val SHARED_PREFERENCES = "QUEELAG_BIOMETRIC_SHARED_PREFERENCES"
    const val SYMMETRIC_KEY_ALGORITHM = "AES"
    const val SYMMETRIC_KEY_ALIAS = "QUEELAG_BIOMETRIC_SYMMETRIC_KEY"
    const val SYMMETRIC_KEY_MODE = "GCM"
    const val SYMMETRIC_KEY_PADDINGS = "NoPadding"
    const val SYMMETRIC_KEY_SIZE = 256
    const val SYMMETRIC_KEY_TRANSFORMATION = "$SYMMETRIC_KEY_ALGORITHM/$SYMMETRIC_KEY_MODE/$SYMMETRIC_KEY_PADDINGS"
    const val SYMMETRIC_KEY_USER_AUTHENTICATION_TIMEOUT = 1
}