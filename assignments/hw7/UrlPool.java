package hw7;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class UrlPool {
    // List of pending urls to be crawled
    BlockingQueue<UrlDepthPair> pending_urls;
    // List of all the urls we've seen -- this forms the result
    List<UrlDepthPair> seen_urls;
    // Maximum crawl depth
    int maxDepth;
    // Count of waiting threads
    AtomicInteger waits;
    // Thread-safe Set of visited urls
    Set<String> visited_urls;

    // Constructor
    public UrlPool(int maxDepth) {
        this.maxDepth = maxDepth;
        pending_urls = new LinkedBlockingQueue<>();
        seen_urls = new LinkedList<>();
        waits = new AtomicInteger(0);
        visited_urls = Collections.synchronizedSet(new HashSet<>());
    }
    
    // Get the next UrlDepthPair to crawl
    public UrlDepthPair getNextPair() {
        waits.set(getWaitCount()+ 1);
        UrlDepthPair pair;
        try {
            pair = pending_urls.take();
        } catch (InterruptedException e) {
            pair = null;
        }
        waits.set(getWaitCount() - 1);

        return pair;
    }

    // Add a new pair to the pool if the depth is
    // less than the maximum depth to be considered.
    public void addPair(UrlDepthPair pair) {
        if(visited_urls.contains(pair.getURLString()))
            return;

        visited_urls.add(pair.getURLString());
        seen_urls.add(pair);

        if (pair.getDepth() < maxDepth)
            try { pending_urls.put(pair); } catch (InterruptedException e) { }

    }
    
    // Get the number of waiting threads
    public synchronized int getWaitCount() {
        return waits.get();
    }
    
    // Get all the urls seen
    public List<UrlDepthPair> getSeenUrls() {
        return seen_urls;
    }
}
