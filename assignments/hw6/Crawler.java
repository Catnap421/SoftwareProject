package hw6;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Crawler {

    public static void main(String args[]) throws MalformedURLException {
        int maxDepth = checkArgs(args);

        setupSslContext();

        List<UrlDepthPair> urlDepthPairList = new LinkedList<>();
        List<UrlDepthPair> resultUrlDepthPairList = new ArrayList<>();
        HashSet<String> visitedUrls = new HashSet<>();

        urlDepthPairList.add(new UrlDepthPair(args[0], 0));
        visitedUrls.add(args[0]);

        while(!urlDepthPairList.isEmpty()){
            UrlDepthPair urlDepthPair = urlDepthPairList.remove(0);
            resultUrlDepthPairList.add(urlDepthPair);

            connectUrl(urlDepthPairList, urlDepthPair, urlDepthPair.depth , maxDepth, visitedUrls);
        }

        for(UrlDepthPair urlDepthPair: resultUrlDepthPairList)
            System.out.println(urlDepthPair.getURLString() + "\t" + urlDepthPair.getDepth());

        System.out.println("Total Number of Urls: " + resultUrlDepthPairList.size());
    }

    public static int checkArgs(String args[]){
        if(args.length != 2){
            System.out.println("java Crawler <URL> <depth>");
            System.exit(0);
        }

        int maxDepth = 0;

        try {
            maxDepth = Integer.parseInt(args[1]);
        } catch (NumberFormatException e){
            System.out.println("<depth> must be Integer");
            System.exit(0);
        }

        return maxDepth;
    }

    /*
     * Set up SSL context for HTTPS support
     */
    public static void setupSslContext(){
        try {
            TrustManager[] trustAllCerts =
                    new TrustManager[]{ new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { System.exit(1); }
    }

    public static void connectUrl(List<UrlDepthPair> urlDepthPairList, UrlDepthPair urlDepthPair, int depth, int maxDepth, HashSet<String> visitedUrls){
        if(depth + 1 > maxDepth)
            return;

        try {
            HttpURLConnection conn = (HttpURLConnection) urlDepthPair.url.openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(3000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            for (String line; (line = in.readLine()) != null; ) {
                parseUrl(line, urlDepthPairList, depth, visitedUrls);
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseUrl(String line, List<UrlDepthPair> urlDepthPairList, int depth, HashSet<String> visitedUrls){
        int aTagIdx = line.indexOf("<a href=\"http");

        while(aTagIdx > 0) {
            int startDoubleQuotationIdx = line.indexOf("\"", aTagIdx);
            int nextDoubleQuotationIdx = line.indexOf("\"", startDoubleQuotationIdx + 1);

            try {
                String newUrl = line.substring(startDoubleQuotationIdx + 1, nextDoubleQuotationIdx);

                if (!visitedUrls.contains(newUrl)) {
                    urlDepthPairList.add(new UrlDepthPair(newUrl, depth + 1));
                    visitedUrls.add(newUrl);
                }

                aTagIdx = line.indexOf("<a href=\"http", nextDoubleQuotationIdx + 1);

            } catch (StringIndexOutOfBoundsException | MalformedURLException e) {
                System.out.println("Error Occurred Line: " + line.trim());
                e.printStackTrace();
                break;
            }
        }
    }
}
