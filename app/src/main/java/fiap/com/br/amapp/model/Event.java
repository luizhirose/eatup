package fiap.com.br.amapp.model;

import java.sql.Timestamp;
import java.util.Date;
//import java.util.Date;

public class Event {
    private String id;
    private String name;
    private String description;
    private String street;
    private String estado;
    private String cidade;
    private String phone;
    private String email;
    private Date beginDateTime;
    private Date endDateTime;

    public Event() {}

    public Event(String id, String name, String description, String street, String estado, String cidade, String phone, String email, Date beginDateTime, Date endDateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.street = street;
        this.estado = estado;
        this.cidade = cidade;
        this.phone = phone;
        this.email = email;
        this.beginDateTime = beginDateTime;
        this.endDateTime = endDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(Date beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }
}
