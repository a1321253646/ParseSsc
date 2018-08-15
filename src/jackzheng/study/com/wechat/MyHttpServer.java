package jackzheng.study.com.wechat;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;  
import java.net.InetSocketAddress;  
  
import com.sun.net.httpserver.HttpExchange;  
import com.sun.net.httpserver.HttpHandler;  
import com.sun.net.httpserver.HttpServer;  
import com.sun.net.httpserver.spi.HttpServerProvider;  

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
            String temp = null;  
            System.out.println("client handle:");  
            while((temp = reader.readLine()) != null) {  
            	
                System.out.println("client request:"+temp);  
            }  
            httpExchange.sendResponseHeaders(200, responseMsg.length()); //������Ӧͷ���Լ���Ӧ��Ϣ�ĳ���  
            OutputStream out = httpExchange.getResponseBody();  //��������  
            out.write(responseMsg.getBytes());  
            out.flush();  
            httpExchange.close();                                 
              
        }  
    }  
    public static void main(String[] args) throws IOException {  
        httpserverService();  
    }  
}  