package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class danglianURLtest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		HttpURLConnection hul = null;
		//URL url = new URL("http://suzhou-huixin.vicp.cc:6666/AMP4/service/organ/findAll");//实例URL
		URL url = new URL("http://192.168.11.7:8080/DRMProject/drmServer");//实例URL
		hul = (HttpURLConnection)url.openConnection();//打开连接
		//hul.setConnectTimeout(2000);
		hul.setDoOutput(false);//当上传文件和参数时，将其设置为true
		hul.setUseCaches(false);//无缓存
		hul.setRequestMethod("POST");//设置请求方法
		hul.setDoInput(true);
		hul.setInstanceFollowRedirects(true);//允许页面进行自动跳转 
		hul.setRequestProperty("User-Agent", "Mozilla/5.0");//在连接前加入User-agent，否则连接时可能会引起403错误
		
		
		String respStr ="";
		String line = "";
		
//		byte[] bs = new byte[hul.getInputStream().available()];
//		hul.getInputStream().read(bs);
//		respStr = new String(bs,"UTF-8");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(hul.getInputStream(),"UTF-8"));
		while((line = reader.readLine())!=null){//遍历信息内容
			respStr += line;
		}
		System.out.println(hul.getResponseCode());
		reader.close();		
		hul.disconnect();
		hul = null;
		System.out.println(respStr);

	}

}
