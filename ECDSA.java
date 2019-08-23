import java.security.*;
import java.util.Base64;

public class ECDSA {
		
	// use ECDSA to generate signature and array
	public static byte[] applyECDSASign(PrivateKey privateKey, String input)
	{
		Signature dsa;
		byte[] output = new byte[0];
		try 
		{
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSign = dsa.sign();
			output = realSign;
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return output;
	}
	
	// use ECDSA verify signature
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature)
	{
		try 
		{
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// return any key's encoded String
	public static String getStringFromKey(Key key) 
	{
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
}
