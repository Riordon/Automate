package diexun.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.alibaba.fastjson.JSON;

import diexun.bean.CommonBean;
import diexun.ftp.FTPBean;
import diexun.ftp.FtpClientC;
import diexun.main.Scanner;
import diexun.util.INameMD5;
import diexun.webservice.WebServiceHander;

/**
 * @author xiaolong
 */
public class CallableImpl implements Runnable{
	private CommonBean bean;
	private File fFile;

	protected FtpClientC ftp;
	
	private static FTPBean ftpBean;
	static {
		ftpBean = new FTPBean(Scanner.EPD_SHOEBAG_FTPBEAN);
	}
	
	public CallableImpl(CommonBean bean, File fFile) {
		this.bean = bean;
		this.fFile = fFile;
		initMapPicture(bean, fFile);
	}
	
	private void initMapPicture(CommonBean bean, File fFile) {
		Map<String, ArrayList<String>> mapPicture = new HashMap<String, ArrayList<String>>();
		
		File[] listFiles = fFile.listFiles();
		for (File file : listFiles) {
			String pic = file.getName().replaceAll(" ", "").replaceAll("（", "(").replaceAll("）", ")");
			if (pic.equals("t.jpg")) continue;
			if (pic.contains("(")) {
				String prefix = pic.substring(0, pic.indexOf("("));
				if (mapPicture.get(prefix) == null) {
					ArrayList<String> attachs = new ArrayList<String>();
					attachs.clear();
					mapPicture.put(prefix, attachs);
					ArrayList<String> att = mapPicture.get(prefix);
					att.add(pic);
				} else {
					ArrayList<String> att = mapPicture.get(prefix);
					att.add(pic);
				}
			}
		}
		
		bean.setMapPicture(mapPicture);
		System.out.println(bean.getMapPicture().toString());
	}

	@Override
	public void run() {
		System.out.println(bean.getTitle() + "----处理开始----");
		executeTheme(fFile);
		System.out.println(bean.getTitle() + "----处理完成----");
	}
	
	private boolean executeTheme(File fFile) {
		if (!initFTP()) {
			return false;
		}
		if (!ftpUploadImages(ftp, bean, fFile)) {
			return false;
		}
		if (!addSubject(fFile)) {
			return false;
		}
		if (!addPicture(bean)) {
			return false;
		}
		
		return true;
	}
	
	private boolean initFTP() {
		ftp = new FtpClientC(ftpBean.getFtp_ip(), ftpBean.getFtp_username(), ftpBean.getFtp_password());
	    if (!ftp.createFtpConnction()) {
	        // 尝试 连接 3 次
	    	int i = 0;
	    	while (i < 3 && !ftp.createFtpConnction()) {
	    		i++;
	    	}
	    }
	    if (ftp.isConnected()) {
	    	ftp.shoeKeepLive();
	    }
	    return ftp != null && ftp.isConnected();
	}
	
	private boolean ftpUploadImages(FtpClientC ftpClient, CommonBean bean, File fFile) {
		String path = fFile.getAbsolutePath();
		String ftpDirectory  = WebServiceHander.insert().createFolder("");
		
	    com.alibaba.fastjson.JSONObject parseObject = JSON.parseObject(ftpDirectory);
        if (parseObject != null && parseObject.containsKey("path")) {
        	ftpDirectory = (String) parseObject.get("path");
        }
		
        bean.setFtpDirectory(ftpDirectory);
		System.out.println(ftpDirectory);
		
		String filePath = null;
		String writerName = null;
		Map<String, ArrayList<String>> mapPicture = bean.getMapPicture();
		
		//上传标题图
		filePath = path + File.separator + "t.jpg";
		writerName = INameMD5.GetImageMD5Code();
		if (!ftpClient.writeFile(writerName, filePath, ftpDirectory)) {
			System.err.println("上传标题图失败");
		}
		bean.setSrcTitle(ftpDirectory + writerName);
		
		for (String key : mapPicture.keySet()) {
			//上传主图
			filePath = path + File.separator + key + ".jpg";
			writerName = INameMD5.GetImageMD5Code();
			ftpClient.writeFile(writerName, filePath, ftpDirectory);
			bean.getMapLocalToFTP().put(key, writerName);
			//上传细节图
			ArrayList<String> picture3D = mapPicture.get(key);
			for (String item : picture3D) {
				filePath = path + File.separator + item;
				writerName = INameMD5.GetImageMD5Code();
				ftpClient.writeFile(writerName, filePath, ftpDirectory);
				bean.getMapLocalToFTP().put(item, writerName);
			}
		}
		
		return true;
	}
	
