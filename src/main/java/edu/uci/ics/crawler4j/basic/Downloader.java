package edu.uci.ics.crawler4j.basic;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * This class is a demonstration of how crawler4j can be used to download a
 * single page and extract its title and text.
 */
public class Downloader {
    private static final Logger logger = LoggerFactory.getLogger(Downloader.class);

    private final Parser parser;
    private final PageFetcher pageFetcher;

    public Downloader() throws InstantiationException, IllegalAccessException {
        CrawlConfig config = new CrawlConfig();
        parser = new Parser(config);
        pageFetcher = new PageFetcher(config);
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Downloader downloader = new Downloader();
        downloader.processUrl("http://www.teknosa.com/");
    }

    public void processUrl(String url) {
        logger.debug("Processing: {}", url);
        Page page = download(url);
        if (page != null) {
            ParseData parseData = page.getParseData();
            if (parseData != null) {
                if (parseData instanceof HtmlParseData) {
                    HtmlParseData htmlParseData = (HtmlParseData) parseData;
                    logger.debug("Title: {}", htmlParseData.getTitle());
                    logger.debug("Text length: {}", htmlParseData.getText().length());
                    logger.debug("Html length: {}", htmlParseData.getHtml().length());
                }
            } else {
                logger.warn("Couldn't parse the content of the page.");
            }
        } else {
            logger.warn("Couldn't fetch the content of the page.");
        }
        logger.debug("==============");
    }

    private Page download(String url) {
        WebURL curURL = new WebURL();
        curURL.setURL(url);
        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchPage(curURL);
            if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
                Page page = new Page(curURL);
                fetchResult.fetchContent(page, pageFetcher.getConfig().getMaxDownloadSize());
                parser.parse(page, curURL.getURL());
                return page;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching url: " + curURL.getURL(), e);
        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }
        return null;
    }
}
