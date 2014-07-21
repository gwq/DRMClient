package httputil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;







//import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.httpclient.URI;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
public class HttpSupportUtils {

	private static Logger logger = Logger.getLogger(HttpSupportUtils.class);

	private final static String DEFAULT_CHARSETNAME = "UTF-8";
	private final static int DEFAULT_TIMEOUT = 5000;

	/**
	 * 根据url获取ResponseBody,method=get
	 * @param url 
	 * @return 以byte[]的方式放回
	 */
	public static String getDataFromUrl(String url) throws Exception {
		return getDataAsStringFromUrl(url, DEFAULT_CHARSETNAME);
	}

	public static String getDataAsStringFromUrl(String url,Map<String, Object> params) throws Exception {
		if(StringUtils.isBlank(url)) {
			logger.error("getDataFromUrl:URL Error");
			return null;
		}
		HttpParams clientparams = new BasicHttpParams();
		clientparams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		clientparams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);
		HttpClient httpClient = new DefaultHttpClient(clientparams);
		HttpGet method = new HttpGet(url);
		URI orginUri = method.getURI();
		for(String key : params.keySet()){
			orginUri =  new URIBuilder(orginUri).addParameter(key,String.valueOf(params.get(key))).build();
		}
	    //URI uri = new URIBuilder(method.getURI()).addParameter("json",JSONUtil.serialize(params)).build();
	    method.setURI(orginUri);
		try {
			// 如果发生错误则连续尝试1次
			//method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
			//method.setURI(new URI(url, false, DEFAULT_CHARSETNAME));
			HttpResponse response = httpClient.execute(method);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				return extractContent(response,DEFAULT_CHARSETNAME);
			}
			logger.info(statusCode);
		}catch (HttpException e) {
			logger.error(e.getMessage(), e);
		}catch (IOException e) {
			logger.error(e.getMessage(), e);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			method.releaseConnection();
		}
		return getDataAsStringFromUrl(url, DEFAULT_CHARSETNAME);
	}

	
	/**
	 * 根据url获取ResponseBody,method=get
	 * @param url
	 * @param charsetName
	 * @param timeout
	 * @return 以String的方式返回
	 * @throws UnsupportedEncodingException
	 */
	public static String getDataAsStringFromUrl(String url, String charsetName, int timeout) throws UnsupportedEncodingException {
		if(StringUtils.isBlank(url)) {
			logger.error("getDataFromUrl:URL Error");
			return null;
		}
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
		HttpClient httpClient = new DefaultHttpClient(params);
		// 等待数据返回超时
		//httpClient.getParams().setSoTimeout(timeout);
		HttpGet method = null;
		try {
			URL urlr = new URL(url);
			URI uri = new URI(urlr.getProtocol(),null,urlr.getHost(),urlr.getPort(),urlr.getPath(),urlr.getQuery(),null);
					//new URI(urlr.getProtocol(),urlr.getHost(),urlr.getPath(),urlr.getQuery(),null);
			method = new HttpGet(uri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		try {
			// 如果发生错误则连续尝试1次
			//method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
			//method.setURI(new URI(url, false, DEFAULT_CHARSETNAME));
			HttpResponse response = httpClient.execute(method);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				return extractContent(response,charsetName);
			}
			logger.info(statusCode);
		}catch (HttpException e) {
			logger.error(e.getMessage(), e);
		}catch (IOException e) {
			logger.error(e.getMessage(), e);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			method.releaseConnection();
		}
		return null;
	}

	/**
	 * 根据url获取ResponseBody,method=get
	 * @param url
	 * @param charsetName
	 * @return 以String的方式返回
	 * @throws UnsupportedEncodingException
	 */
	public static String getDataAsStringFromUrl(String url, String charsetName) throws UnsupportedEncodingException {
		return getDataAsStringFromUrl(url, charsetName, DEFAULT_TIMEOUT);
	}

	public static String getDataAsStringFromUrl(String url) throws UnsupportedEncodingException {
		return getDataAsStringFromUrl(url, DEFAULT_CHARSETNAME, DEFAULT_TIMEOUT);
	}


	/**
	 * 根据url获取ResponseBody,method=post
	 * @throws Exception 
	 */
	
	public static String getDataAsStringFromPostUrlWithObjecPara(String url, Map<String, Object> map) throws Exception {
		Map<String, String> strParam = new HashMap<String, String>();
		for (String key : map.keySet()) {
			if (map.get(key)!=null) {
				strParam.put(key, map.get(key).toString());
			}
		}
		return getDataAsStringFromPostUrl(url,strParam, 8000);
	}
	
	public static String getDataAsStringFromPostUrl(String url, Map<String, String> map) throws Exception {
		return getDataAsStringFromPostUrl(url, map, DEFAULT_TIMEOUT);

	}

	public static String getDataAsStringFromPostUrl(String url, Map<String, String> map, int timeout) throws Exception {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
		HttpClient httpClient = new DefaultHttpClient(params);
		HttpPost method = new HttpPost(url);
		List<NameValuePair> parametersBody = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> entry : map.entrySet()) {
			parametersBody.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
	    UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(parametersBody, "UTF-8");
	    method.setEntity(formEntiry);
		//method.setRequestBody(nameValuePairs);
		//method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
		try {
			HttpResponse response = httpClient.execute(method);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				return extractContent(response,DEFAULT_CHARSETNAME);
			}
		}catch (HttpException e) {
			logger.error(e.getMessage(), e);
		}catch (IOException e) {
			logger.error(e.getMessage(), e);
		}finally {
			method.releaseConnection();
		}
		return null;
	}
	
	public static String extractContent(HttpResponse response, String charSet) throws Exception{
	      HttpEntity entity = response.getEntity();
//	      System.out.println(entity.getContentEncoding().getValue());
	      BufferedReader bfr = new BufferedReader(new InputStreamReader(entity.getContent(),charSet));
	      StringBuilder sb = new StringBuilder();
	      String bufferLine = null;
	      while((bufferLine = bfr.readLine())!=null){
	         sb.append(bufferLine).append("\r\n");
	      }
	      if(bfr!=null){
	         bfr.close();
	      }
	      return sb.toString();
	   }
	
	  public static String httpPutRequest(String url,String headers) {
	      HttpClient httpclient = new DefaultHttpClient();
	      HttpPut httpPut = new HttpPut(url);
	      httpPut.addHeader("Content-Type","application/json");
	      httpPut.addHeader("Authorization","Token token=cdHoh9sSPmuzHsmIrChNUkbWyyVYnNApKAiCOutWoMU");
	      String html = null;
	      try {
	         HttpEntity entity = new InputStreamEntity(new ByteArrayInputStream(headers.getBytes()), headers.getBytes().length);
	         httpPut.setEntity(entity);
	         HttpResponse response = httpclient.execute(httpPut);
	         html = HttpClientUtils.extractContent(response, "utf8");
	      }catch(Exception e) {
	         throw new RuntimeException(e.getMessage(),e);
	      } finally {
	         httpPut.releaseConnection();
	      }
	      return html;
	   }
}
