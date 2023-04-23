package com.aracna.capacitor.biometric

import android.app.Activity
import android.content.Intent
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.aracna.capacitor.biometric.definitions.BiometricPromptCallbackStatus
import com.aracna.capacitor.biometric.definitions.BiometricPromptCallbackType
import com.aracna.capacitor.biometric.definitions.Core
import com.aracna.capacitor.biometric.definitions.Core.IV_SEPARATOR
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class BiometricPromptCallback(private val activity: AppCompatActivity) : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)

        val intent = Intent()
                .putExtra("errorCode", errorCode)
                .putExtra("errString", errString.toString())
                .putExtra("status", BiometricPromptCallbackStatus.ERROR)

        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)

        val intent = Intent()

        kotlin.runCatching {
            when (this.type) {
                BiometricPromptCallbackType.ANY -> intent.putExtra("status", BiometricPromptCallbackStatus.SUCCESS)
                BiometricPromptCallbackType.DECRYPT -> intent
                        .putExtra("decrypted", this.decrypt(encrypted, this.cipherSymmetricDecryptMode))
                        .putExtra("status", BiometricPromptCallbackStatus.SUCCESS)
                BiometricPromptCallbackType.ENCRYPT -> intent
                        .putExtra("encrypted", this.encrypt(decrypted, this.cipherSymmetricEncryptMode))
                        .putExtra("status", BiometricPromptCallbackStatus.SUCCESS)
            }
        }.onFailure { e ->
            e.printStackTrace()
            intent
                    .putExtra("decrypted", "")
                    .putExtra("encrypted", "")
                    .putExtra("status", BiometricPromptCallbackStatus.ERROR)
        }

        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()

        activity.setResult(Activity.RESULT_OK, Intent().putExtra("status", BiometricPromptCallbackStatus.FAILED))
        activity.finish()
    }

    private fun decrypt(encrypted: String, cipher: Cipher): String {
        return Base64.encodeToString(cipher.doFinal(Base64.decode(encrypted.replace(Regex("$IV_SEPARATOR.+"), ""), Base64.NO_WRAP)), Base64.NO_WRAP)
    }

    private fun encrypt(decrypted: String, cipher: Cipher): String {
        return Base64.encodeToString(cipher.doFinal(Base64.decode(decrypted, Base64.NO_WRAP)), Base64.NO_WRAP) + IV_SEPARATOR + Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
    }

    private val cipherSymmetricDecryptMode: Cipher
        get() {
            val cipher = Cipher.getInstance(Core.SYMMETRIC_KEY_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, this.symmetricKey, GCMParameterSpec(Core.AUTHENTICATION_TAG_LENGTH, this.iv))
            return cipher
        }

    private val cipherSymmetricEncryptMode: Cipher
        get() {
            val cipher = Cipher.getInstance(Core.SYMMETRIC_KEY_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, this.symmetricKey)
            return cipher
        }

    private val decrypted: String
        get() {
            return activity.intent.getStringExtra("decrypted") as String
        }

    private val encrypted: String
        get() {
            return activity.intent.getStringExtra("encrypted") as String
        }

    private val iv: ByteArray
        get() {
            return Base64.decode(this.encrypted.replace(Regex("^.+${IV_SEPARATOR}"), ""), Base64.NO_WRAP)
        }

    private val keyStore: KeyStore
        get() {
            val keyStore = KeyStore.getInstance(Core.KEYSTORE_TYPE)
            keyStore.load(null)
            return keyStore
        }

    private val symmetricKey: Key
        get() {
            return this.keyStore.getKey(Core.SYMMETRIC_KEY_ALIAS, null)
        }

    private val type: BiometricPromptCallbackType
        get() {
            return activity.intent.getSerializableExtra("type") as BiometricPromptCallbackType
        }
}