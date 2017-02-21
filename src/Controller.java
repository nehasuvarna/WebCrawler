import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import java.io.*;
import java.util.HashMap;


/**
 * Created by neha on 2/15/17.
 */
public class Controller {

    static HashMap<Integer, String> sizeTextMap = new HashMap<>();
    public static void writeToFile() throws IOException{
        File textFile = new File("CrawlReport_abcnews.txt");
        BufferedWriter writer= new BufferedWriter(new FileWriter(textFile));
        writer.write("Name: Neha Suvarna Sanjeev\n");
        writer.write("USC ID: 3207131176\n");
        writer.write("News site crawled: abcnews.go.com\n");
        writer.newLine();

        writer.write("Fetch Statistics\n");
        writer.write("================\n");
        writer.write("# fetches attempted: " + MyCrawler.fetchAttempt);
        writer.write("\n# fetches succeeded: " + MyCrawler.fetchSucceeded);
        writer.write("\n# fetches aborted: " + MyCrawler.fetchAborted);
        writer.write("\n# fetches failed: " + MyCrawler.fetchFailed);
        writer.newLine();
        writer.newLine();

        writer.write("Outgoing URLs:\n");
        writer.write("==============\n");
        writer.write("Total URLs extracted:" + MyCrawler.totalURL);
        writer.write("\nunique URLs extracted:" + MyCrawler.uniqueURL);
        writer.write("\nunique URLs within News Site:" + MyCrawler.uniqueNewsURL);
        writer.write("\nunique URLs outside News Site:" + MyCrawler.uniqueNonNewsURL);
        writer.newLine();
        writer.newLine();

        writer.write("Status Codes:\n");
        writer.write("=============\n");
        for(int key : MyCrawler.statusCodes.keySet()){
            writer.write(key + ": " + MyCrawler.statusCodes.get(key)+"\n");
        }
        writer.newLine();

        writer.write("File Sizes:\n");
        writer.write("===========\n");
        for(int key : MyCrawler.fileSize.keySet()){
            writer.write(sizeTextMap.get(key) + ": " + MyCrawler.fileSize.get(key)+"\n");
        }
        writer.newLine();

        writer.write("Content Types:\n");
        writer.write("==============\n");
        for(String key : MyCrawler.contentMap.keySet()){
            writer.write(key + ": " + MyCrawler.contentMap.get(key)+"\n");
        }
        writer.newLine();
        writer.close();
    }

    public static void main(String[] args){
        String crawlStorageFolder = "data/crawl";
        String url = "http://abcnews.go.com/";
        int numberOfCrawlers = 8;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(16);
        config.setMaxPagesToFetch(20000);
        sizeTextMap.put(1,"< 1KB" );
        sizeTextMap.put(2,"1KB ~ <10KB" );
        sizeTextMap.put(3,"10KB ~ <100KB" );
        sizeTextMap.put(4,"100KB ~ <1MB" );
        sizeTextMap.put(5,">= 1MB" );
 /*
 * Instantiate the controller for this crawl.
 */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        try {
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
            /*
                * For each crawl, you need to add some seed urls. These are the first
                * URLs that are fetched and then the crawler starts following links
                * which are found in these pages
            */
            /*
                 * Start the crawl. This is a blocking operation, meaning that your code
                 * will reach the line after this only when crawling is finished.
            */

            controller.addSeed(url);
            controller.start(MyCrawler.class, numberOfCrawlers);

            writeToFile();

            System.out.println("Fetch Succeeded : " + MyCrawler.fetchSucceeded);
            System.out.println("Fetch Failed : " + MyCrawler.fetchFailed);
            System.out.println("Fetch Aborted : " + MyCrawler.fetchAborted);
            System.out.println("Fetch Attempted : " + MyCrawler.fetchAttempt);
            System.out.println("Fetch Difference : " + (MyCrawler.fetchAttempt - MyCrawler.fetchSucceeded-MyCrawler.fetchFailed-MyCrawler.fetchAborted));


            System.out.println("Total URLs  : " + MyCrawler.totalURL);
            System.out.println("Unique URLs  : " + MyCrawler.uniqueURL);
            System.out.println("Unique URLs as per HashSet : " + MyCrawler.urlMap.size());
            System.out.println("Unique News URLs  : " + MyCrawler.uniqueNewsURL);
            System.out.println("Unique Non News URLs  : " + MyCrawler.uniqueNonNewsURL);

            System.out.println("Status Codes  : " + MyCrawler.statusCodes.toString());
            System.out.println("File Size  : " + MyCrawler.fileSize.toString());
            System.out.println("Content Type  : " + MyCrawler.contentMap.toString());
        }
        catch(FileNotFoundException e){
            e.getMessage();
        }
        catch (Exception e){
            System.out.print(e.getMessage());
        }
    }
}
