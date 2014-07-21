package httputil;

import java.util.HashMap;
import java.util.Map;

public class SRMPtest {
	
	 public static String getMD5(byte[] bytes) {
	        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	        char str[] = new char[16 * 2];
	        try {
	            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
	            md.update(bytes);
	            byte tmp[] = md.digest();
	            int k = 0;
	            for (int i = 0; i < 16; i++) {
	                byte byte0 = tmp[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }
	        return new String(str);
	    }
	    
	public static String getMD5(String value) {
	        String result = "";
	        result = getMD5(value.getBytes());
	        return result;
	 }
	
	
	public void srmpSysInstallAPI(){
		String url = "http://10.125.6.96/config/createreinstall";
		Map<String,String> map = new HashMap<String, String>();
		map.put("from", "idcfree");
		map.put("key", getMD5("srmp_idcfree20140721nnLL+q6+/5zgyyfaVMEEtIQ==3"));
		
		try {
			HttpSupportUtils.getDataAsStringFromPostUrl(url, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
