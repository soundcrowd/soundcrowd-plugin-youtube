/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.soundcrowd.plugins.youtube

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.MediaMetadataCompat
import com.tiefensuche.soundcrowd.extensions.MediaMetadataCompatExt
import com.tiefensuche.soundcrowd.plugins.Callback
import com.tiefensuche.soundcrowd.plugins.IPlugin
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList.YouTube
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.stream.Stream
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.utils.Localization
import java.io.IOException
import java.util.*

/**
 * YouTube plugin for soundcrowd
 */
class Plugin(appContext: Context, context: Context) : IPlugin {

    companion object {
        private const val name = "YouTube"
    }

    private val icon: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.plugin_icon)

    private var query: String? = null
    private var nextPage: String? = null

    init {
        NewPipe.init(Downloader(), Localization("DE", "DE"))
    }

    override fun name(): String = name

    override fun mediaCategories(): List<String> = listOf(name)

    override fun preferences() = JSONArray()

    @Throws(Exception::class)
    override fun getMediaItems(mediaCategory: String, callback: Callback<JSONArray>) {
        callback.onResult(getTrending())
    }

    @Throws(Exception::class)
    override fun getMediaItems(mediaCategory: String, path: String, callback: Callback<JSONArray>) {
        // empty result, only search is supported
        callback.onResult(JSONArray())
    }

    @Throws(Exception::class)
    override fun getMediaItems(mediaCategory: String, path: String, query: String, callback: Callback<JSONArray>) {
        callback.onResult(query(query, false))
    }

    @Throws(Exception::class)
    override fun getMediaUrl(metadata: JSONObject, callback: Callback<JSONObject>) {
        getStreams(metadata, callback)
    }

    private fun getTrending(): JSONArray {
        val extractor = YouTube.kioskList.getExtractorById("Trending", null)
        extractor.fetchPage()
        return extractItems(extractor.initialPage.items)
    }

    @Throws(IOException::class, ExtractionException::class, JSONException::class)
    private fun query(query: String?, reset: Boolean): JSONArray {
        val results = JSONArray()
        if (query == null) {
            return results
        }

        val extractor = YouTube.getSearchExtractor(query)

        val itemsPage: ListExtractor.InfoItemsPage<InfoItem>

        if (!reset && query == this.query) {
            // get next page from previous query
            itemsPage = extractor.getPage(nextPage)
        } else {
            // new query, initial results
            this.query = query
            extractor.fetchPage()
            itemsPage = extractor.initialPage
        }

        nextPage = extractor.nextPageUrl

        return extractItems(itemsPage.items)
    }

    private fun extractItems(items: List<InfoItem>): JSONArray {
        val results = JSONArray()

        for (item in items) {
            if (item is StreamInfoItem) {
                val result = JSONObject()
                result.put(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, item.url.substring(item.url.indexOf('=')+1))
                        .put(MediaMetadataCompat.METADATA_KEY_TITLE, item.getName())
                        .put(MediaMetadataCompat.METADATA_KEY_ARTIST, item.uploaderName)
                        .put(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, item.getUrl())
                        .put(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, item.getThumbnailUrl())
                        .put(MediaMetadataCompat.METADATA_KEY_DURATION, item.duration * 1000)
                        .put(MediaMetadataCompatExt.METADATA_KEY_SOURCE, name)
                        .put(MediaMetadataCompatExt.METADATA_KEY_TYPE, MediaMetadataCompatExt.MediaType.MEDIA.name)
                results.put(result)
            }
        }

        return results
    }

    @Throws(ExtractionException::class, IOException::class)
    private fun getStreams(url: JSONObject, callback: Callback<JSONObject>) {
        val extractor = YouTube.getStreamExtractor(url.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
        extractor.fetchPage()

        val streams = ArrayList<Stream>()
        streams.addAll(extractor.audioStreams)

        for (stream in streams) {
            if (stream.getFormat().getName() == "m4a") {
                callback.onResult(url.put(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, stream.getUrl().replace("signature", "sig")))
                return
            }
        }
        throw IllegalStateException("no audio stream")
    }

    override fun getIcon() = icon
}
