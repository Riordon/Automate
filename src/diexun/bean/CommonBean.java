package diexun.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaolong
 */
public abstract class CommonBean {
	private String attributes = "";
	private String title;
	private String srcTitle;
	private String att_ids;
	private String description;
	
	private Integer en_subject_id;
	private Integer es_subject_id;
	
	private String ftpDirectory;
	
	private Map<String, ArrayList<String>> mapPicture = new HashMap<String, ArrayList<String>>();
	private Map<String, String> mapLocalToFTP = new HashMap<String, String>();
	
	public void addAttribute(String attr) {
		attributes += attr + ",";
	}
	public String getAttributes() {
		return attributes;
	}	
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public Map<String, ArrayList<String>> getMapPicture() {
		return mapPicture;
	}
	public void setMapPicture(Map<String, ArrayList<String>> mapPicture) {
		this.mapPicture = mapPicture;
	}
	public String getSrcTitle() {
		return srcTitle;
	}
	public void setSrcTitle(String srcTitle) {
		this.srcTitle = srcTitle;
	}
	public String getAtt_ids() {
		return att_ids;
	}
	public void setAtt_ids(String att_ids) {
		this.att_ids = att_ids;
	}
	public Integer getEn_subject_id() {
		return en_subject_id;
	}
	public void setEn_subject_id(Integer en_subject_id) {
		this.en_subject_id = en_subject_id;
	}
	public Integer getEs_subject_id() {
		return es_subject_id;
	}
	public void setEs_subject_id(Integer es_subject_id) {
		this.es_subject_id = es_subject_id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, String> getMapLocalToFTP() {
		return mapLocalToFTP;
	}
	public void setMapLocalToFTP(Map<String, String> mapLocalToFTP) {
		this.mapLocalToFTP = mapLocalToFTP;
	}
	public String getFtpDirectory() {
		return ftpDirectory;
	}
	public void setFtpDirectory(String ftpDirectory) {
		this.ftpDirectory = ftpDirectory;
	}
}
