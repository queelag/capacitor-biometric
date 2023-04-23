package com.aracna.capacitor.biometric

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.aracna.capacitor.biometric.definitions.BiometricPromptCallbackStatus
import com.aracna.capacitor.biometric.definitions.BiometricPromptCallbackType
import com.aracna.capacitor.biometric.definitions.Core.AUTHENTICATION_TAG_LENGTH
import com.aracna.capacitor.biometric.definitions.Core.IV_SEPARATOR
import com.aracna.capacitor.biometric.definitions.Core.KEYSTORE_TYPE
import com.aracna.capacitor.biometric.definitions.Core.SYMMETRIC_KEY_ALIAS
import com.aracna.capacitor.biometric.definitions.Core.SYMMETRIC_KEY_TRANSFORMATION
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec


@RequiresApi(Build.VERSION_CODES.P)
class BiometricPromptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.authorize(BiometricPrompt(this, ContextCompat.getMainExecutor(this), BiometricPromptCallback(this)))
    }

    private fun authorize(prompt: BiometricPrompt) {
        kotlin.runCatching {
            when (this.type) {
                BiometricPromptCallbackType.ANY -> prompt.authenticate(this.buildPromptInfo())
                BiometricPromptCallbackType.DECRYPT -> prompt.authenticate(this.buildPromptInfo())
                BiometricPromptCallbackType.ENCRYPT -> prompt.authenticate(this.buildPromptInfo())
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

    private val type: BiometricPromptCallbackType
        get() {
            return intent.getSerializableExtra("type") as BiometricPromptCallbackType
        }
}