package diexun.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import diexun.config.Constants;
import diexun.main.Scanner;

/**
 * @author xiaolong
 */
public class CacheSystem {
	
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static final WriteLock writeLock = lock.writeLock();
	private static final ReadLock readLock = lock.readLock();
	
	private static String epd_shoebag_cache;
	private static String epd_clothing_cache;
	
	static {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        
		epd_shoebag_cache = Scanner.CACHE_ROOT + File.separator + Constants.EPD_SHOEBAG_WEBSITE + File.separator + year + month + day + "_theme";
		epd_clothing_cache = Scanner.CACHE_ROOT + File.separator + Constants.EPD_SHOEBAG_WEBSITE + File.separator + year + month + day + "_theme";
	}
	
	public static void writeTheme(String theme) {
		FileWriter writer;
		try {
			writeLock.lock();
			writer = new FileWriter(epd_shoebag_cache, true);
		    writer.write(theme);
		    writer.write("\r\n");
		    writer.close();  
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writeLock.unlock();
		}
	}
	
	public static boolean isScan(String theme) {
		FileReader reader; 
		try {
			readLock.lock();
			reader = new FileReader(epd_shoebag_cache);
			BufferedReader br = new BufferedReader(reader); 
			while (br.ready()) {
				if (br.readLine().equals(theme)) return true;
			}
			reader.close();  
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			readLock.unlock();
		}
		return false;
	}
}
