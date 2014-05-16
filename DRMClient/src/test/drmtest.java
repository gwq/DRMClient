package test;

import java.io.IOException;  
import java.io.UnsupportedEncodingException;  
import java.security.KeyManagementException;  
import java.security.NoSuchAlgorithmException;  
import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager;  
  
import org.apache.http.HttpEntity;  
import org.apache.http.HttpResponse;  
import org.apache.http.NameValuePair;  
import org.apache.http.ParseException;  
import org.apache.http.client.ClientProtocolException;  
import org.apache.http.client.HttpClient;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.conn.scheme.Scheme;  
import org.apache.http.conn.ssl.SSLSocketFactory;  
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.util.EntityUtils;  
  
/** 
 * @see ===================================================================================================== 
 * @see 在开发HTTPS应用时，时常会遇到两种情况 
 * @see 1、要么测试服务器没有有效的SSL证书,客户端连接时就会抛异常 
 * @see    javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated 
 * @see 2、要么测试服务器有SSL证书,但可能由于各种不知名的原因,它还是会抛一堆烂码七糟的异常 
 * @see ===================================================================================================== 
 * @see 由于我们这里使用的是HttpComponents-Client-4.1.2创建的连接，所以，我们就要告诉它使用一个不同的TrustManager 
 * @see TrustManager是一个用于检查给定的证书是否有效的类 
 * @see SSL使用的模式是X.509....对于该模式,Java有一个特定的TrustManager,称为X509TrustManager 
 * @see 所以我们自己创建一个X509TrustManager实例 
 * @see 而在X509TrustManager实例中，若证书无效，那么TrustManager在它的checkXXX()方法中将抛出CertificateException 
 * @see 既然我们要接受所有的证书,那么X509TrustManager里面的方法体中不抛出异常就行了 
 * @see 然后创建一个SSLContext并使用X509TrustManager实例来初始化之 
 * @see 接着通过SSLContext创建SSLSocketFactory，最后将SSLSocketFactory注册给HttpClient就可以了 
 * @see ===================================================================================================== 
 * @create Jul 30, 2012 1:11:52 PM 
 * @author 玄玉(http://blog.csdn/net/jadyer) 
 */  
public class drmtest {  
    public static void main(String[] args)throws Exception{  
        Map<String, String> params = new HashMap<String, String>();  
        params.put("TransName", "IQSR");  
        params.put("Plain", "transId=IQSR~|~originalorderId=2012~|~originalTransAmt= ~|~merURL= ");  
        params.put("Signature", "9b759887e6ca9d4c24509d22ee4d22494d0dd2dfbdbeaab3545c1acee62eec7");  
        sendSSLPostRequest("https://www.cebbank.com/per/QueryMerchantEpay.do", params);  
    }  
      
    /** 
     * 向HTTPS地址发送POST请求 
     * @see 该方法会自动关闭连接,释放资源 
     * @param reqURL 请求地址 
     * @param params 请求参数 
     * @return 响应内容 
     */  
    public static String sendSSLPostRequest(String reqURL, Map<String, String> params){  
        long responseLength = 0;                         //响应长度   
        String responseContent = "";                     //响应内容   
        HttpClient httpClient = new DefaultHttpClient(); //创建默认的httpClient实例   
        X509TrustManager xtm = new X509TrustManager(){   //创建TrustManager   
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}  
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}  
            public X509Certificate[] getAcceptedIssuers() { return null; }  
        };  
        try {  
            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext   
            SSLContext ctx = SSLContext.getInstance("TLS");  
              
            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用   
            ctx.init(null, new TrustManager[]{xtm}, null);  
              
            //创建SSLSocketFactory 且 不校验域名 
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
            //*********不校验域名(该方法已过期，现在直接在SSLSocketFactory构造函数中设置参数，如上行代码所示)*********
            //socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);   
              
            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上   
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));  
              
            HttpPost httpPost = new HttpPost(reqURL);                        //创建HttpPost   
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); //构建POST请求的表单参数   
            for(Map.Entry<String,String> entry : params.entrySet()){  
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
            }  
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));  
           
            HttpResponse response = httpClient.execute(httpPost); //执行POST请求   
            HttpEntity entity = response.getEntity();             //获取响应实体   
              
            if (null != entity) {  
                responseLength = entity.getContentLength();  
                responseContent = EntityUtils.toString(entity, "UTF-8");  
                EntityUtils.consume(entity); //Consume response content   
            }  
            System.out.println("请求地址: " + httpPost.getURI());  
            System.out.println("响应状态: " + response.getStatusLine());  
            System.out.println("响应长度: " + responseLength);  
            System.out.println("响应内容: " + responseContent);  
        } catch (KeyManagementException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源   
            httpClient = null;
        }  
        return responseContent;  
    }  
}  
