package test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class dangliantest {

	public static void main(String[] args)throws Exception{ 
       String str = sendSSLPostRequest("http://suzhou-huixin.vicp.cc:6666/AMP4/service/organ/findAll");
		//String str = sendSSLPostRequest("http://www.baidu.com");
    }  
      
    /** 
     * ��HTTPS��ַ����POST���� 
     * @param reqURL �����ַ 
     * @param params ������� 
     * @return ��Ӧ���� 
     */  
    public static String sendSSLPostRequest(String reqURL){  
        long responseLength = 0;                         //��Ӧ����   
        String responseContent = "";                     //��Ӧ����   
        HttpClient httpClient = new DefaultHttpClient(); //����Ĭ�ϵ�httpClientʵ��   
        httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 1000);
        httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 6000);
       
        try {  
              
            HttpPost httpPost = new HttpPost(reqURL);                        //����HttpPost   
            //HttpGet httpGet = new HttpGet(reqURL);
            HttpResponse response = httpClient.execute(httpPost); //ִ��POST����   
            HttpEntity entity = response.getEntity();             //��ȡ��Ӧʵ��   
              
            if (null != entity) {  
                responseLength = entity.getContentLength();  
                responseContent = EntityUtils.toString(entity, "UTF-8");  
                EntityUtils.consume(entity); //Consume response content   
            }  
            System.out.println("�����ַ: " + httpPost.getURI());  
            System.out.println("��Ӧ״̬: " + response.getStatusLine());  
            System.out.println("��Ӧ����: " + responseLength);  
            System.out.println("��Ӧ����: " + responseContent);  
            
        }catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            httpClient.getConnectionManager().shutdown(); //�ر�����,�ͷ���Դ   
            httpClient = null;
        }  
        return responseContent;  
    }  

}
