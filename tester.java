import java.security.Security;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;
import java.util.Base64;
import java.util.HashMap;

public class tester {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	// the set of UTXOs
	public static HashMap<String,transactionOutput> UTXOs = new HashMap<String,transactionOutput>();
	public static int difficulty = 5;
	public static wallet walletA;
	public static wallet walletB;
	public static float minimumTransaction = 0.1f;
	public static transaction firstTransaction;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub				
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		walletA = new wallet();
		walletB = new wallet();
		wallet coinbase = new wallet();
		// create first transaction, sending 100 coins to A
		firstTransaction = new transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		firstTransaction.generateSignature(coinbase.privateKey);
		firstTransaction.transactionId = "0";
		// add transactionOutput by hand
		firstTransaction.output.add(new transactionOutput(firstTransaction.recipient, firstTransaction.value, firstTransaction.transactionId));
		UTXOs.put(firstTransaction.output.get(0).id, firstTransaction.output.get(0)); // save 1st transaction in UTXO
		System.out.println("Creating and Mining First Block... ");
		Block inception = new Block("0");
		inception.addTransaction(firstTransaction);
		addBlock(inception);

		
		Block block1 = new Block(inception.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		isChainValid();
		System.out.println();
		System.out.println("Made by Yunxiang Chi");
	}
	
	public static boolean isChainValid()
	{
		Block currentBlock = null;
		Block previousBlock = null;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,transactionOutput> tempUTXOs = new HashMap<String,transactionOutput>(); //temp list
		tempUTXOs.put(firstTransaction.output.get(0).id, firstTransaction.output.get(0));
		// check hash value
		for(int i=1; i < blockchain.size(); i++)
		{
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			// compare the hash value to the calculated hash value
			if(!currentBlock.hash.equals(currentBlock.calculateHash()))
			{
				System.out.println("Current Hash is not equal");
				return false;
			}
			// compare previous block's hash value with the previousHash.
			if(!previousBlock.hash.equals(currentBlock.previousHash)) 
			{
				System.out.println("Previous Hash is not equal");
				return false;
			}
			// check whether the hash has already calculated
			if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget))
			{
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		
		// iterate loop every transactions in the block 
		transactionOutput tempOutput;
		for(int t=0; t <currentBlock.transactions.size(); t++)
		{
			transaction currentTransaction = currentBlock.transactions.get(t);
			if(!currentTransaction.verifySignature())
			{
				System.out.println("#Signature on Transaction(" + t + ") is Invalid");
				return false;
			}
			if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue())
			{
				System.out.println("#Inputs are not equal to outputs on Transaction(" + t + ")");
				return false;
			}
			for(transactionInput input: currentTransaction.input)
			{
				tempOutput = tempUTXOs.get(input.transactionOutputId);
				if(tempOutput == null)
				{
					System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
					return false;
				}
				if(input.UTXO.value != tempOutput.value)
				{
					System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
					return false;
				}
				tempUTXOs.remove(input.transactionOutputId);
			}
			for(transactionOutput output: currentTransaction.output)
			{
				tempUTXOs.put(output.id, output);
			}
			if(currentTransaction.output.get(0).recipient != currentTransaction.recipient)
			{
				System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
				return false;
			}
			if(currentTransaction.output.get(1).recipient != currentTransaction.sender)
			{
				System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
				return false;
			}
		}
		System.out.println("The block chain is valid");
		return true;
	}
	
	public static void addBlock(Block newBlock)
	{
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	

}
