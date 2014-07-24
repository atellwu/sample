package classpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class TestClasspath {

    public static void main(String[] args) throws IOException, InterruptedException {
        URL resource = TestClasspath.class.getResource("/LICENSE.txt");//��getResources�������ǣ������ص�һ���ҵ���
        //�����
        //jar:file:/home/kezhu/.m2/repository/cglib/cglib/2.2.2/cglib-2.2.2.jar!/NOTICE
        System.out.println(resource);
        
        Enumeration<URL> list =  Thread.currentThread().getContextClassLoader().getResources("/LICENSE.txt");
        while (list.hasMoreElements()) {
                  URL url = list.nextElement();
                  /*
                   * �����
                   * jar:file:/home/kezhu/.m2/repository/cglib/cglib/2.2.2/cglib-2.2.2.jar!/NOTICE
                   * jar:file:/home/kezhu/.m2/repository/org/mockito/mockito-core/1.9.5/mockito-core-1.9.5.jar!/NOTICE
                   * jar:file:/home/kezhu/.m2/repository/cglib/cglib-nodep/2.2/cglib-nodep-2.2.jar!/NOTICE
                   */
                  System.out.println(url);
        }
        
//        TimeUnit.SECONDS.sleep(3000000);
    }

}
