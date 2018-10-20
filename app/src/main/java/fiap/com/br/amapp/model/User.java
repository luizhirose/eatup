package fiap.com.br.amapp.model;

public class User {

    private String id;
    private String name;
    private static String password;
    private String cpf;
    private static String email;
    private String phone;
    private String idPayment;

    public User(String name, String password, String cpf, String email, String phone) {
        this.name = name;
        this.password = password;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
    }

    public User(String name, String password, String cpf, String email, String phone,String id,String idPayment) {
        this.name = name;
        this.password = password;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.idPayment = idPayment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(String idPayment) {
        this.idPayment = idPayment;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
