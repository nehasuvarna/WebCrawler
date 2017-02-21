import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by neha on 2/15/17.
 **/
public class MyCrawler extends WebCrawler{
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|"
            + "|mp3|mp3|zip|gz))$");
    BufferedWriter fetch, visit,urls;
    File fetchFile = new File("fetch_abcnews.csv");
    File urlsFile = new File("urls_abcnews.csv");
    File visitFile = new File("visit_abcnews.csv");
    static int fetchSucceeded = 0,fetchFailed, fetchAttempt, fetchAborted;
    static int totalURL, uniqueURL, uniqueNewsURL, uniqueNonNewsURL;
    static HashMap<Integer, Integer> statusCodes = new HashMap<>();
    static HashMap<Integer, Integer> fileSize = new HashMap<>();
    static HashMap<String, Integer> contentMap = new HashMap<>();
    static HashMap<String,Integer> urlMap = new HashMap<>();

    /**
 * This method receives two parameters. The first parameter is the page
 * in which we have discovered this new url and the second parameter is
 * the new url. You should implement this function to specify whether
 * the given url should be crawled or not (based on your crawling logic).
 * In this example, we are instructing the crawler to ignore urls that
 * have css, js, git, ... extensions and to only accept urls that start
 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
 * referringPage parameter to make the decision.
 **/

    public void addFileSize(int size){
        int key=0;
        if(size < 1024) key = 1;
        else if(size >= 1024 && size<10*1024) key = 2;
        else if(size >= 10*1024 && size<100*1024) key = 3;
        else if(size >= 100*1024 && size<1000*1024) key = 4;
        else if(size >= 1000*1024) key = 5;

        if(fileSize.get(key)==null)
            fileSize.put(key,1);
        else
            fileSize.put(key,fileSize.get(key)+1);
    }

    public void addContentType(String contentType){
        if(contentMap.get(contentType)==null)
            contentMap.put(contentType,1);
        else
            contentMap.put(contentType,contentMap.get(contentType)+1);
    }

    public void fetchWriters(String url, int code){
        try {
            fetch = new BufferedWriter(new FileWriter(fetchFile,true));
            StringBuilder str = new StringBuilder();
            if(fetchFile.length() == 0){
                str.append("URL");
                str.append(',');
                str.append("HTTP status code");
                str.append('\n');
            }
            str.append(url);
            str.append(',');
            str.append(String.valueOf(code));
            str.append('\n');

            fetch.write(str.toString());
            fetch.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }


    }

    public void visitWriters(String url, double size, int outlink, String type){
        try {
            visit = new BufferedWriter(new FileWriter(visitFile ,true));
            StringBuilder str = new StringBuilder();
            if(visitFile.length() == 0){
                str.append("URL");
                str.append(',');
                str.append("Size");
                str.append(',');
                str.append("Number of outlinks");
                str.append(',');
                str.append("Content-Type");
                str.append('\n');
            }
            str.append(url);
            str.append(',');
            str.append(String.valueOf(size));
            str.append(',');
            str.append(String.valueOf(outlink));
            str.append(',');
            str.append(type);
            str.append('\n');

            visit.write(str.toString());
            visit.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void urlWriters(String url, String indicator){
        try {
            urls = new BufferedWriter(new FileWriter(urlsFile ,true));
            StringBuilder str = new StringBuilder();
            if(urlsFile.length() == 0){
                str.append("URL");
                str.append(',');
                str.append("Indicator");
                str.append('\n');
            }
            str.append(url);
            str.append(',');
            str.append(indicator);
            str.append('\n');

            urls.write(str.toString());
            urls.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean shouldVisit(Page referringPage, WebURL weburl) {
        String href = weburl.getURL().toLowerCase();

        boolean unique = (urlMap.get(href) == null)?true:false;

        if(unique && href.startsWith("http://abcnews.go.com/")){
            urlMap.put(href,1);
            uniqueURL++;
            uniqueNewsURL++;
            //urlWriters(checkUrl,"OK");
        }
        else if (unique && !href.startsWith("http://abcnews.go.com/")){
            urlMap.put(href,1);
            uniqueURL++;
            uniqueNonNewsURL++;
            //urlWriters(checkUrl,"N_OK");
        }
        return !FILTERS.matcher(href).matches()
            && href.startsWith("http://abcnews.go.com/");
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        String pageURL = webUrl.getURL();
        fetchAttempt++;
        fetchWriters(pageURL,statusCode);
        if(statusCode == 200)
            fetchSucceeded++;
        else if(statusCode >= 300 && statusCode< 400)
            fetchAborted++;
        else
            fetchFailed++;

        if(statusCodes.get(statusCode) != null)
            statusCodes.put(statusCode, statusCodes.get(statusCode)+1);
        else
            statusCodes.put(statusCode,1);
    }



    @Override
    public void visit(Page page){
        String url = page.getWebURL().getURL();
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            totalURL += links.size();
            addFileSize(html.length());
            String []content= page.getContentType().split(";");
            if(content[0].equals("")) content[0] = "text/plain";
            addContentType(content[0]);
            visitWriters(url,html.length(),links.size(), content[0]);
        }
    }
}
