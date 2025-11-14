/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.soundcrowd.plugins.youtube

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.tiefensuche.soundcrowd.plugins.IPlugin
import org.json.JSONException
import androidx.media3.common.MediaItem
import com.tiefensuche.soundcrowd.plugins.MediaItemUtils
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.Page
import org.schabi.newpipe.extractor.ServiceList.YouTube
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory.ALL
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory.MUSIC_SONGS
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory.MUSIC_VIDEOS
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.io.IOException
import java.util.*

/**
 * YouTube plugin for soundcrowd
 */
class Plugin(context: Context) : IPlugin {

    companion object {
        private const val NAME = "YouTube"
        private const val SEARCH_ALL = "All"
        private const val SEARCH_TRACKS = "YouTube Music Tracks"
        private const val SEARCH_VIDEOS = "YouTube Music Videos"
        private val searchCategories = listOf(SEARCH_ALL, SEARCH_TRACKS, SEARCH_VIDEOS)
    }

    private val icon: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_plugin_youtube)

    private var query: String? = null
    private var nextPage: Page? = null

    init {
        NewPipe.init(Downloader(), Localization.DEFAULT)
    }

    override fun name(): String = NAME

    override fun mediaCategories(): List<String> = listOf(NAME)

    @Throws(Exception::class)
    override fun getMediaItems(mediaCategory: String, refresh: Boolean): List<MediaItem> {
        return getTrending()
    }

    @Throws(Exception::class)
    override fun getMediaItems(mediaCategory: String, path: String, query: String, type: String, refresh: Boolean): List<MediaItem> {
        return query(query, when (type) {
            SEARCH_ALL -> ALL
            SEARCH_TRACKS -> MUSIC_SONGS
            SEARCH_VIDEOS -> MUSIC_VIDEOS
            else -> ALL
        }, refresh)
    }

    @Throws(Exception::class)
    override fun getMediaUri(mediaItem: MediaItem): Uri {
        return Uri.parse(getAudioStream(mediaItem.requestMetadata.mediaUri.toString()))
    }

    override fun getSuggestions(category: String, query: String): List<String> {
        return YouTube.suggestionExtractor.suggestionList(query)
    }

    override fun searchCategories(): List<String> {
        return searchCategories
    }

    private fun getTrending(): List<MediaItem> {
        val extractor = YouTube.kioskList.getExtractorById("trending_music", null)
        extractor.fetchPage()
        return extractItems(extractor.initialPage.items)
    }

    @Throws(IOException::class, ExtractionException::class, JSONException::class)
    private fun query(query: String?, type: String, reset: Boolean): List<MediaItem> {
        if (query == null) {
            return emptyList()
        }

        val extractor = YouTube.getSearchExtractor(query, listOf(type), "")
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

    private fun extractItems(items: List<InfoItem>): List<MediaItem> {
        val results = LinkedList<MediaItem>()
        for (item in items) {
            if (item is StreamInfoItem) {
                results.add(
                    MediaItemUtils.createMediaItem(
                        item.url.substringAfterLast('='),
                        Uri.parse(item.getUrl()),
                        item.getName(),
                        item.duration * 1000,
                        item.uploaderName,
                        artworkUri = Uri.parse(item.thumbnails.maxBy {
                            it.width * it.height
                        }.url)
                    )
                )
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
