package com.queelag.capacitor.biometric

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.activity.result.ActivityResult
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import com.queelag.capacitor.biometric.definitions.ActivityCode
import com.queelag.capacitor.biometric.definitions.BiometricPromptCallbackStatus
import com.queelag.capacitor.biometric.definitions.BiometricPromptCallbackType
import com.queelag.capacitor.biometric.definitions.Core
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.KeyGenerator

@RequiresApi(Build.VERSION_CODES.R)
@CapacitorPlugin(name = "Biometric")
class BiometricPlugin : Plugin() {
    @PluginMethod
    fun isAvailable(call: PluginCall) {
        when (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                call.reject("BIOMETRIC_ERROR_HW_UNAVAILABLE")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                call.reject("BIOMETRIC_ERROR_NONE_ENROLLED")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                call.reject("BIOMETRIC_ERROR_NO_HARDWARE")
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                call.reject("BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                call.reject("BIOMETRIC_ERROR_UNSUPPORTED")
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                call.reject("BIOMETRIC_STATUS_UNKNOWN")
            BiometricManager.BIOMETRIC_SUCCESS ->
                call.resolve()
        }
    }

    @PluginMethod
    fun prompt(call: PluginCall) {
        this.startPromptActivityForResult(call, ActivityCode.ACTIVITY_CODE_PROMPT)
    }

    @PluginMethod
    fun createAsymmetricKeys(call: PluginCall) {
        call.resolve(JSObject().put("publicKey", this.createAsymmetricKeys()))
    }

    @PluginMethod
    fun createSymmetricKey(call: PluginCall) {
        this.createSymmetricKey()
        call.resolve()
    }

    @PluginMethod
    fun readPublicKey(call: PluginCall) {
        call.resolve(JSObject().put("value", this.readPublicKey()))
    }

    @PluginMethod
    fun deleteAsymmetricKeys(call: PluginCall) {
        this.deleteEntryFromKeyStore(Core.ASYMMETRIC_KEYS_ALIAS)
        call.resolve()
    }

    @PluginMethod
    fun deleteSymmetricKey(call: PluginCall) {
        this.deleteEntryFromKeyStore(Core.SYMMETRIC_KEY_ALIAS)
        call.resolve()
    }

    @PluginMethod
    fun writeData(call: PluginCall) {
        this.startPromptActivityForResult(call, ActivityCode.ACTIVITY_CODE_PROMPT_ENCRYPT)
    }

    @PluginMethod
    fun readData(call: PluginCall) {
        this.startPromptActivityForResult(call, ActivityCode.ACTIVITY_CODE_PROMPT_DECRYPT)
    }

    @PluginMethod
    fun deleteData(call: PluginCall) {
        this.sharedPreferencesEditor.remove(call.getString("key")).apply()
        call.resolve()
    }

    @PluginMethod
    fun hasData(call: PluginCall) {
        call.resolve(JSObject().put("value", this.sharedPreferences.contains(call.getString("key"))))
    }

    @PluginMethod
    fun areAsymmetricKeysCreated(call: PluginCall) {
        call.resolve(JSObject().put("value", keyStore.containsAlias(Core.ASYMMETRIC_KEYS_ALIAS)))
    }

    @PluginMethod
    fun isSymmetricKeyCreated(call: PluginCall) {
        call.resolve(JSObject().put("value", keyStore.containsAlias(Core.SYMMETRIC_KEY_ALIAS)))
    }

    @ActivityCallback
    private fun onPromptResult(call: PluginCall, result: ActivityResult) {
        val data = result.data ?: return

        when (data.getSerializableExtra("status")) {
            BiometricPromptCallbackStatus.ERROR -> this.rejectBiometricPromptCallbackError(data, call)
            BiometricPromptCallbackStatus.FAILED -> call.reject(BiometricPromptCallbackStatus.FAILED.name)
            BiometricPromptCallbackStatus.SUCCESS -> call.resolve()
        }
    }

    @ActivityCallback
    private fun onPromptDecryptResult(call: PluginCall, result: ActivityResult) {
        val data = result.data ?: return

        when (data.getSerializableExtra("status")) {
            BiometricPromptCallbackStatus.ERROR -> this.rejectBiometricPromptCallbackError(data, call)
            BiometricPromptCallbackStatus.FAILED -> call.reject(BiometricPromptCallbackStatus.FAILED.name)
            BiometricPromptCallbackStatus.SUCCESS -> call.resolve(JSObject().put("value", data.getStringExtra("decrypted")))
        }
    }

    @ActivityCallback
    private fun onPromptEncryptResult(call: PluginCall, result: ActivityResult) {
        val data = result.data ?: return

        when (data.getSerializableExtra("status")) {
            BiometricPromptCallbackStatus.ERROR -> this.rejectBiometricPromptCallbackError(data, call)
            BiometricPromptCallbackStatus.FAILED -> call.reject(BiometricPromptCallbackStatus.FAILED.name)
            BiometricPromptCallbackStatus.SUCCESS -> {
                this.sharedPreferencesEditor.putString(call.getString("key"), data.getStringExtra("encrypted")).apply()
                call.resolve()
            }
        }
    }

    private fun startPromptActivityForResult(call: PluginCall, code: Int) {
        val intent = Intent(context, BiometricPromptActivity::class.java)

        intent.putExtra("confirmationRequired", call.getBoolean("confirmationRequired", false))
        intent.putExtra("description", call.getString("description", ""))
        intent.putExtra("negativeButtonText", call.getString("negativeButtonText", "Cancel"))
        intent.putExtra("subtitle", call.getString("subtitle", ""))
        intent.putExtra("title", call.getString("title", "Authenticate"))

        when (code) {
            ActivityCode.ACTIVITY_CODE_PROMPT -> {
                intent.putExtra("type", BiometricPromptCallbackType.ANY)
                startActivityForResult(call, intent, "onPromptResult")
            }
            ActivityCode.ACTIVITY_CODE_PROMPT_DECRYPT -> {
                intent.putExtra("encrypted", this.sharedPreferences.getString(call.getString("key"), ""))
                intent.putExtra("type", BiometricPromptCallbackType.DECRYPT)
                startActivityForResult(call, intent, "onPromptDecryptResult")
            }
            ActivityCode.ACTIVITY_CODE_PROMPT_ENCRYPT -> {
                intent.putExtra("decrypted", call.getString("value"))
                intent.putExtra("type", BiometricPromptCallbackType.ENCRYPT)
                startActivityForResult(call, intent, "onPromptEncryptResult")
            }
        }
    }

    private fun createAsymmetricKeys(): String {
        kotlin.runCatching {
            if (this.keyStore.containsAlias(Core.ASYMMETRIC_KEYS_ALIAS)) {
                this.keyStore.deleteEntry(Core.ASYMMETRIC_KEYS_ALIAS)
            }

            val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, Core.KEYSTORE_PROVIDER)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(Core.ASYMMETRIC_KEYS_ALIAS, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .setInvalidatedByBiometricEnrollment(true)
                    .setRandomizedEncryptionRequired(true)
                    .setUnlockedDeviceRequired(true)
                    .setUserAuthenticationParameters(Core.ASYMMETRIC_USER_AUTHENTICATION_TIMEOUT, KeyProperties.AUTH_BIOMETRIC_STRONG)
                    .setUserAuthenticationRequired(true)
                    .build()

            keyPairGenerator.initialize(keyGenParameterSpec)
            val keyPair = keyPairGenerator.genKeyPair()

            return Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
        }.onFailure { e -> e.printStackTrace() }

        return ""
    }

    private fun createSymmetricKey() {
        kotlin.runCatching {
            if (this.keyStore.containsAlias(Core.SYMMETRIC_KEY_ALIAS)) {
                this.keyStore.deleteEntry(Core.SYMMETRIC_KEY_ALIAS)
            }

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, Core.KEYSTORE_PROVIDER)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(Core.SYMMETRIC_KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setInvalidatedByBiometricEnrollment(true)
                    .setKeySize(Core.SYMMETRIC_KEY_SIZE)
                    .setRandomizedEncryptionRequired(true)
                    .setUnlockedDeviceRequired(true)
                    .setUserAuthenticationParameters(Core.SYMMETRIC_KEY_USER_AUTHENTICATION_TIMEOUT, KeyProperties.AUTH_BIOMETRIC_STRONG)
                    .setUserAuthenticationRequired(true)
                    .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }.onFailure { e -> e.printStackTrace() }
    }

    private fun readPublicKey(): String {
        kotlin.runCatching {
            Base64.encodeToString(this.keyStore.getCertificate(Core.ASYMMETRIC_KEYS_ALIAS).publicKey.encoded, Base64.NO_WRAP)
        }.onFailure { e -> e.printStackTrace() }

        return ""
    }

    private fun rejectBiometricPromptCallbackError(data: Intent, call: PluginCall) {
        val errorCode = data.getStringExtra("errorCode")
        val errString = data.getStringExtra("errString")

        call.reject("$errorCode, $errString")
    }

    private fun deleteEntryFromKeyStore(alias: String) {
        kotlin.runCatching { this.keyStore.deleteEntry(alias) }.onFailure { e -> e.printStackTrace() }
    }

    private val keyStore: KeyStore
        get() {
            val keyStore = KeyStore.getInstance(Core.KEYSTORE_TYPE)
            keyStore.load(null)
            return keyStore
        }

    private val sharedPreferences: SharedPreferences
        get() {
            return context.getSharedPreferences(Core.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        }

    private val sharedPreferencesEditor: SharedPreferences.Editor
        get() {
            return this.sharedPreferences.edit()
        }
}
