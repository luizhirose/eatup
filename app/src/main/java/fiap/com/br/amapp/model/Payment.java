package fiap.com.br.amapp.model;

public class Payment {

    private volatile static Payment _payment;
    private String cardBrand;
    private String cpf;
    private String cardNumber;
    private String cardExp;
    private String cvv;

    public static Payment getInstance() {
        if (_payment == null) {
            synchronized (Payment.class) {
                if (_payment == null) _payment = new Payment();
            }
        }
        return _payment;
    }

    public Payment() {
    }

    public Payment(String cardBrand, String cpf, String cardNumber, String cardExp, String cvv) {
        this.cardBrand = cardBrand;
        this.cpf = cpf;
        this.cardNumber = cardNumber;
        this.cardExp = cardExp;
        this.cvv = cvv;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExp() {
        return cardExp;
    }

    public void setCardExp(String cardExp) {
        this.cardExp = cardExp;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "cardBrand='" + cardBrand + '\'' +
                ", cpf='" + cpf + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardExp='" + cardExp + '\'' +
                ", cvv='" + cvv + '\'' +
                '}';
    }
}
