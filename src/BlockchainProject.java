import java.security.PublicKey;
import java.util.ArrayList;

public class BlockchainProject {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static int difficulty = 3;
    public static float miningReward = 50.0f; // Вознаграждение майнеру

    public static void main(String[] args) {
        // Создаем участников
        Wallet miner = new Wallet();
        Wallet userB = new Wallet();

        System.out.println("Баланс майнера в начале: " + getBalance(miner.publicKey));

        // --- БЛОК 1 (Генезис) ---
        // Майнер добывает первый блок и получает награду
        Block genesis = new Block("0");
        addBlock(genesis, miner.publicKey);

        System.out.println("Баланс майнера после добычи 1-го блока: " + getBalance(miner.publicKey));

        // --- БЛОК 2 ---
        // Майнер отправляет 10 монет пользователю B
        Block block2 = new Block(genesis.hash);
        float sendAmount = 10.0f;

        if (getBalance(miner.publicKey) >= sendAmount) {
            Transaction tx1 = new Transaction(miner.publicKey, userB.publicKey, sendAmount);
            tx1.generateSignature(miner.privateKey);

            if (block2.addTransaction(tx1)) {
                addBlock(block2, miner.publicKey); // Майнер снова добывает блок
            }
        }

        // --- ИТОГИ ---
        System.out.println("\n===== ФИНАЛЬНЫЙ ОТЧЕТ =====");
        System.out.println("Баланс майнера: " + getBalance(miner.publicKey) + " (награда 50+50 минус отправка 10)");
        System.out.println("Баланс пользователя B: " + getBalance(userB.publicKey));
        System.out.println("Блокчейн валиден: " + isChainValid());
    }

    public static float getBalance(PublicKey address) {
        float balance = 0f;
        for (Block block : blockchain) {
            for (Transaction t : block.transactions) {
                if (t.recipient.equals(address)) balance += t.value;
                if (t.sender != null && t.sender.equals(address)) balance -= t.value;
            }
        }
        return balance;
    }

    public static void addBlock(Block newBlock, PublicKey minerAddress) {
        // Сначала добавляем транзакцию награды майнеру
        Transaction rewardTx = new Transaction(null, minerAddress, miningReward);
        newBlock.transactions.add(rewardTx);

        // Затем майним блок
        System.out.print("Запуск майнинга... ");
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public static Boolean isChainValid() {
        for (int i = 1; i < blockchain.size(); i++) {
            Block current = blockchain.get(i);
            Block prev = blockchain.get(i - 1);
            if (!current.hash.equals(current.calculateHash())) return false;
            if (!current.previousHash.equals(prev.hash)) return false;
            for (Transaction t : current.transactions) {
                if (!t.verifySignature()) return false;
            }
        }
        return true;
    }
}