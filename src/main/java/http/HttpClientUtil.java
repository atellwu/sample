package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class HttpClientUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final DefaultHttpClient httpclient;

    static {
        HttpParams httpParams = new BasicHttpParams();
        // �������������  
        ConnManagerParams.setMaxTotalConnections(httpParams, 500);
        // ���û�ȡ���ӵ����ȴ�ʱ��  
        ConnManagerParams.setTimeout(httpParams, 5000);
        // ����ÿ��·�����������  
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(100);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        // �������ӳ�ʱʱ��  
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        // ���ö�ȡ��ʱʱ��  
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, registry);
        httpclient = new DefaultHttpClient(cm, httpParams);
    }

    public static String post(String url, List<? extends NameValuePair> nvps) throws IOException {
        long start = System.currentTimeMillis();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

        HttpEntity entity = null;
        String result = null;
        try {
            HttpResponse response = httpclient.execute(httpPost);
            entity = response.getEntity();
            InputStream ins = entity.getContent();
            result = IOUtils.toString(ins, "UTF-8");
            return result;
        } finally {
            EntityUtils.consume(entity);
            httpPost.releaseConnection();
            if (LOG.isDebugEnabled()) {
                LOG.debug("****** http client invoke (Post method), url: " + url + ", nameValuePair: " + nvps + ", result: "
                        + result + ", time: " + String.valueOf(System.currentTimeMillis() - start) + "ms.");
            }
        }
    }

    public static String postParts(String url, List<FormBodyPart> parts) throws IOException {
        long start = System.currentTimeMillis();

        HttpPost httpPost = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity();
        for (FormBodyPart part : parts) {
            reqEntity.addPart(part);
        }
        httpPost.setEntity(reqEntity);

        HttpEntity entity = null;
        String result = null;
        try {
            HttpResponse response = httpclient.execute(httpPost);
            entity = response.getEntity();
            InputStream ins = entity.getContent();
            result = IOUtils.toString(ins, "UTF-8");
            return result;
        } finally {
            EntityUtils.consume(entity);
            httpPost.releaseConnection();
            if (LOG.isDebugEnabled()) {
                LOG.debug("****** http client invoke (Post method), url: " + url + ", result: " + result + ", time: "
                        + String.valueOf(System.currentTimeMillis() - start) + "ms.");
            }
        }
    }

    public static String get(String url, List<? extends NameValuePair> nvps) throws IOException {
        long start = System.currentTimeMillis();

        //����nvpsΪqueryString
        if (nvps != null && nvps.size() > 0) {
            String query = URLEncodedUtils.format(nvps, "UTF-8");
            url += "?" + query;
        }
        HttpGet httpGet = new HttpGet(url);

        HttpEntity entity = null;
        String result = null;
        try {
            HttpResponse response = httpclient.execute(httpGet);
            entity = response.getEntity();
            InputStream ins = entity.getContent();
            result = IOUtils.toString(ins, "UTF-8");
            return result;
        } finally {
            EntityUtils.consume(entity);
            httpGet.releaseConnection();
            if (LOG.isDebugEnabled()) {
                LOG.debug("****** http client invoke (Get method), url: " + url + ", nameValuePair: " + nvps + ", result: "
                        + result + ", time: " + String.valueOf(System.currentTimeMillis() - start) + "ms.");
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        HttpURLConnection conn = null;
        try {
            String url = "http://localhost:8080/diamond-server/admin.do?method=listUser";
            conn = (HttpURLConnection) new URL(url ).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(5000);
            conn.addRequestProperty("Connection", "keep-alive"); 

            conn.connect();
            
//            System.out.println("===== header:" + conn.getHeaderFields());
//            int respCode = conn.getResponseCode(); // �����ڲ���������
            String resp = null;
//            System.out.println("===== code:"+respCode);

//            if (HttpURLConnection.HTTP_OK == respCode) {
                System.out.println("===== ok");
                resp = IOUtils.toString(conn.getInputStream(), "gbk");
//            } else {
//                resp = IOUtils.toString(conn.getErrorStream(), "gbk");
//            }
            System.out.println("===== end");
            System.out.println(resp);
        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
        }
        
        Thread.sleep(5000000);
    }
}
