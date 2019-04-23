/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.soundcrowd.plugins.youtube

import com.tiefensuche.soundcrowd.extensions.WebRequests
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.io.IOException
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection


internal class Downloader : org.schabi.newpipe.extractor.Downloader {

    /**
     * Download the text file at the supplied URL as in download(String),
     * but set the HTTP header field "Accept-Language" to the supplied string.
     *
     * @param siteUrl  the URL of the text file to return the contents of
     * @param language the language (usually a 2-character code) to set as the preferred language
     * @return the contents of the specified text file
     */
    @Throws(IOException::class, ReCaptchaException::class)
    override fun download(siteUrl: String, language: String): String {
        val requestProperties = HashMap<String, String>()
        requestProperties["Accept-Language"] = language
        return download(siteUrl, requestProperties)
    }


    /**
     * Download the text file at the supplied URL as in download(String),
     * but set the HTTP header field "Accept-Language" to the supplied string.
     *
     * @param siteUrl          the URL of the text file to return the contents of
     * @param customProperties set request header properties
     * @return the contents of the specified text file
     * @throws IOException
     */
    @Throws(IOException::class, ReCaptchaException::class)
    override fun download(siteUrl: String, customProperties: Map<String, String>): String {
        val url = URL(siteUrl)
        val con = url.openConnection() as HttpsURLConnection
        for ((key, value) in customProperties) {
            con.setRequestProperty(key, value)
        }
        return WebRequests.request(con)
    }

    /**
     * Download (via HTTP) the text file located at the supplied URL, and return its contents.
     * Primarily intended for downloading web pages.
     *
     * @param siteUrl the URL of the text file to download
     * @return the contents of the specified text file
     */
    @Throws(IOException::class, ReCaptchaException::class)
    override fun download(siteUrl: String): String {
        return WebRequests.get(siteUrl)
    }
}
