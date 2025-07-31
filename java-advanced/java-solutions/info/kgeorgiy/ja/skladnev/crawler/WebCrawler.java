package info.kgeorgiy.ja.skladnev.crawler;

import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;

public class WebCrawler implements Crawler {
    final Downloader downloader;
    final int downloaders;
    final int extractors;
    final int perHost;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = downloaders;
        this.extractors = extractors;
        this.perHost = perHost;
    }

    @Override
    public Result download(String url, int depth) {
        return null;
    }

    @Override
    public void close() {

    }

    public static void main(String[] args) {

    }
}
