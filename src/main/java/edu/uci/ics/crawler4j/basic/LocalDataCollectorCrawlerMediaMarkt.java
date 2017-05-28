package edu.uci.ics.crawler4j.basic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class LocalDataCollectorCrawlerMediaMarkt extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(LocalDataCollectorCrawlerMediaMarkt.class);

    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
        "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    CrawlStat myCrawlStat;
    HtmlWriter htmlfile;

    public LocalDataCollectorCrawlerMediaMarkt() {
        myCrawlStat = new CrawlStat();
        htmlfile = new HtmlWriter();
        htmlfile.open();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && (href.startsWith("http://www.mediamarkt.com.tr/"));
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
            Elements ogTags = doc.select("meta[property^=og:]");
            Elements prTags = doc.select("meta[property^=product:]");
            if (ogTags.size() <= 0 || prTags.size() <= 0) {
                return;
            }

            // Set OGTags you want
            String title=null;
            String type=null;
            String image=null;
            String url=null;
            String price=null;
            String brand=null;
            String cur=null;
            System.out.println("OGler alındı...");
            for (int i = 0; i < ogTags.size(); i++) {
                Element tag = ogTags.get(i);

                String text = tag.attr("property");
                if ("og:image".equals(text)) {
                    image = tag.attr("content");
                } else if ("og:type".equals(text)) {
                    type = tag.attr("content");
                } else if ("og:title".equals(text)) {
                    title = tag.attr("content");
                } else if ("og:url".equals(text)) {
                    url = tag.attr("content");
                }
            }
            for (int i = 0; i < prTags.size(); i++) {
                Element tag = prTags.get(i);

                String text = tag.attr("property");
                if ("product:price:amount".equals(text)) {
                    price = tag.attr("content");
                } else if ("product:price:currency".equals(text)) {
                    cur = tag.attr("content");
                } else if ("product:brand".equals(text)) {
                    brand = tag.attr("content");
                }
            }
            System.out.println("ye:"+title+brand+cur+image+price);
            if (type.equals("og:product")) {
            	Product p = new Product();
            	p.setImgUrl(image);
            	p.setLink(url);
            	p.setPrice(Float.parseFloat(price));
            	p.setBrand(brand);
            	p.setResource("MediaMarkt");
            	p.setName(title);
            	DbDAO d = new DbDAO();
            	try {
					d.addDB(p);
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
				} catch (ClassNotFoundException | SQLException e) {
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