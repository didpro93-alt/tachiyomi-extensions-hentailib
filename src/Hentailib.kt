package eu.kanade.tachiyomi.extension.all.hentailib

import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.network.GET
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Hentailib : ParsedHttpSource() {

    override val name = "Hentailib"
    override val baseUrl = "https://hentailib.me"
    override val lang = "all"
    override val supportsLatest = true

    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/category/manga/page/$page", headers)
    }

    override fun popularMangaSelector() = ".manga-item"

    override fun popularMangaFromElement(element: Element) = SManga.create().apply {
        title = element.select("h3").text()
        setUrlWithoutDomain(element.select("a").attr("href"))
        thumbnail_url = element.select("img").attr("src")
    }

    override fun popularMangaNextPageSelector() = ".pagination a.next"

    override fun latestUpdatesRequest(page: Int) = popularMangaRequest(page)
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/?s=$query", headers)
    }

    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    override fun mangaDetailsParse(document: Document) = SManga.create().apply {
        title = document.select("h1").text()
        author = document.select(".author").text()
        description = document.select(".description").text()
        genre = document.select(".tags a").joinToString { it.text() }
        thumbnail_url = document.select(".cover img").attr("src")
    }

    override fun chapterListSelector() = ".chapter-list a"
    override fun chapterFromElement(element: Element) = SChapter.create().apply {
        name = element.text()
        setUrlWithoutDomain(element.attr("href"))
    }

    override fun pageListParse(document: Document): List<Page> {
        return document.select(".page-img img").mapIndexed { i, img ->
            Page(i, "", img.attr("src"))
        }
    }

    override fun imageUrlParse(document: Document) = document.select("img").attr("src")
}
