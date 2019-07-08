/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.soundcrowd.plugins.youtube

import com.tiefensuche.soundcrowd.extensions.WebRequests

import org.schabi.newpipe.extractor.DownloadRequest
import org.schabi.newpipe.extractor.DownloadResponse
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.utils.Localization

import java.io.IOException


class Downloader : org.schabi.newpipe.extractor.Downloader {

    @Throws(IOException::class, ReCaptchaException::class)
    override fun download(siteUrl: String): String {
        return WebRequests.get(siteUrl)
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun download(siteUrl: String, localization: Localization): String {
        return download(siteUrl)
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun download(siteUrl: String, customProperties: Map<String, String>): String {
        // this method is unneeded for the functionality of this plugin
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun get(siteUrl: String, request: DownloadRequest): DownloadResponse {
        // this method is unneeded for the functionality of this plugin
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun get(siteUrl: String): DownloadResponse {
        // this method is unneeded for the functionality of this plugin
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun post(siteUrl: String, request: DownloadRequest): DownloadResponse {
        // this method is unneeded for the functionality of this plugin
        throw UnsupportedOperationException()
    }
}