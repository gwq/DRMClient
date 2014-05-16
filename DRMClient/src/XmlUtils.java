
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * <p>Title: XML工具类</p>
 * <p>Description: </p>
 * <p>2008 All Rights Reserved.</p>
 * <p>Company: smartsecuri</p>
 * @author 
 * @version 1.0
 * @Date
 */
public class XmlUtils {
	
	private String rootStr = "DRMClientREQ";//根元素
	
	private Document document;
	
	private Element rootElement;
	
	private String encoding = "UTF-8";//字符编码
	
	public void setRootStr(String rootStr) {
		this.rootStr = rootStr;
	}
	
	public String getRootStr() {
		return rootStr;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @ 说明:初始化
	 * @ 时间:
	 */
	public void init(){
		document = DocumentHelper.createDocument();
		rootElement = document.addElement(rootStr);
	}
	
	/**
	 * @ 说明:增加节点
	 * @ 时间:
	 * @param element
	 * @param value
	 */
	public void addElement(String element,String value,Map<String, String> attrMap){
		Element temp=rootElement.addElement(element);
		if(value!=null)
			temp.setText(value);	
		if(attrMap != null){
			Iterator<String> iter=attrMap.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				String elementValue= attrMap.get(key);
				temp.addAttribute(key, elementValue);
			}
		}
	}
	
