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

import jackzheng.study.com.wechat.utils.TextUtils;  

public class MyHttpServer {  
    //�������񣬼������Կͻ��˵�����  
    public static void httpserverService() throws IOException {  
        HttpServerProvider provider = HttpServerProvider.provider();  
        HttpServer httpserver =provider.createHttpServer(new InetSocketAddress(8967), 100);//�����˿�6666,��ͬʱ�� ��100������  
        httpserver.createContext("/myApp", new MyHttpHandler());   
        httpserver.setExecutor(null);  
        httpserver.start();  
        System.out.println("server started");  
    }  
    //Http��������  
    static class MyHttpHandler implements HttpHandler {  
        public void handle(HttpExchange httpExchange) throws IOException {  
            String responseMsg = "ok";   //��Ӧ��Ϣ  
            InputStream in = httpExchange.getRequestBody(); //���������  
            System.out.println("client handle:");  
            String queryString =  httpExchange.getRequestURI().getQuery();
            Map<String,String> queryStringInfo = formData2Dic(queryString);
            String takeName =  queryStringInfo.get("takename");
            String groupame =queryStringInfo.get("groupname");
            String takeId = responseMsg+" "+queryStringInfo.get("takeid");
            String type = responseMsg+" "+queryStringInfo.get("type");
            String message = responseMsg+" "+queryStringInfo.get("message");
            if(TextUtils.isEmpty(takeName) ||
            	TextUtils.isEmpty(message) ||
            	TextUtils.isEmpty(takeId)  ||
            	TextUtils.isEmpty(type) ){
            	responseMsg = "����ȱʧ";
            }else {
            	WechatApi.getIntance().receviceMessage(message, takeName,groupame ,type,takeId);
            }
            
            
            httpExchange.sendResponseHeaders(200, responseMsg.length()); //������Ӧͷ���Լ���Ӧ��Ϣ�ĳ���  
            OutputStream out = httpExchange.getResponseBody();  //��������  
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