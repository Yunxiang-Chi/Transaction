import java.util.ArrayList;

public class merkleRoot {
	
	public static String getMerkleRoot(ArrayList<transaction> transactions)
	{
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(transaction transaction : transactions)
		{
			previousTreeLayer.add(transaction.transactionId);
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1)
		{
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++)
			{
				treeLayer.add(sha_256.getSHA256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
	
}