	private boolean addSubject(File fFile) {
		System.out.println("addSubject begin...");
		String description = "";
		File[] files = fFile.listFiles();
		int picture_count = files.length;
		for (File file : files) {
			if (file.getName().contains(".txt")) {
				description = handleThemeTxt(file);
				picture_count--;
				break;
			}
		}
		bean.setDescription(description);
		
		String name = fFile.getName();
		name = format(name);
		addAttribute(bean, name);
		String att_ids = "";
		if (!attrsToIds(bean)) return false;
		
		JSONObject json = new JSONObject();
    	json.put("lang", 2); //english
    	json.put("app_id", 5);
    	json.put("title", bean.getTitle());
    	json.put("title_picture", bean.getSrcTitle());
    	json.put("picture_count", picture_count);
    	json.put("att_ids", bean.getAtt_ids());
    	json.put("description", description);
    	json.put("description_en", description);
    	json.put("source_app_id", 5);
    	json.put("sys_user_id", 100);
		String ret = WebServiceHander.insert().addSubject(json.toJSONString()); //insert english subject
	    com.alibaba.fastjson.JSONObject parseObject = JSON.parseObject(ret);
	    if (!checkStatus(parseObject)) return false;
	    Integer subject_id = (Integer) parseObject.get("subject_id");
		bean.setEn_subject_id(subject_id);
	    
		System.out.println("英文subject_id:" + subject_id);
		
		json.put("lang", 1); //chinese
		json.put("rel_id", subject_id);
		ret = WebServiceHander.insert().addSubject(json.toJSONString()); //insert chinese subject
		parseObject = JSON.parseObject(ret);
		if (!checkStatus(parseObject)) return false;
		subject_id = (Integer) parseObject.get("subject_id");
		bean.setEs_subject_id(subject_id);
		
		System.out.println("中文subject_id:" + subject_id);
		System.out.println("addSubject end...");
		return true;
	}
	
	private boolean checkStatus(com.alibaba.fastjson.JSONObject parseObject) {
		Integer ret = (Integer) parseObject.get("status");
		if (ret == 0) return false;
		return true;
	}

	private boolean attrsToIds(CommonBean bean) {
		String att_ids = "";
		JSONObject json = new JSONObject();
		json.put("app_id", 5);
		String attributes = bean.getAttributes();
		System.out.println(attributes);
		String temp = null;
		String[] split = attributes.split(",");
		for (String attr : split) {
			json.put("name", attr);
			temp = WebServiceHander.query().getAttrInfoByName(json.toJSONString());
			
			com.alibaba.fastjson.JSONArray array = JSON.parseArray(temp);
			com.alibaba.fastjson.JSONObject object = array.getJSONObject(0);
		    
		    temp = (String) object.get("att_id");
			att_ids += temp + ",";
		}
		bean.setAtt_ids(att_ids.substring(0, att_ids.length()-1));
		return true;
	}

	private void addAttribute(CommonBean bean, String name) {
		int begin = 0;
		int end = 0;
		if (name.contains("[")) {
			begin = name.indexOf("[");
			end = name.indexOf("]");
			bean.addAttribute(name.substring(begin+1, end));
		}
		begin = name.indexOf("(");
		end = name.indexOf(")");
		String attrs = name.substring(begin+1, end);
		String[] split = attrs.split(",");
		for (String attr :split) {
			bean.addAttribute(attr);
		}
		System.out.println(bean.getAttributes());
	}

	private static String format(String name) {
		String temp = name.replaceAll("（", "(").replaceAll("）", ")")
						  .replaceAll("【", "[").replaceAll("】", "]")
						  .replaceAll("，", ",").replaceAll(" ", "");
		return temp;
	}

