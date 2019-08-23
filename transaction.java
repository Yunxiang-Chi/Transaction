import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;

public class transaction {
	
	public String transactionId;
	public PublicKey sender;
	public PublicKey recipient;
	public float value; // transaction amount
	public byte[] signature;
	public ArrayList<transactionInput> input = new ArrayList<transactionInput>();
	public ArrayList<transactionOutput> output = new ArrayList<transactionOutput>();
	private static int record = 0; // record how many created transactions 
	
	transaction(PublicKey from, PublicKey to, float value, ArrayList<transactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.input = inputs;
	}
	
	// return whether new transaction is created
	// and this method make sure that a usable output must be a valid input before
	public boolean processTransaction()
	{
		if(verifySignature() == false)
		{
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
		
		// collect input of transaction
		for(transactionInput i : input) 
		{
				i.UTXO = tester.UTXOs.get(i.transactionOutputId);
		}

		// check whether the transaction is valid
		if(getInputsValue() < tester.minimumTransaction) 
		{
			System.out.println("#Transaction Inputs too small: " + getInputsValue());
			return false;
		}
		
		// create transactionOutput
		float leftOver = getInputsValue() - value;
		transactionId = transactionHash();
		output.add(new transactionOutput(this.recipient,value,transactionId)); // send coins to recipient
		output.add(new transactionOutput(this.sender,leftOver,transactionId)); // give back leftOver to sender
		
		// put output into UTXO
		for(transactionOutput o : output)
		{
			tester.UTXOs.put(o.id , o);
		}
		
		// remove the input of transaction that has already paid
		for(transactionInput i : input)
		{
			if(i.UTXO == null) continue; // if Transaction can't be found skip it
			tester.UTXOs.remove(i.UTXO.id);
		}
		return true;
	}
	
	// return balances
	public float getInputsValue()
	{
		float total = 0;
		for(transactionInput i : input) 
		{
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		return total;
	}

	// return outputs' sum
	public float getOutputsValue()
	{
		float total = 0;
		for(transactionOutput o : output) 
		{
				total += o.value;
		}
		return total;
	}
	
	private String transactionHash()
	{
		record ++;
		return sha_256.getSHA256(ECDSA.getStringFromKey(sender) + 
								 ECDSA.getStringFromKey(recipient) +
								 Float.toString(value) + record);
	}
	
	public void generateSignature(PrivateKey privateKey) 
	{
		String data = ECDSA.getStringFromKey(sender)+ECDSA.getStringFromKey(recipient)+Float.toString(value);
		signature = ECDSA.applyECDSASign(privateKey,data);
	}
	
	public boolean verifySignature() 
	{
		String data = ECDSA.getStringFromKey(sender)+ECDSA.getStringFromKey(recipient)+Float.toString(value);
		return ECDSA.verifyECDSASig(sender, data, signature);
	}
	
}

// when a transaction happened, the transactionInput point to last transactionOutput
// so the balances in account are all unspent transactionOutput which once pointed to you.
class transactionInput
{
	public String transactionOutputId;
	public transactionOutput UTXO; // including all of unspent transactionOutput
	public transactionInput(String transactionOutputId) 
	{
		this.transactionOutputId = transactionOutputId;
	}
}

class transactionOutput
{
	public String id;
	public PublicKey recipient;
	public float value; // the amount of coins recipient had
	public String parentTransactionId; 
	
	public transactionOutput(PublicKey recipient, float value, String parentTransactionId) 
	{
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id=sha_256.getSHA256(ECDSA.getStringFromKey(recipient)+Float.toString(value)+parentTransactionId);
	}
	
	// to verify whether it's yours
	public boolean isMine(PublicKey publicKey)
	{
		return (publicKey == recipient);
	}
}
