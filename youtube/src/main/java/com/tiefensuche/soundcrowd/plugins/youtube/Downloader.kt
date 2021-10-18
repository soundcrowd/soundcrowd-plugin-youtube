/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.soundcrowd.plugins.youtube

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.util.concurrent.TimeUnit


class Downloader : org.schabi.newpipe.extractor.downloader.Downloader() {

    val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:68.0) Gecko/20100101 Firefox/68.0"
    private val client: OkHttpClient = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        var requestBody: RequestBody? = null
        if (dataToSend != null) {
            requestBody = RequestBody.create(null, dataToSend)
        }

        val requestBuilder = okhttp3.Request.Builder()
                .method(httpMethod, requestBody).url(url)
                .addHeader("User-Agent", USER_AGENT)

        for ((headerName, headerValueList) in headers) {
            if (headerValueList.size > 1) {
                requestBuilder.removeHeader(headerName)
                for (headerValue in headerValueList) {
                    requestBuilder.addHeader(headerName, headerValue)
                }
            } else if (headerValueList.size == 1) {
                requestBuilder.header(headerName, headerValueList[0])
            }
        }

        val response = client.newCall(requestBuilder.build()).execute()

        if (response.code == 429) {
            response.close()
            throw ReCaptchaException("reCaptcha Challenge requested", url)
        }

        val body = response.body
        var responseBodyToReturn: String? = null

        if (body != null) {
            responseBodyToReturn = body.string()
        }

        val latestUrl = response.request.url.toString()
        return Response(response.code,
            response.message, response.headers.toMultimap(), responseBodyToReturn, latestUrl)
    }
}