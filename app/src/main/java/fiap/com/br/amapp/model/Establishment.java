package fiap.com.br.amapp.model;

public class Establishment {
    private String id;
    private Integer rating;
    private String cidade;
    private String cnpj;
    private String description;
    private String email;
    private String estado;
    private String logradouro;
    private String name;
    private String phone;

    public Establishment() {
    }

    public Establishment(String id, Integer rating, String cidade, String cnpj, String description, String email, String estado, String logradouro, String name, String phone) {
        this.id = id;
        this.rating = rating;
        this.cidade = cidade;
        this.cnpj = cnpj;
        this.description = description;
        this.email = email;
        this.estado = estado;
        this.logradouro = logradouro;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
