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
		//URL url = new URL("http://suzhou-huixin.vicp.cc:6666/AMP4/service/organ/findAll");//ʵ��URL
		URL url = new URL("http://192.168.11.7:8080/DRMProject/drmServer");//ʵ��URL
		hul = (HttpURLConnection)url.openConnection();//������
		//hul.setConnectTimeout(2000);
		hul.setDoOutput(false);//���ϴ��ļ��Ͳ���ʱ����������Ϊtrue
		hul.setUseCaches(false);//�޻���
		hul.setRequestMethod("POST");//�������󷽷�
		hul.setDoInput(true);
		hul.setInstanceFollowRedirects(true);//����ҳ������Զ���ת 
		hul.setRequestProperty("User-Agent", "Mozilla/5.0");//������ǰ����User-agent����������ʱ���ܻ�����403����
		
		
		String respStr ="";
		String line = "";
		
//		byte[] bs = new byte[hul.getInputStream().available()];
//		hul.getInputStream().read(bs);
//		respStr = new String(bs,"UTF-8");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(hul.getInputStream(),"UTF-8"));
		while((line = reader.readLine())!=null){//������Ϣ����
			respStr += line;
		}
		System.out.println(hul.getResponseCode());
		reader.close();		
		hul.disconnect();
		hul = null;
		System.out.println(respStr);

	}

}
