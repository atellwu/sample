package curator;

import java.util.concurrent.TimeUnit;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
 
public class TestDriver {
    private String mIdentifier;
    private CuratorFramework mClient;
    private InterProcessMutex mLock;
    private NodeCache mCache;
    private String mCachePath;
 
    public TestDriver(String pIdentifier,
                      String pConnectString,
                      RetryPolicy pRetryPolicy,
                      String pLockPath,
                      String pCachePath) {
        mIdentifier = pIdentifier;
        mClient = CuratorFrameworkFactory.newClient(pConnectString, pRetryPolicy);
        mClient.start();
        mLock = new InterProcessMutex(mClient, pLockPath);
        mCache = new NodeCache(mClient, pCachePath);
        NodeCacheListener listener = new TestListener(mCache);
        mCache.getListenable().addListener(listener);
        mCachePath = pCachePath;
    }
 
    public class CacheWriter implements Runnable {
        private String mCacheData;
 
        public CacheWriter(String pCacheData) {
            mCacheData = pCacheData;
        }
 
        @Override
        public void run() {
            try {
                Thread.sleep(200);
                System.out.printf("MAIN THREAD: Setting data to record %s\n", mCacheData);
                mClient.setData().forPath(mCachePath, mCacheData.getBytes());
                Thread.sleep(200);
            }
            catch (Exception exn) {
                // never mind
            }
        }
    }
 
    public class TestListener implements NodeCacheListener {
        private NodeCache mCache;
 
        public TestListener(NodeCache pCache) {
            mCache = pCache;
        }
 
        public void nodeChanged() throws Exception {
            String nodeData = new String(mCache.getCurrentData().getData(), "UTF-8");
            runWithLock(nodeData, new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    }
                    catch (InterruptedException exn) {
                        // never mind
                    }
                }
            });
        }
    }
 
    public void runWithLock(String pIdentifier, Runnable pBody) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.printf("%s: Starting lock section at %d\n", pIdentifier, startTime);
        if (mLock.acquire(5, TimeUnit.SECONDS)) {
            long acquiredTime = System.currentTimeMillis();
            System.out.printf("%s: Acquired lock at %d, took %d\n",
                    pIdentifier, acquiredTime, acquiredTime - startTime);
            pBody.run();
            mLock.release();
            long releasedTime = System.currentTimeMillis();
            System.out.printf("%s: Released lock at %d, took %d\n",
                    pIdentifier, releasedTime, releasedTime - startTime);
        }
        else {
            long failedTime = System.currentTimeMillis();
            System.out.printf("%s: Failed to acquire lock at %d, elapsed %d\n",
                    pIdentifier, failedTime, failedTime - startTime);
        }
    }
 
    public void start() throws Exception {
        mCache.start();
        while (true) {
            Thread.sleep(10000);
            long loopStartTime = System.currentTimeMillis();
            runWithLock("MAIN THREAD", new CacheWriter(mIdentifier + ";" + Long.toString(loopStartTime)));
        }
    }
}