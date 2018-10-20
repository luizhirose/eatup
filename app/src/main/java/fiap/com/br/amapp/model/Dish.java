package fiap.com.br.amapp.model;

public class Dish {
    private String produtoId;
    private String name;
    private String description;
    private Double price;
    private String image;
    private Integer quantity;

    public Dish(String produtoId, String name, String description, Double price, String image) {
        this.produtoId = produtoId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.quantity = 0;
    }

    public String getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
