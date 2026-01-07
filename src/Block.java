import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private long timeStamp;
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String input = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + transactions.toString();
        return StringUtil.applySha256(input);
    }

    public void mineBlock(int difficulty) {
        String target = "0".repeat(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Блок успешно смайнен! Hash: " + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) return false;
        if (!transaction.verifySignature()) {
            System.out.println("Ошибка: подпись транзакции неверна!");
            return false;
        }
        transactions.add(transaction);
        return true;
    }
}