package com.queelag.capacitor.biometric

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import com.getcapacitor.*
import com.queelag.capacitor.biometric.definitions.ActivityCode.ACTIVITY_CODE_PROMPT
import com.queelag.capacitor.biometric.definitions.ActivityCode.ACTIVITY_CODE_PROMPT_DECRYPT
import com.queelag.capacitor.biometric.definitions.ActivityCode.ACTIVITY_CODE_PROMPT_ENCRYPT
import com.queelag.capacitor.biometric.definitions.BiometricPromptCallbackStatus
import com.queelag.capacitor.biometric.definitions.BiometricPromptCallbackType
import com.queelag.capacitor.biometric.definitions.Core.ASYMMETRIC_KEYS_ALIAS
import com.queelag.capacitor.biometric.definitions.Core.KEYSTORE_PROVIDER
import com.queelag.capacitor.biometric.definitions.Core.KEYSTORE_TYPE
import com.queelag.capacitor.biometric.definitions.Core.SHARED_PREFERENCES
import com.queelag.capacitor.biometric.definitions.Core.SYMMETRIC_KEY_ALIAS
import com.queelag.capacitor.biometric.definitions.Core.SYMMETRIC_KEY_SIZE
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.KeyGenerator

@RequiresApi(Build.VERSION_CODES.P)
@NativePlugin(requestCodes = [ACTIVITY_CODE_PROMPT, ACTIVITY_CODE_PROMPT_DECRYPT, ACTIVITY_CODE_PROMPT_ENCRYPT])
class Biometric : Plugin() {
    override fun load() {
        super.load()

        if (!this.keyStore.containsAlias(ASYMMETRIC_KEYS_ALIAS)) {
            this.createAsymmetricKeys()
        }
        if (!this.keyStore.containsAlias(SYMMETRIC_KEY_ALIAS)) {
            this.createSymmetricKey()
        }
    }

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
        this.startPromptActivityForResult(call, ACTIVITY_CODE_PROMPT)
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
        this.deleteEntryFromKeyStore(ASYMMETRIC_KEYS_ALIAS)
        call.resolve()
    }

    @PluginMethod
    fun deleteSymmetricKey(call: PluginCall) {
        this.deleteEntryFromKeyStore(SYMMETRIC_KEY_ALIAS)
        call.resolve()
    }

    @PluginMethod
    fun writeData(call: PluginCall) {
        this.startPromptActivityForResult(call, ACTIVITY_CODE_PROMPT_ENCRYPT)
    }

    @PluginMethod
    fun readData(call: PluginCall) {
        this.startPromptActivityForResult(call, ACTIVITY_CODE_PROMPT_DECRYPT)
    }

    @PluginMethod
    fun deleteData(call: PluginCall) {
        this.sharedPreferencesEditor.remove(call.getString("key")).apply()
        call.resolve()
    }

    @PluginMethod
    fun areAsymmetricKeysCreated(call: PluginCall) {
        call.resolve(JSObject().put("value", keyStore.containsAlias(ASYMMETRIC_KEYS_ALIAS)))
    }

    @PluginMethod
    fun isSymmetricKeyCreated(call: PluginCall) {
        call.resolve(JSObject().put("value", keyStore.containsAlias(SYMMETRIC_KEY_ALIAS)))
    }

    override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.handleOnActivityResult(requestCode, resultCode, data)

        val call = savedCall ?: return
        val status = data?.getSerializableExtra("status") ?: return

        when (requestCode) {
            ACTIVITY_CODE_PROMPT -> {
                when (status) {
                    BiometricPromptCallbackStatus.ERROR -> this.rejectBiometricPromptCallbackError(data, call)
                    BiometricPromptCallbackStatus.FAILED -> call.reject(BiometricPromptCallbackStatus.FAILED.name)
                    BiometricPromptCallbackStatus.SUCCESS -> call.resolve()
                }
            }
            ACTIVITY_CODE_PROMPT_DECRYPT -> {
                when (status) {
                    BiometricPromptCallbackStatus.ERROR -> this.rejectBiometricPromptCallbackError(data, call)
                    BiometricPromptCallbackStatus.FAILED -> call.reject(BiometricPromptCallbackStatus.FAILED.name)
                    BiometricPromptCallbackStatus.SUCCESS -> call.resolve(JSObject().put("value", data.getStringExtra("decrypted")))
                }
            }
            ACTIVITY_CODE_PROMPT_ENCRYPT -> {
                when (status) {
                    BiometricPromptCallbackStatus.ERROR -> this.rejectBiometricPromptCallbackError(data, call)
                    BiometricPromptCallbackStatus.FAILED -> call.reject(BiometricPromptCallbackStatus.FAILED.name)
                    BiometricPromptCallbackStatus.SUCCESS -> {
                        this.sharedPreferencesEditor.putString(call.getString("key"), data.getStringExtra("encrypted")).apply()
                        call.resolve()
                    }
                }
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
            ACTIVITY_CODE_PROMPT -> intent.putExtra("type", BiometricPromptCallbackType.ANY)
            ACTIVITY_CODE_PROMPT_DECRYPT -> {
                intent.putExtra("encrypted", this.sharedPreferences.getString(call.getString("key"), ""))
                intent.putExtra("type", BiometricPromptCallbackType.DECRYPT)
            }
            ACTIVITY_CODE_PROMPT_ENCRYPT -> {
                intent.putExtra("decrypted", call.getString("value"))
                intent.putExtra("type", BiometricPromptCallbackType.ENCRYPT)
            }
        }

        saveCall(call)
        startActivityForResult(call, intent, code)
    }

    private fun createAsymmetricKeys(): String {
        kotlin.runCatching {
            if (this.keyStore.containsAlias(ASYMMETRIC_KEYS_ALIAS)) {
                this.keyStore.deleteEntry(ASYMMETRIC_KEYS_ALIAS)
            }

            val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_PROVIDER)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(ASYMMETRIC_KEYS_ALIAS, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .setInvalidatedByBiometricEnrollment(true)
//                .setIsStrongBoxBacked(true)
                    .setRandomizedEncryptionRequired(true)
                    .setUnlockedDeviceRequired(true)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(-1)
                    .build()

            keyPairGenerator.initialize(keyGenParameterSpec)
            val keyPair = keyPairGenerator.genKeyPair()

            return Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
        }.onFailure { e -> e.printStackTrace() }

        return ""
    }

    private fun createSymmetricKey() {
        kotlin.runCatching {
            if (this.keyStore.containsAlias(SYMMETRIC_KEY_ALIAS)) {
                this.keyStore.deleteEntry(SYMMETRIC_KEY_ALIAS)
            }

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(SYMMETRIC_KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setInvalidatedByBiometricEnrollment(true)
//                .setIsStrongBoxBacked(true)
                    .setKeySize(SYMMETRIC_KEY_SIZE)
                    .setRandomizedEncryptionRequired(true)
                    .setUnlockedDeviceRequired(true)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(-1)
                    .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }.onFailure { e -> e.printStackTrace() }
    }

    private fun readPublicKey(): String {
        kotlin.runCatching {
            Base64.encodeToString(this.keyStore.getCertificate(ASYMMETRIC_KEYS_ALIAS).publicKey.encoded, Base64.NO_WRAP)
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
            val keyStore = KeyStore.getInstance(KEYSTORE_TYPE)
            keyStore.load(null)
            return keyStore
        }

    private val sharedPreferences: SharedPreferences
        get() {
            return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        }

    private val sharedPreferencesEditor: SharedPreferences.Editor
        get() {
            return this.sharedPreferences.edit()
        }
}