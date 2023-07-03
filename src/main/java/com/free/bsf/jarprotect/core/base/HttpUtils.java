package com.free.bsf.jarprotect.core.base;

import com.free.bsf.jarprotect.core.util.FileUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 *
 * https://www.cnblogs.com/caoweixiong/p/14716187.html
 */
public class HttpUtils {
    public static class HttpRequest{
        String URL;
        String Method="GET";
        Map<String,String> Header;
        byte[] Body;
        int ConnectTimeOut=5000;
        int ReadTimeout=60000;
        Boolean KeepAlivePool=true;
    }

    public static class HttpResponse{
        int Code=-1;
        String Method;
        Map<String,String> Header;
        byte[] Body;

        public boolean isSuccess(){
            if(Code == 200){
                return true;
            }
            return false;
        }
    }
    public static HttpResponse request(HttpRequest httpRequest){
        HttpURLConnection conn=null;
        HttpResponse response = new HttpResponse();
        try {
            URL url = new URL(httpRequest.URL);
            response.Method =httpRequest.Method;
            conn = (HttpURLConnection)url.openConnection();
            // 获取请求方式
            conn.setRequestMethod(httpRequest.Method.toUpperCase());
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            //备注:底层http url connection 遇到get会自动转成post发出请求
            if(!"GET".equals(conn.getRequestMethod())) {
               conn.setDoOutput(true);
            }
            // 设置连接输入流为true
            conn.setDoInput(true);
            conn.setConnectTimeout(httpRequest.ConnectTimeOut);
            conn.setReadTimeout(httpRequest.ReadTimeout);
            conn.setUseCaches(false);
            conn.setRequestProperty("Accept-Charset","utf-8");
            //conn.setInstanceFollowRedirects(true);
            //理论上默认本身应该开启keep-alive,此处手工再加一次确保！
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 获取请求头部信息
            for (Map.Entry<String, String> kv:httpRequest.Header.entrySet()) {
                String key = kv.getKey();
                String value = kv.getValue();
                //默认
                if ("content-type".equalsIgnoreCase(key) && value == null) {
                    conn.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                } else {
                    conn.setRequestProperty(key, value);
                }
            }
            //备注: 强行抹去gzip等压缩,转发不做压缩处理,否则会涉及自解压。而且内网传输性能损耗不大。
            //conn.setRequestProperty("Accept-Encoding","");
            //tryAddPool(url,conn);
            long requestSize =0;
            conn.connect();
            if(conn.getDoOutput()) {
                // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                    if(httpRequest.Body!=null) {
                        byte[] bs = httpRequest.Body;
                        requestSize += bs.length;
                        // 将参数输出到连接
                        out.write(bs);
                        // 输出完成后刷新并关闭流
                        out.flush();
                    }
                }
            }
            response.Code=conn.getResponseCode();
            response.Header = new HashMap<>();
            for(Map.Entry<String, List<String>> filed:conn.getHeaderFields().entrySet()){
                response.Header.put(filed.getKey()==null? "":filed.getKey()  ,conn.getHeaderField(filed.getKey()));
            }
            try (InputStream in = getStream(conn)) {
                response.Body = FileUtils.toBytes(in);
            }
            return response;
        }catch (Exception e){
            if(response.Code<0) {
                response.Code = 404;
            }
            return  response;
        }finally {
            //释放连接以为不能复用连接池,但是可以避免连接溢出风险
            //底层太复杂,做好关闭连接池的配置预案,但是会降低性能
            if(httpRequest.KeepAlivePool==false) {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    private static InputStream getStream(HttpURLConnection conn) throws IOException {
        String encoding = conn.getContentEncoding();
        if("gzip".equalsIgnoreCase(encoding)){
            return new GZIPInputStream(conn.getInputStream());
        }else
            return conn.getInputStream();
    }

}
