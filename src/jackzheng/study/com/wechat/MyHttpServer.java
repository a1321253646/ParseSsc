package jackzheng.study.com.wechat;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;  
import com.sun.net.httpserver.HttpHandler;  
import com.sun.net.httpserver.HttpServer;  
import com.sun.net.httpserver.spi.HttpServerProvider;  

public class MyHttpServer {  
    //启动服务，监听来自客户端的请求  
    public static void httpserverService() throws IOException {  
        HttpServerProvider provider = HttpServerProvider.provider();  
        HttpServer httpserver =provider.createHttpServer(new InetSocketAddress(8967), 100);//监听端口6666,能同时接 受100个请求  
        httpserver.createContext("/myApp", new MyHttpHandler());   
        httpserver.setExecutor(null);  
        httpserver.start();  
        System.out.println("server started");  
    }  
    //Http请求处理类  
    static class MyHttpHandler implements HttpHandler {  
        public void handle(HttpExchange httpExchange) throws IOException {  
            String responseMsg = "ok";   //响应信息  
            InputStream in = httpExchange.getRequestBody(); //获得输入流  
            System.out.println("client handle:");  
            String queryString =  httpExchange.getRequestURI().getQuery();
            Map<String,String> queryStringInfo = formData2Dic(queryString);
            responseMsg = responseMsg+" "+queryStringInfo.get("rt");
            responseMsg = responseMsg+" "+queryStringInfo.get("ie");
            httpExchange.sendResponseHeaders(200, responseMsg.length()); //设置响应头属性及响应信息的长度  
            OutputStream out = httpExchange.getResponseBody();  //获得输出流  
            out.write(responseMsg.getBytes());  
            out.flush();  
            httpExchange.close();                                 
              
        }  
    }  
    
    public static Map<String,String> formData2Dic(String formData ) {
        Map<String,String> result = new HashMap<>();
        if(formData== null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split("&");
        Arrays.stream(items).forEach(item ->{
            final String[] keyAndVal = item.split("=");
            if( keyAndVal.length == 2) {
                try{
                    final String key = URLDecoder.decode( keyAndVal[0],"utf8");
                    final String val = URLDecoder.decode( keyAndVal[1],"utf8");
                    result.put(key,val);
                }catch (UnsupportedEncodingException e) {}
            }
        });
        return result;
    }
    
    public static void main(String[] args) throws IOException {  
        httpserverService();  
    }  
}  