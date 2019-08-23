import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class sha_256 {
	
	public static String getSHA256(String str) {
		MessageDigest messageDigest;
		String hashCode;
		String encstr = "";
		try 
		{
			messageDigest = MessageDigest.getInstance("SHA-256"); // get sha-256 algorithm
			messageDigest.update(str.getBytes("UTF-8")); // introduce UTF-8 code
			hashCode = byteHex(messageDigest.digest()); // use byteHex to produce hashcode
			encstr = hashCode;
		}
		catch (NoSuchAlgorithmException e) // warn: no such algorithm in the enviroment
		{
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e) // warn: errors on UTF-8
		{
			e.printStackTrace();
		}
		return encstr;
	}
 
	public static String byteHex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) 
		{
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) // add 0 toward temp which has length 1
			{
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

}
