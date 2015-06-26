package diexun.webservice;

/**
 * @author xiaolong
 */
public class WebServiceHander {
    private static GetattrinfoLogicPort queryp;
    private static AutouploadLogicPort insertp;
    
    static {
    	GetattrinfoLogic abl = new GetattrinfoLogic();
    	AutouploadLogic ab2 = new AutouploadLogic();
    	
    	queryp = abl.getPort(GetattrinfoLogicPort.class);
        insertp = ab2.getPort(AutouploadLogicPort.class);
    }
    
    public static GetattrinfoLogicPort query() {
    	return queryp;
    }
    
    public static AutouploadLogicPort insert() {
    	return insertp;
    }
}
