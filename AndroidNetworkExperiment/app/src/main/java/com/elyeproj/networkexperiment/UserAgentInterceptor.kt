package com.elyeproj.networkexperiment

import android.content.Context
import android.os.Build
import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.Version
import java.io.IOException


class UserAgentInterceptor(context: Context) : Interceptor {

    companion object {
        private const val USER_AGENT = "User-Agent"
    }

    private val userAgent: String = "${getApplicationName(context)}/" +
            "${BuildConfig.VERSION_NAME} " +
            "(${context.packageName}; " +
            "build:${BuildConfig.VERSION_CODE} " +
            "Android SDK ${Build.VERSION.SDK_INT}) " +
            "${Version.userAgent()} " +
            getDeviceName()

    private fun getApplicationName(context: Context): String? {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    }

    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestWithUserAgent = request.newBuilder()
            .header(USER_AGENT, userAgent)
            .build()

        return chain.proceed(requestWithUserAgent)
    }
}
