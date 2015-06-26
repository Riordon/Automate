package diexun.util;

import java.security.MessageDigest;

/**
 * MD5 简单加密字符串
 * @author kay.yang
 *
 */
public class MD5
{
	/**
	 * MD5加密
	 * @return MD5
	 * @param 需要加密的字符串
	 */
	public static String md5(String text)
	{
		if (text == null)
			return "";
		StringBuffer hexString = new StringBuffer();
		try
		{

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] digest = md.digest();
			for (int i = 0; i < digest.length; i++)
			{
				text = Integer.toHexString(0xFF & digest[i]);
				if (text.length() < 2)
				{
					text = "0" + text;
				}
				hexString.append(text);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return hexString.toString();
	}
}

