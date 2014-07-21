package httputil;

import java.util.HashMap;
import java.util.Map;

public class SRMPtest {
	
	public void srmpSysInstallAPI(){
		String url = "http://10.125.6.96/config/createreinstall";
		Map<String,String> map = new HashMap<String, String>();
		
		try {
			HttpSupportUtils.getDataAsStringFromPostUrl(url, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
