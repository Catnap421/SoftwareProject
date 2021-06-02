package hw7;

import java.util.*;
import java.util.concurrent.*;

public class UrlPool {
    // List of pending urls to be crawled
    BlockingQueue<UrlDepthPair> pending_urls;
    // List of all the urls we've seen -- this forms the result
    List<UrlDepthPair> seen_urls;
    // Maximum crawl depth
    int maxDepth;
    // Count of waiting threads
    int waits;
    //Set of visited urls
    Set<String> visited_urls;

    // Constructor
    public UrlPool(int maxDepth) {
        this.maxDepth = maxDepth;
        pending_urls = new LinkedBlockingQueue<>();
        seen_urls = Collections.synchronizedList(new LinkedList<>());
        waits = 0;
        visited_urls = new HashSet<>();
    }

    // Get the next UrlDepthPair to crawl
    public UrlDepthPair getNextPair() {
        addWaitCount(1);
        UrlDepthPair pair;
        try {
            pair = pending_urls.take();
        } catch (InterruptedException e) {
            pair = null;
        }
        addWaitCount(-1);

        return pair;
    }

    // Add a new pair to the pool if the depth is
    // less than the maximum depth to be considered.
    public void addPair(UrlDepthPair pair) {
        if(checkVisited(pair))
            return;

        seen_urls.add(pair);

        if (pair.getDepth() < maxDepth)
            try { pending_urls.put(pair); } catch (InterruptedException e) { }
    }
    
    // Get the number of waiting threads
    public synchronized int getWaitCount() {
        return waits;
    }
    
    // Get all the urls seen
    public List<UrlDepthPair> getSeenUrls() {
        return seen_urls;
    }

    // Add number to the wait count
    public synchronized void addWaitCount(int num) {
        waits += num;
    }

    // Check the url is visited and if not, add url to the set of visited urls
    public synchronized boolean checkVisited(UrlDepthPair pair){
        if (visited_urls.contains(pair.getURLString()))
            return true;

        visited_urls.add(pair.getURLString());
        return false;
    }

}
