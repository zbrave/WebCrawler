package edu.uci.ics.crawler4j.basic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class LocalDataCollectorCrawlerTeknosa extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(LocalDataCollectorCrawlerTeknosa.class);

    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
        "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    CrawlStat myCrawlStat;
    HtmlWriter htmlfile;

    public LocalDataCollectorCrawlerTeknosa() {
        myCrawlStat = new CrawlStat();
        htmlfile = new HtmlWriter();
        htmlfile.open();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && (href.startsWith("http://www.istanbulbilisim.com.tr/"));
    }

    @Override
    public void visit(Page page) {
        logger.info("Visited: {}", page.getWebURL().getURL());
        myCrawlStat.incProcessedPages();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData parseData = (HtmlParseData) page.getParseData();
            Set<WebURL> links = parseData.getOutgoingUrls();
            List<WebURL> list = new ArrayList<WebURL>();
            list.addAll(links);
            logger.info("Outgoings: "+links.size()+" "+parseData.getTitle());
            myCrawlStat.incTotalLinks(links.size());
            // Parse data
            String html = parseData.getHtml();
            Document doc = Jsoup.parseBodyFragment(html);
//            Elements info = doc.select("a.product");
            // for vatancomp. add html just products
            Elements info = doc.select("script[type=application/ld+json]");//<script type="application/ld+json">
            if (!info.html().isEmpty() && html.contains("'ecomm_pcat': 'Cep Telefonu'")){
            	logger.info("Ürünler seçildi... -> "+info.html());
            	
            	System.out.println(page.getWebURL().toString());
            	try {
            		DbDAO d = new DbDAO();
            		for (int i=0; i<list.size(); i++) {
            			try {
            				System.out.println("adding"+list.get(i).getURL());
							d.addDBout(page.getWebURL().toString(), list.get(i).getURL());
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						}
            		}
					htmlfile.addForTeknosa(info.html(), page.getWebURL().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            try {
                myCrawlStat.incTotalTextSize(parseData.getText().getBytes("UTF-8").length);
            } catch (UnsupportedEncodingException ignored) {
                // Do nothing
            }
        }
        // We dump this crawler statistics after processing every 50 pages
        if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
            dumpMyData();
            htmlfile.close();
        }
    }

    /**
     * This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    @Override
    public void onBeforeExit() {
        dumpMyData();
    }

    public void dumpMyData() {
        int id = getMyId();
        // You can configure the log to output to file
        logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
        logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
        logger.info("Crawler {} > Total Text Size: {}", id, myCrawlStat.getTotalTextSize());
    }
}