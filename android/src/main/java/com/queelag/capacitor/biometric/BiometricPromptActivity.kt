package com.queelag.capacitor.biometric

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.queelag.capacitor.biometric.definitions.BiometricPromptCallbackStatus
import com.queelag.capacitor.biometric.definitions.BiometricPromptCallbackType
import com.queelag.capacitor.biometric.definitions.Core.AUTHENTICATION_TAG_LENGTH
import com.queelag.capacitor.biometric.definitions.Core.IV_SEPARATOR
import com.queelag.capacitor.biometric.definitions.Core.KEYSTORE_TYPE
import com.queelag.capacitor.biometric.definitions.Core.SYMMETRIC_KEY_ALIAS
import com.queelag.capacitor.biometric.definitions.Core.SYMMETRIC_KEY_TRANSFORMATION
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec


@RequiresApi(Build.VERSION_CODES.P)
class BiometricPromptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.authorize(BiometricPrompt(this, mainExecutor, BiometricPromptCallback(this)))
    }

    private fun authorize(prompt: BiometricPrompt) {
        kotlin.runCatching {
            when (this.type) {
                BiometricPromptCallbackType.ANY -> prompt.authenticate(this.buildPromptInfo())
                BiometricPromptCallbackType.DECRYPT -> prompt.authenticate(this.buildPromptInfo(), BiometricPrompt.CryptoObject(this.cipherSymmetricDecryptMode))
                BiometricPromptCallbackType.ENCRYPT -> prompt.authenticate(this.buildPromptInfo(), BiometricPrompt.CryptoObject(this.cipherSymmetricEncryptMode))
            }
        }.onFailure { e ->
            e.printStackTrace()
            setResult(
                    Activity.RESULT_OK,
                    Intent()
                            .putExtra("decrypted", "")
                            .putExtra("encrypted", "")
                            .putExtra("status", BiometricPromptCallbackStatus.ERROR)
            )
            finish()
        }
    }

    private fun buildPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
                .setConfirmationRequired(intent.getBooleanExtra("confirmationRequired", false))
                .setDescription(intent.getStringExtra("description"))
                .setNegativeButtonText(if (intent.hasExtra("negativeButtonText")) intent.getStringExtra("negativeButtonText").orEmpty() else "Cancel")
                .setSubtitle(intent.getStringExtra("subtitle"))
                .setTitle(if (intent.hasExtra("title")) intent.getStringExtra("title").orEmpty() else "Authenticate")
                .build()
    }

    private val cipherSymmetricDecryptMode: Cipher
        get() {
            val cipher = Cipher.getInstance(SYMMETRIC_KEY_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, this.symmetricKey, GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, this.iv))
            return cipher
        }

    private val cipherSymmetricEncryptMode: Cipher
        get() {
            val cipher = Cipher.getInstance(SYMMETRIC_KEY_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, this.symmetricKey)
            return cipher
        }

    private val encrypted: String
        get() {
            return intent.getStringExtra("encrypted") as String
        }

    private val iv: ByteArray
        get() {
            return Base64.decode(this.encrypted.replace(Regex("^.+${IV_SEPARATOR}"), ""), Base64.NO_WRAP)
        }

    private val keyStore: KeyStore
        get() {
            val keyStore = KeyStore.getInstance(KEYSTORE_TYPE)
            keyStore.load(null)
            return keyStore
        }

    private val symmetricKey: Key
        get() {
            return this.keyStore.getKey(SYMMETRIC_KEY_ALIAS, null)
        }

    private val type: BiometricPromptCallbackType
        get() {
            return intent.getSerializableExtra("type") as BiometricPromptCallbackType
        }
}