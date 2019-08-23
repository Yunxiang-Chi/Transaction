import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class wallet {
	
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public HashMap<String,transactionOutput> UTXOs = new HashMap<String,transactionOutput>(); // wallet's UTXO
	//public ArrayList<transaction> history = new ArrayList<transaction>();
	
	wallet()
	{
		generateKeyPair();
	}
	
	// create private and public key through generating key-pair
	public void generateKeyPair()
	{
		try 
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	// return balance and save wallet's UTXO
	public float getBalance() 
	{
		float total = 0;
		for (Map.Entry<String, transactionOutput> item: tester.UTXOs.entrySet())
		{
			transactionOutput UTXO = item.getValue();
			if(UTXO.isMine(publicKey)) 
			{
				UTXOs.put(UTXO.id,UTXO); // add to unspent transactionOutput
				total += UTXO.value ;
			}
		}
		return total;
	}
	
	// create and return a new transaction belonging to the wallet
	public transaction sendFunds(PublicKey _recipient,float value )
	{
		// check the coins in the wallet
		if(getBalance() < value) 
		{
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}
		
		// build an input list
		ArrayList<transactionInput> inputs = new ArrayList<transactionInput>();
		float total = 0;
		for (Map.Entry<String, transactionOutput> item: UTXOs.entrySet())
		{
			transactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new transactionInput(UTXO.id));
			if(total > value) break;
		}
		
		transaction newTransaction = new transaction(publicKey, _recipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		for(transactionInput input: inputs)
		{
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
	
}