	private boolean addPicture(CommonBean bean) {
		System.out.println("addPicture begin...");
		Map<String, ArrayList<String>> mapPicture = bean.getMapPicture();
		
		String picture_src = "";
		String ftpDirectory = bean.getFtpDirectory();
		for (String key : mapPicture.keySet()) {
			picture_src = ftpDirectory + bean.getMapLocalToFTP().get(key);
			JSONObject json = new JSONObject();
			json.clear();
	    	json.put("lang", 2);
	    	json.put("subject_id", bean.getEn_subject_id());
	    	json.put("picture_random", ToPicture_random(picture_src));
	    	json.put("type", 1);
	    	json.put("picture_src", ToPicture_src(picture_src));
	    	json.put("order_no", 5);
	    	json.put("add_user_id", 100);
	    	json.put("att_ids", bean.getAtt_ids());
	    	json.put("source_app_id", 5);
	    	json.put("sys_user_id", 100);
	    	json.put("app_id", 5);
	    	json.put("description", bean.getDescription());
	    	
	    	String ret = WebServiceHander.insert().addPicture(json.toJSONString());
	    	com.alibaba.fastjson.JSONObject parseObject = JSON.parseObject(ret);
	    	if (!checkStatus(parseObject)) return false;
	    	Integer picture_en_id = (Integer) parseObject.get("picture_id");
	    	System.out.println("英文picture_id:" + picture_en_id);
	    	json.put("lang", 1); 
	    	json.put("subject_id", bean.getEs_subject_id());
	    	json.put("rel_id", picture_en_id);
	    	ret = WebServiceHander.insert().addPicture(json.toJSONString());
	    	parseObject = JSON.parseObject(ret);
	    	if (!checkStatus(parseObject)) return false;
	    	Integer picture_es_id = (Integer) parseObject.get("picture_id");
	    	System.out.println("中文picture_id:" + picture_es_id);
	    	//插入细节图
			ArrayList<String> picture3D = mapPicture.get(key);
			for (String item : picture3D) {
				picture_src = ftpDirectory + bean.getMapLocalToFTP().get(item);
				json.clear();
		    	json.put("lang", 2);
		    	json.put("picture_id", picture_en_id);
		    	json.put("picture_random", ToPicture_random(picture_src));
		    	json.put("picture_src", ToPicture_src(picture_src));
		    	json.put("source_app_id", 5);
		    	json.put("description", bean.getDescription());
		    	json.put("att_ids", bean.getAtt_ids());
		    	json.put("sys_user_id", 100);
		    	json.put("app_id", 5);
		    	ret = WebServiceHander.insert().addSubsidiary(json.toJSONString());
		    	parseObject = JSON.parseObject(ret);
		    	if (!checkStatus(parseObject)) return false;
		    	Integer pic3D_id = (Integer) parseObject.get("subsidiary_id");
		    	System.out.println("英文subsidiary_id:" + pic3D_id);
		    	
		    	json.put("lang", 1); 
		    	json.put("picture_id", picture_es_id);
		    	json.put("rel_id", pic3D_id);
		    	ret = WebServiceHander.insert().addSubsidiary(json.toJSONString());
		      	parseObject = JSON.parseObject(ret);
		    	if (!checkStatus(parseObject)) return false;
		    	pic3D_id = (Integer) parseObject.get("subsidiary_id");
		    	System.out.println("中文subsidiary_id:" + pic3D_id);
			}
			
			System.out.println("addPicture end...");
		}
		
		return true;
	}
	
    private String  handleThemeTxt(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String temp = null;
            StringBuilder sb = new StringBuilder();
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
	
    
    private static String ToPicture_src(String src) {
    	return src.substring(0, src.length()-9) + ".jpg";
    }
    
    private static String ToPicture_random(String src) {
    	return src.substring(src.length()-8, src.length()-4);
    }
    
	public static void main(String[] args) {
		//System.out.println(CallableImpl.format("【订货会】2015春夏意大利fendi芬迪时尚休闲女单鞋系列（意大利，fendi，单鞋，休闲）"));
		String str = "2015_epd/0518/1814/201505181403335515_4567.jpg";
		System.out.println(ToPicture_src(str));
		System.out.println(ToPicture_random(str));
	}
}
