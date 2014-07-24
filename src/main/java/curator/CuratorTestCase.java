package curator;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.retry.BoundedExponentialBackoffRetry;

 
public class CuratorTestCase {
 
    public static final String zkConnectString = "localhost";
    public static final RetryPolicy zkRetryPolicy =
            new BoundedExponentialBackoffRetry(1000, 5000, 3);
    public static final String zkLockPath = "/lock";
    public static final String zkCachePath = "/cache";
 
    public static void main(String[] args) throws Exception {
        String nodeId = (args.length > 0) ? args[0] : "0";
        TestDriver driver = new TestDriver(
                nodeId,
                zkConnectString,
                zkRetryPolicy,
                zkLockPath,
                zkCachePath);
        driver.start();
    }
}
