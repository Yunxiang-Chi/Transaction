import java.util.ArrayList;
import java.util.Date;

public class Block {
	
	public String hash;
	public String previousHash;
	private long timeStamp;
	private int nonce;
	public String merkleRoot;
	public ArrayList<transaction> transactions = new ArrayList<transaction>();
	
	Block(String previousHash)
	{
		this.previousHash = previousHash;
		Date date = new Date();
		this.timeStamp = date.getTime();
		this.hash = calculateHash();
	}
	
	public String calculateHash()
	{
		String calhash = sha_256.getSHA256(previousHash + Long.toString(timeStamp) 
						 + Integer.toString(nonce) + merkleRoot);
		return calhash;
	}
	
	public void mineBlock(int difficulty)
	{
		// create a string value which depends on the diffculty's total digits
		String target = new String(new char[difficulty]).replace('\0', '0');
		double timep = System.currentTimeMillis();
		while(!hash.substring(0, difficulty).equals(target))
		{
			nonce ++;
			hash = calculateHash();
		}
		double timen = System.currentTimeMillis();
		System.out.println("Congratuations! A Block Is Mined: " + hash 
							+ "The total time-consuming is: " + (timen - timep)/1000 + "s.");
	}	
	
	public boolean addTransaction(transaction _transaction)
	{
		// check if the transaction is valid, ignoring the first block
		if(_transaction == null) return false;
		if((previousHash != "0"))
		{
			if((_transaction.processTransaction() != true))
			{
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		transactions.add(_transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
	
}
