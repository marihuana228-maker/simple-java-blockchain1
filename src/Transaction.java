import java.security.*;

public class Transaction {
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;

    public Transaction(PublicKey from, PublicKey to, float value) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = getStringData();
        try {
            Signature dsa = Signature.getInstance("SHA256withECDSA");
            dsa.initSign(privateKey);
            dsa.update(data.getBytes());
            signature = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifySignature() {
        if (sender == null) return true; // Для вознаграждений майнерам
        String data = getStringData();
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
            ecdsaVerify.initVerify(sender);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String getStringData() {
        return (sender != null ? sender.toString() : "SYSTEM") + recipient.toString() + Float.toString(value);
    }
}