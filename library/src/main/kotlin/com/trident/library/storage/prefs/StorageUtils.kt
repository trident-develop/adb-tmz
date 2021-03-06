package com.trident.library.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class StorageUtils {

    class Preferences(context: Context, name: String, secureKey: String, boolean: Boolean){
        private val sharedPrefs: SecurePreferences =
            SecurePreferences(
                context,
                name,
                secureKey,
                boolean
            )


        fun setOnConversionDataSuccess(name: String, flag: String) {
            sharedPrefs.put(name, flag)
        }

        fun getOnConversionDataSuccess(name: String): String {
            return sharedPrefs.getString(name).toString()
        }



        fun setOnGameLaunched(name: String, flag: String){
            sharedPrefs.put(name, flag)
        }

        fun getOnGameLaunched(name: String): String{
            return sharedPrefs.getString(name).toString()
        }

        fun setOnWebLaunched(name: String, flag: String){
            sharedPrefs.put(name, flag)
        }

        fun getOnWebLaunched(name: String): String{
            return sharedPrefs.getString(name).toString()
        }

        fun setOnLastUrlNumber(num: String){
            sharedPrefs.put("number", num)
        }

        fun getOnLastUrlNumber(): String{
            return sharedPrefs.getString("number").toString()
        }

        fun setOnRemoteStatus(status: String){
            sharedPrefs.put("status", status)
        }

        fun getOnRemoteStatus(): String{
            return sharedPrefs.getString("status").toString()
        }
    }

    class SecurePreferences(
        context: Context,
        preferenceName: String?,
        secureKey: String,
        encryptKeys: Boolean
    ) {
        class SecurePreferencesException(e: Throwable?) :
            RuntimeException(e)


        //???????????????? ????????????
        private var encryptKeys = false
        private var writer: Cipher? = null
        private var reader: Cipher? = null
        private var keyWriter: Cipher? = null
        private var preferences: SharedPreferences? = null

        //??????????????????
        @Throws(
            UnsupportedEncodingException::class,
            NoSuchAlgorithmException::class,
            InvalidKeyException::class,
            InvalidAlgorithmParameterException::class
        )
        //???????????????????????????? ?????????????????? ????????????????????
        protected fun initCiphers(secureKey: String) {
            val ivSpec = iv
            val secretKey = getSecretKey(secureKey)
            writer!!.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            reader!!.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            keyWriter!!.init(Cipher.ENCRYPT_MODE, secretKey)
        }

        //?????????????????????????? ?????????? ?? ??????????????
        protected val iv: IvParameterSpec
            protected get() {
                val iv = ByteArray(writer!!.blockSize)
                System.arraycopy(
                    "fldsjfodasjifudslfjdsaofshaufihadsf".toByteArray(),
                    0,
                    iv,
                    0,
                    writer!!.blockSize
                )
                return IvParameterSpec(iv)
            }

        @Throws(
            UnsupportedEncodingException::class,
            NoSuchAlgorithmException::class
        )
        //???????????????? ?????????????????? ????????
        protected fun getSecretKey(key: String): SecretKeySpec {
            val keyBytes = createKeyBytes(key)
            return SecretKeySpec(
                keyBytes,
                TRANSFORMATION
            )
        }

        @Throws(
            UnsupportedEncodingException::class,
            NoSuchAlgorithmException::class
        )
        //?????? ?? ?????????????????????????? ???????? ??????
        protected fun createKeyBytes(key: String): ByteArray {
            val md =
                MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION)
            md.reset()
            return md.digest(key.toByteArray(charset(CHARSET)))
        }

        //???????????? ????????
        fun put(key: String, value: String?) {
            if (value == null) {
                preferences!!.edit().remove(toKey(key)).commit()
            } else {
                putValue(toKey(key), value)
            }
        }


        @Throws(SecurePreferencesException::class)
        fun getString(key: String): String? {
            if (preferences!!.contains(toKey(key))) {
                val securedEncodedValue = preferences!!.getString(toKey(key), "")
                return decrypt(securedEncodedValue)
            }
            return null
        }


        private fun toKey(key: String): String {
            return if (encryptKeys) encrypt(key, keyWriter) else key
        }

        @Throws(SecurePreferencesException::class)
        private fun putValue(key: String, value: String) {
            val secureValueEncoded = encrypt(value, writer)
            preferences!!.edit().putString(key, secureValueEncoded).commit()
        }

        //??????????????
        @Throws(SecurePreferencesException::class)
        protected fun encrypt(value: String, writer: Cipher?): String {
            val secureValue: ByteArray
            secureValue = try {
                convert(
                    writer,
                    value.toByteArray(charset(CHARSET))
                )
            } catch (e: UnsupportedEncodingException) {
                throw SecurePreferencesException(
                    e
                )
            }
            return Base64.encodeToString(secureValue, Base64.NO_WRAP)
        }

        //??????????????????????
        protected fun decrypt(securedEncodedValue: String?): String {
            val securedValue =
                Base64.decode(securedEncodedValue, Base64.NO_WRAP)
            val value =
                convert(
                    reader,
                    securedValue
                )
            return try {
                String(value, Charset.forName(CHARSET))
            } catch (e: UnsupportedEncodingException) {
                throw SecurePreferencesException(
                    e
                )
            }
        }

        //?????????????? ?????????????????????????? ?? ??????????????????
        companion object {
            private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
            private const val KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding"
            private const val SECRET_KEY_HASH_TRANSFORMATION = "SHA-256"
            private const val CHARSET = "UTF-8"

            @Throws(SecurePreferencesException::class)
            private fun convert(cipher: Cipher?, bs: ByteArray): ByteArray {
                return try {
                    cipher!!.doFinal(bs)
                } catch (e: Exception) {
                    throw SecurePreferencesException(
                        e
                    )
                }
            }
        }

        //???????????????????????????? ?????? ??????????????????????
        init {
            try {
                writer =
                    Cipher.getInstance(TRANSFORMATION)
                reader =
                    Cipher.getInstance(TRANSFORMATION)
                keyWriter =
                    Cipher.getInstance(KEY_TRANSFORMATION)
                initCiphers(secureKey)
                preferences =
                    context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
                this.encryptKeys = encryptKeys
            } catch (e: GeneralSecurityException) {
                throw SecurePreferencesException(
                    e
                )
            } catch (e: UnsupportedEncodingException) {
                throw SecurePreferencesException(
                    e
                )
            }
        }
    }

}