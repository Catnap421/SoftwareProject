package hw5;

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
        if(args.length != 2){
            System.out.println("java Crawler <URL> <depth>");
            System.exit(0);
        }

        setupSslContext();

        int maxDepth = Integer.parseInt(args[1]);

        List<UrlDepthPair> urlDepthPairList = new LinkedList<>();
        List<UrlDepthPair> printUrlDepthPairList = new ArrayList<>();
        HashSet<String> visitedUrls = new HashSet<>();

        urlDepthPairList.add(new UrlDepthPair(args[0], 0));
        visitedUrls.add(args[0]);

        while(urlDepthPairList.size() > 0){
            UrlDepthPair urlDepthPair = urlDepthPairList.remove(0);
            printUrlDepthPairList.add(urlDepthPair);

            addUrl(urlDepthPairList, urlDepthPair, urlDepthPair.depth , maxDepth, visitedUrls);
        }

        for(UrlDepthPair urlDepthPair: printUrlDepthPairList)
            System.out.println(urlDepthPair.getURLString() + " " + urlDepthPair.getDepth());

        System.out.println(printUrlDepthPairList.size());
    }

    public static void setupSslContext(){
        // Set up SSL context for HTTPS support
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

    public static void addUrl(List<UrlDepthPair> urlDepthPairList, UrlDepthPair urlDepthPair, int depth, int maxDepth, HashSet<String> visitedUrls){
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
                int fromIdx = 0;
                int startLinkIdx = line.indexOf("<a href=\"http", fromIdx);
                if (startLinkIdx < 0) continue;

                int startIdx = line.indexOf("\"", startLinkIdx);
                int lastIdx = line.indexOf("\"", startIdx + 1);

                String newUrl;

                try {
                    newUrl = line.substring(startIdx + 1, lastIdx);
                } catch (StringIndexOutOfBoundsException e) {
                    continue;
                }

//                System.out.println(line + "\t" + newUrl+ "\n");

                try {
                    if (!visitedUrls.contains(newUrl)) {
                        urlDepthPairList.add(new UrlDepthPair(newUrl, depth + 1));
                        visitedUrls.add(newUrl);
                    }
                } catch(MalformedURLException e){
                    continue;
                }
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
