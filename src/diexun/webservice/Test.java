package diexun.webservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author xiaolong
 */
public class Test {

	public static void main(String[] args) {
		JSONObject json = new JSONObject();
    	json.put("lang", 2);
    	json.put("app_id", 5);
    	json.put("title", "英文标题");
    	json.put("title_picture", "jjj");
    	json.put("picture_count", 20);
    	json.put("att_ids", "eee");
    	json.put("description", "ddd");
    	json.put("description_en", "ddd");
    	json.put("source_app_id", 5);
    	json.put("sys_user_id", 100);
    	
    	String ret = WebServiceHander.insert().addSubject(json.toJSONString()); 
	    com.alibaba.fastjson.JSONObject parseObject = JSON.parseObject(ret);
	    Integer subject_id = (Integer) parseObject.get("subject_id");
		System.out.println("英文subject_id:" + subject_id);
		
		json.put("lang", 1); 
		json.put("rel_id", subject_id);
		ret = WebServiceHander.insert().addSubject(json.toJSONString()); 
		subject_id = (Integer) parseObject.get("subject_id");
		System.out.println("中文subject_id:" + subject_id);
	}

}
