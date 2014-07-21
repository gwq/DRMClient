package httputil;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

/**
 * @author Administrator Jan 22, 2013
 */

public class HttpClientUtils {

	private final static Logger    logger           = Logger.getLogger(HttpClientUtils.class);

	public static String getDataAsStringFromPostUrl(String url,
			Map<String, String> map) throws Exception {
		return getDataAsStringFromPostUrl(url, map, 18000);
	}

	public static String getDataAsStringFromPostUrl(String url,
			Map<String, String> map, int timeout) throws Exception {

		Form form = Form.form();
		for (String key : map.keySet()) {
			form.add(key, map.get(key));
		}
		
		Long start = System.currentTimeMillis();
		HttpResponse response = Request.Post(url).connectTimeout(timeout)
				.socketTimeout(timeout).bodyForm(form.build()).execute().returnResponse();
		
		logger.error(url + "responseTime:"+(System.currentTimeMillis()-start));
		return extractContent(response, "UTF-8");
	}

	public static SSLContext initSSLContextWithCert(String certPath,
			String keyPass) {

		SSLContext sslContext = null;

		try {
			KeyStore ks = KeyStore.getInstance("pkcs12");
			// 加载pfx文件

			ks.load(HttpClientUtils.class.getClassLoader().getResourceAsStream(
					certPath), keyPass.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
			kmf.init(ks, keyPass.toCharArray());
			sslContext = SSLContext.getInstance("SSL");
			// 初始化
			sslContext.init(kmf.getKeyManagers(),
					new TrustManager[] { new X509TrustManager() {
						public void checkClientTrusted(X509Certificate[] xcs,
								String string) throws CertificateException {
						}

						public void checkServerTrusted(X509Certificate[] xcs,
								String string) throws CertificateException {
						}

						public X509Certificate[] getAcceptedIssuers() {
							return new X509Certificate[] {};
						}
					} }, new java.security.SecureRandom());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return sslContext;
	}

	public static HttpClient registerClientWithSSL(SSLContext sslContext) {
		HttpClient httpclient = new DefaultHttpClient();
		SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext);
		Scheme sch = new Scheme("https", 443, socketFactory);
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		return httpclient;
	}

	public static String extractContent(HttpResponse response, String charSet)
			throws Exception {
		HttpEntity entity = response.getEntity();
		// System.out.println(entity.getContentEncoding().getValue());
		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				entity.getContent(), charSet));
		StringBuilder sb = new StringBuilder();
		String bufferLine = null;
		while ((bufferLine = bfr.readLine()) != null) {
			sb.append(bufferLine).append("\r\n");
		}
		if (bfr != null) {
			bfr.close();
		}
		return sb.toString();
	}

}
