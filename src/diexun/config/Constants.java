package diexun.config;

import java.util.HashMap;

/**
 * @author xiaolong
 */
public class Constants {
	public static final String CLOTHING_WEBSITE = "服装网";
    public static final String SHOSE_WEBSITE = "鞋业网";
    public static final String BAG_WEBSITE = "箱包网";
    
    public static final String EPD_SHOEBAG_WEBSITE = "EPD鞋包";
    public static final String EPD_CLOTHING_SEBSITE = "EPD服装";
	
	
	/*------------------------MAP---------------------*/
	//所有网站
	public static final HashMap<String, Integer> MAP_WEBSITES = new HashMap<String, Integer>();
	static {
		MAP_WEBSITES.put(CLOTHING_WEBSITE, 1);
		MAP_WEBSITES.put(SHOSE_WEBSITE, 2);
		MAP_WEBSITES.put(BAG_WEBSITE, 3);
		MAP_WEBSITES.put(EPD_SHOEBAG_WEBSITE, 4);
		MAP_WEBSITES.put(EPD_CLOTHING_SEBSITE, 5);
	}
	
	/*-------------------------------------------------*/
	public static final HashMap<String, String> MAP_EN_CH = new HashMap<String, String>();
	static {
		MAP_EN_CH.put("clothing", CLOTHING_WEBSITE);
		MAP_EN_CH.put("shose", SHOSE_WEBSITE);
		MAP_EN_CH.put("bag", BAG_WEBSITE);
		MAP_EN_CH.put("epdshoebag", EPD_SHOEBAG_WEBSITE);
		MAP_EN_CH.put("epdclothing", EPD_CLOTHING_SEBSITE);
	}
	
	//所有栏目
	public static final HashMap<String, Integer> MAP_COLUMNS = new HashMap<String, Integer>();
	static {
		
	}
}
