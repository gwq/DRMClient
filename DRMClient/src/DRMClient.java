import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;



public class DRMClient {
	 /** 
     * ��HTTPS��ַ����POST���� 
     * @see �÷������Զ��ر�����,�ͷ���Դ 
     * @param reqURL �����ַ 
     * @param params ������� 
     * @return ��Ӧ���� 
	 * @throws KeyStoreException 
	 * @throws CertificateException 
	 * @throws UnrecoverableKeyException 
     */  
    @SuppressWarnings("deprecation")
	public static String sendSSLPostRequest(String reqURL, String xmlStr, Map<String, String> params,Map<String, String> headparams,Integer download) throws KeyStoreException, CertificateException, UnrecoverableKeyException{  
        long responseLength = 0;                         //��Ӧ����   
        String responseContent = "";                     //��Ӧ����   
        HttpClient httpClient = new DefaultHttpClient(); //����Ĭ�ϵ�httpClientʵ��   
        //httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 1000);
        //httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 6000);
        try {  
/************************************�������е�֤��********************************************************/   
        	 X509TrustManager xtm = new X509TrustManager(){   //����TrustManager   
                 public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}  
                 public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}  
                 public X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }  
             }; 
            //TLS1.0��SSL3.0������û��̫��Ĳ�𣬿ɴ������ΪTLS��SSL�ļ̳��ߣ�������ʹ�õ�����ͬ��SSLContext   
            SSLContext ctx = SSLContext.getInstance("TLS");  
              
            //ʹ��TrustManager����ʼ���������ģ�TrustManagerֻ�Ǳ�SSL��Socket��ʹ��   
            ctx.init(null, new TrustManager[]{xtm}, null);  
              
            //����SSLSocketFactory �� ��У������ 
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
            //*********��У������(�÷����ѹ��ڣ�����ֱ����SSLSocketFactory���캯�������ò����������д�����ʾ)*********
            //socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
              
            //ͨ��SchemeRegistry��SSLSocketFactoryע�ᵽ���ǵ�HttpClient��   
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));  
/**********************************************************************************************************************/   
            
 /**************************�������ε�֤��*****************************************************         
            
          //����ܳ׿�
            KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream instream = new FileInputStream(new File("D://keystore.jks"));
            //�ܳ׿������
            trustStore.load(instream, "111111".toCharArray());
            //ע���ܳ׿�
            SSLSocketFactory socketFactory2 = new SSLSocketFactory(trustStore);
            //��У������
            socketFactory2.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", 800, socketFactory2);

            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
/*******************************************************************************/            
            
            HttpPost httpPost = new HttpPost(reqURL);                        //����HttpPost
            
            if(params != null){
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); //����POST����ı�����   
            for(Map.Entry<String,String> entry : params.entrySet()){  
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
            }  
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));  
            }
            if(xmlStr != null){
            	ByteArrayEntity bae = new ByteArrayEntity(xmlStr.getBytes("UTF-8"));
            	httpPost.setEntity(bae);
            }
            
            if(headparams != null){                               //ipͷ��������
            	for(Map.Entry<String,String> entry : headparams.entrySet()){  
            		//httpPost.addHeader(entry.getKey(), entry.getValue());
                  httpPost.addHeader(entry.getKey(),new String(entry.getValue().getBytes("utf-8"),"ISO-8859-1"));
            	}
            	File f = new File("D://����tomcat���������ܵ���������[1].doc.gef");
            	InputStreamEntity isre = new InputStreamEntity(new FileInputStream(f), f.length());//����File
            	httpPost.setEntity(isre);
            }
            HttpResponse response = httpClient.execute(httpPost); //ִ��POST����   
            HttpEntity entity = response.getEntity();             //��ȡ��Ӧʵ��
            
              
            if (null != entity && download.equals(0)) {  
                responseLength = entity.getContentLength();  
                responseContent = EntityUtils.toString(entity, "UTF-8");  
                EntityUtils.consume(entity); //Consume response content  
                System.out.println("�����ַ: " + httpPost.getURI());  
                System.out.println("��Ӧ״̬: " + response.getStatusLine());  
                System.out.println("��Ӧ����: " + responseLength);  
                System.out.println("��Ӧ����: " + responseContent);  
                Header[] hs = response.getAllHeaders();
                for(Header h : hs)
                  System.out.println(h.getName()+":"+h.getValue());
            } 
            
            if (null != entity && download.equals(1)) {  
                responseLength = entity.getContentLength();  
                Header[] hds = response.getAllHeaders();
                for(Header h : hds){
                	System.out.println(h.getName()+":"+h.getValue());
                }
                String filename = new String(response.getHeaders("GEFFileName")[0].getValue().getBytes("iso8859-1"),"UTF-8"); 
                System.out.println(filename);
                File saveFile = new File("e://"+filename);
                InputStream is = entity.getContent();
                OutputStream fos = new FileOutputStream(saveFile);
                byte[] bs = new byte[1024];
                int len = 0;
                while((len = is.read(bs)) != -1){
                	fos.write(bs, 0, len);
                }
                is.close();
                fos.close();
                
                
                EntityUtils.consume(entity); //Consume response content   
                System.out.println("SAVE OK!!!!!!!");
            }  
            httpPost.releaseConnection();
           
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
        
            httpClient.getConnectionManager().shutdown(); //�ر�����,�ͷ���Դ   
            httpClient = null;
        }  
        return responseContent;  
    }  
    
    public static void main(String args[]) throws UnrecoverableKeyException, KeyStoreException, CertificateException, UnsupportedEncodingException{
    	XmlUtils drmtest2 = new XmlUtils();
		drmtest2.init();
//		drmtest2.addElement("TYPE", "GEFMS_CS_GEFAPPLYDECODE",null);
//		drmtest2.addElement("TICKET", "7214d1dd80f077e1",null);
//		drmtest2.addElement("GEFNAME", "�����ĵ�.doc",null);
//		drmtest2.addElement("FILELEVEL", "1",null);
//		String xmlStr = drmtest2.generateXml();
//		System.out.println(xmlStr);
//		
//		byte[] bs = xmlStr.getBytes("UnicodeLittleUnmarked");
//		System.out.println(bs.length);
//		System.out.println(new String(bs));
//		System.out.println(new String(bs,"UnicodeLittleUnmarked"));
//		
//		//��������
//		long l = System.currentTimeMillis();
//		DRMClient.sendSSLPostRequest("https://192.168.1.31:8443/DRMProject/drmServer", xmlStr, null, null, 0);
//		long e = System.currentTimeMillis();
//		System.out.println((e-l));
		
		
		//�ϴ������ܵ�GEF�ļ�
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("ClientREQ", "test");
//		map.put("Ticket", "1773a9ad641ceda0");
//		map.put("ApplyID", "dec16d8f7e6f4aa991b21c03dbaab9ac");
////		map.put("name", "��������ǿ");
//		map.put("APID", "1");
//		DRMClient.sendSSLPostRequest("https://127.0.0.1:8443/DRMProject/drmServer", null, null, map, 0);
		
		//���ؽ��ܺ��GEF�ļ�
//    	XmlUtils drmtest2 = new XmlUtils();
//		drmtest2.init();
//		drmtest2.addElement("TYPE", "GEFMS_CS_DOWNLOADDECODEGEF",null);
//		drmtest2.addElement("TICKET", "1773a9ad641ceda0",null);
//		drmtest2.addElement("APPID", "dec16d8f7e6f4aa991b21c03dbaab9ac",null);
//		String xmlStr = drmtest2.generateXml();
//		System.out.println(xmlStr);
//		DRMClient.sendSSLPostRequest("https://192.168.1.31:8443/DRMProject/drmServer", xmlStr, null, null, 1);
		
		
		drmtest2.addElement("TYPE", "GEFMS_CS_GETGEFAUTHORITYXML",null);
		String xmlStr = drmtest2.generateXml();
		System.out.println("�������ݣ�"+xmlStr);
		DRMClient.sendSSLPostRequest("http://127.0.0.1:8080/DRMProject/drmServer", xmlStr, null, null, 0);

		
//		drmtest2.addElement("TYPE", "GEFMS_CS_TERMINAL_POLICY",null);
//		drmtest2.addElement("ID", "4d7ef85e-06ed-11e2-b68a-000b2f6176dc",null);
//		//drmtest2.addElement("ID", "fda80468-870f-11d7-8637-00112ff14107",null);
//		String xmlStr = drmtest2.generateXml();
//		DRMClient.sendSSLPostRequest("https://v.vegacnc.cn:8443/DRMProject/drmServer", xmlStr, null, null, 0);
		
//		drmtest2.addElement("TYPE", "GEFMS_CS_TERMINAL_POLICY",null);
//		drmtest2.addElement("ID", "ebf57af8-32ee-11e2-a14c-00e04c390e63",null);
//		//drmtest2.addElement("ID", "fda80468-870f-11d7-8637-00112ff14107",null);
//		String xmlStr = drmtest2.generateXml();
//		DRMClient.sendSSLPostRequest("https://127.0.0.1:8443/VEGADRMProject/drmServer", xmlStr, null, null, 0);
    }
}