	/**
	 * @ 说明:增加节点的子节点
	 * @ 时间:
	 * @param element
	 * @param value
	 */
	public void addChildElement(String element,String childelement,List<Map<String, String>> list,Map<String, String> attrMap){
		Element temp=rootElement.addElement(element);
		if(list!=null){
			for(Map<String, String> map:list){
			Iterator<String> iter=map.keySet().iterator();
			Element childtemp =temp.addElement(childelement);
			while(iter.hasNext()){
				String key = iter.next();
				String elementValue= map.get(key);
				childtemp.addAttribute(key, elementValue);
			}
			}
		}
		if(attrMap != null){
			Iterator<String> iter=attrMap.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				String elementValue= attrMap.get(key);
				temp.addAttribute(key, elementValue);
			}
		}
	}
	/**
	 * @ 说明:增加子节点属性列表
	 * @ 时间:
	 * @param list
	 * @param subNode
	 */
	public void addElementAttributeList(List<Map<String, String>> list,String subNode){		
		for(Map<String, String> map:list){
			Element subElement = rootElement.addElement(subNode);
			Iterator<String> iter=map.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				String elementValue= map.get(key);
				subElement.addAttribute(key, elementValue);
			}
		}
	}
	/**
	 * @ 说明:产生xml
	 * @ 时间:
	 * @return
	 */
	public String generateXml(){
		try{
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			OutputFormat format = OutputFormat.createCompactFormat();
			format.setEncoding(encoding);
			XMLWriter writer=new XMLWriter(bos,format);
			writer.write(document);
			byte[] xmlByte = bos.toByteArray();
			bos.close();
			return new String(xmlByte, "UTF-8");
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	  * <p>Description:解析xml字符串</p>
	  * @param xmlStr xml字符串
	  * @author 
	  * @date 
	 */
	public void setParseString(String xmlStr){
		try {
			document=DocumentHelper.parseText(xmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @ 说明:获取节点原始
	 * @ 时间:
	 * @param nodeName 节点名称
	 * @return
	 */
	public String getElement(String nodeName){
		Node node = document.selectSingleNode("/"+nodeName);
		if(node != null)
			return node.getText();
		else
			return null;
	}
	
	/**
	  * <p>Description:获取节点元素属性</p>
	  * @param nodeName 节点名称
	  * @param attrName 属性名称
	  * @return
	  * @author 
	  * @date 
	 */
	@SuppressWarnings("unchecked")
	public String getElementAttribute(String nodeName,String attrName){
		List<Iterator> list = document.selectNodes("/"+nodeName+"/@"+attrName);
		if(list.size() > 0){
			String attValue = "";
			Iterator iter=list.iterator();
			while(iter.hasNext()){
	            Attribute attribute=(Attribute)iter.next();
	            attValue = attribute.getValue();
	       }
			return attValue;
		}
		else
			return null;
	}
	
	/**
	 * @ 说明:获取元素列表
	 * @ 时间:
	 * @param nodeName 节点名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getElementAttributeList(String nodeName){
		List<Map<String, String>> content=new ArrayList<Map<String, String>>();
		List list=document.selectNodes("/"+nodeName);
		Map<String, String> map = null;
		for(Iterator iter=list.iterator();iter.hasNext();){
			Element ele=(Element)iter.next();//取得application元素
			List attrList = ele.attributes();
			map = new HashMap<String, String>();
			for(int i=0;i<attrList.size();i++){
				Attribute attriObj = (Attribute)attrList.get(i);
				map.put(attriObj.getName(),attriObj.getValue());
			}
			content.add(map);
		}
		return content;	
	}
    
	/**
	 * 遍历根节点下的子节点
	 */
	public void getRootChildNodes(){
		Element rootElement = document.getRootElement();
		List<Element> list = rootElement.elements();
		for(Element e : list){
			System.out.println(e.getName());
		}
	}
	
	
	public static void main(String[] args) {
 
		XmlUtils drmXmls = new XmlUtils();
		drmXmls.init();
        drmXmls.addElement("GESSPOLICY", "1234",null);
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map=new HashMap<String, String>();
		map.put("aaa", "测试");
		map.put("bbb", "dddd");
		drmXmls.addElement("GESSPOLICY", null,map);
		list.add(map);
		map=new HashMap<String, String>();
		map.put("aaa", "3111");
		map.put("bbb", "222");
		list.add(map);
		drmXmls.addElementAttributeList(list, "ff");
		drmXmls.addElementAttributeList(list, "ff1");
		String xmlStr = drmXmls.generateXml();
		System.out.println(xmlStr);
		
		//******************************************************
		XmlUtils drmXmls2 = new XmlUtils();		
		drmXmls2.setParseString(xmlStr);
		String nodeStr = drmXmls2.getRootStr()+"/GESSPOLICY";
		System.out.println("arr="+drmXmls2.getElementAttribute(nodeStr, "aaa"));
		System.out.println(drmXmls2.getElement(nodeStr));
		
		drmXmls2.getRootChildNodes();
		
		nodeStr = drmXmls2.getRootStr()+"/ff";
		List<Map<String, String>> resultList = drmXmls2.getElementAttributeList(nodeStr);
		System.out.println(resultList.size());
		for(Map<String, String> mapObj:resultList){
			System.out.println("*******************************");
			Iterator<String> iter=mapObj.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				String elementValue= mapObj.get(key);
				System.out.println(key+"   "+elementValue);
			}
		}
		System.out.println("*****************************************************");
		//**********************************************************************************
		String[] filelevels = {"0","1","2","3","4"};
		XmlUtils drmtest = new XmlUtils();
		drmtest.init();
		
		System.out.println((char)('a'+25));
		
		for(String fl : filelevels){
			List<Map<String, String>> testlist = new ArrayList<Map<String,String>>();
			for(int i = 0;i<10;i++){
			Map<String, String> testmap=new HashMap<String, String>();
			testmap.put("v",String.valueOf((char)('a'+i)));
			testmap.put("a","127");
			testlist.add(testmap);
			}
			Map<String, String> tm=new HashMap<String, String>();
			tm.put("v", fl);
			drmtest.addChildElement("FL","UL", testlist, tm);
		}
		
		String testXmlStr = drmtest.generateXml();
		System.out.println(testXmlStr.getBytes().length);
		System.out.println(testXmlStr);
		System.out.println("*****************************************************");
		//******************************************************************************************
		String[] userlevels2 = {"1","2","3","4","5","6","7","8","9","10"};
		String[] filelevels2 = {"1","2","3","4","5","6","7","8"};
		XmlUtils drmtest2 = new XmlUtils();
		drmtest2.init();
		drmtest2.addElement("TYPE", "GEFMS_CS_GETGEFAUTHORITYXML",null);
		List<Map<String, String>> drmtest2list = new ArrayList<Map<String,String>>();
		Map<String, String> drmtest2map = null;
		for(String s : userlevels2){
			drmtest2map = new HashMap<String, String>();
			drmtest2map.put("ul", s);
			drmtest2map.put("name", "用户级别");
			drmtest2list.add(drmtest2map);
		}
		drmtest2.addElementAttributeList(drmtest2list, "USERNAME");
		drmtest2list = new ArrayList<Map<String,String>>();
		for(String s : filelevels2){
			drmtest2map = new HashMap<String, String>();
			drmtest2map.put("ul", s);
			drmtest2map.put("name", "文件密级");
			drmtest2list.add(drmtest2map);
		}
		drmtest2.addElementAttributeList(drmtest2list, "FILENAME");
		for(String s1 : filelevels2){
			for(String s2 : userlevels2){
				drmtest2map = new HashMap<String, String>();
				drmtest2map.put("fl", s1);
				drmtest2map.put("ul", s2);
				drmtest2map.put("a", "127");
				drmtest2list.add(drmtest2map);
			}
		}
		drmtest2.addElementAttributeList(drmtest2list, "GEF");
		String drmtest2Str = drmtest2.generateXml();
		System.out.println(drmtest2Str);
		
		
		/*String reqInfo = "<DRMClientREQ><TYPE>GEFMS_CS_SetFilePermission</TYPE>" +
		"<TICKET>364e133ab050baa</TICKET><FILEID>40</FILEID><ABSTRACT>2121</ABSTRACT>" +
		"<UPLOADDIRID>212</UPLOADDIRID><FILELEVEL>A</FILELEVEL>" +
		"<USERAUTHORITIES ID='admin' SS='5'  ST='2009-12-12 12:12:12' ET='2009-12-12 12:12:12' RTS='123'/>" +
		"<ORGAUTHORITIES ID='project' FA='1011001' ST='2009-12-12 12:12:12' ET='2009-12-12 12:12:12' RTS='123'/>" +
		"</DRMClientREQ>";
		
		XmlUtils drmXmlsIn = new XmlUtils();
		drmXmlsIn.setParseString(reqInfo);
		
		List<Map<String,String>> userList = drmXmlsIn.getElementAttributeList("DRMClientREQ/USERAUTHORITIES");
		*/
		/*for(Map<String,String> mapObj:userList){
			String userName = mapObj.get("ID");//用户登录名
			String policeId = mapObj.get("SS");//文件策略Id
			String userfilePermission = mapObj.get("FA");//用户文件权限
			String userfileBeginTime = mapObj.get("ST");//用户文件起始时间
			String userfileEndTime = mapObj.get("ET");//用户文件终止时间
			String readCount = mapObj.get("RTS");//用户文件阅读次数
			System.out.println(userName);
			System.out.println(policeId);
			System.out.println(userfilePermission);
			System.out.println(userfileBeginTime);
			System.out.println(userfileEndTime);
			System.out.println(readCount);
			
		}*/
		/*for(Map<String, String> mapObj:userList){
			System.out.println("*******************************");
			Iterator<String> iter=mapObj.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				String elementValue= mapObj.get(key);
				System.out.println(key+"   "+elementValue);
			}
		}*/
		
	}

}
