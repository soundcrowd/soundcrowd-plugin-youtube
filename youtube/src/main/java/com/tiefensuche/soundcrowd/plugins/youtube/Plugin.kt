/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.soundcrowd.plugins.youtube

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaDataSource
import android.support.v4.media.MediaMetadataCompat
import com.tiefensuche.soundcrowd.extensions.MediaMetadataCompatExt
import com.tiefensuche.soundcrowd.plugins.Callback
import com.tiefensuche.soundcrowd.plugins.IPlugin
import org.json.JSONException
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.Page
import org.schabi.newpipe.extractor.ServiceList.YouTube
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory.MUSIC_SONGS
import org.schabi.newpipe.extractor.stream.StreamInfoItem
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
    private var nextPage: Page? = null

    init {
        NewPipe.init(Downloader(), Localization.DEFAULT)
    }

    override fun name(): String = name

    override fun mediaCategories(): List<String> = listOf(name)

    @Throws(Exception::class)
    override fun getMediaItems(mediaCategory: String, callback: Callback<List<MediaMetadataCompat>>, refresh: Boolean) {
        callback.onResult(getTrending())
    }

    @Throws(Exception::class)
    override fun getMediaItems(mediaCategory: String, path: String, query: String, callback: Callback<List<MediaMetadataCompat>>, refresh: Boolean) {
        callback.onResult(query(query, refresh))
    }

    @Throws(Exception::class)
    override fun getMediaUrl(metadata: MediaMetadataCompat, callback: Callback<Pair<MediaMetadataCompat, MediaDataSource?>>) {
        callback.onResult(Pair(MediaMetadataCompat.Builder(metadata)
            .putString(MediaMetadataCompatExt.METADATA_KEY_DOWNLOAD_URL,
                getAudioStream(metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))).build(), null))
    }

    private fun getTrending(): List<MediaMetadataCompat> {
        val extractor = YouTube.kioskList.getExtractorById("Trending", null)
        extractor.fetchPage()
        return extractItems(extractor.initialPage.items)
    }

    @Throws(IOException::class, ExtractionException::class, JSONException::class)
    private fun query(query: String?, reset: Boolean): List<MediaMetadataCompat> {
        if (query == null) {
            return emptyList()
        }

        val extractor = YouTube.getSearchExtractor(query, listOf(MUSIC_SONGS), "")
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

        nextPage = itemsPage.nextPage

        return extractItems(itemsPage.items)
    }

    private fun extractItems(items: List<InfoItem>): List<MediaMetadataCompat> {
        val results = LinkedList<MediaMetadataCompat>()
        for (item in items) {
            if (item is StreamInfoItem) {
                results.add(MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, item.url.substring(item.url.indexOf('=')+1))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, item.getName())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, item.uploaderName)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, item.getUrl())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, item.getThumbnailUrl())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, item.duration * 1000)
                        .putString(MediaMetadataCompatExt.METADATA_KEY_TYPE, MediaMetadataCompatExt.MediaType.MEDIA.name)
                    .build())
            }
        }

        return results
    }

    @Throws(ExtractionException::class, IOException::class)
    private fun getAudioStream(url: String): String {
        val extractor = YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        return extractor.audioStreams.maxBy { it.averageBitrate }.content
    }

    override fun getIcon(): Bitmap = icon
}
