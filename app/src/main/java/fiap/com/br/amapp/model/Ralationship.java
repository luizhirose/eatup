package fiap.com.br.amapp.model;

public class Ralationship {

    private String eventId;
    private String establishmentId;

    public Ralationship(String eventId, String establishmentId) {
        this.eventId = eventId;
        this.establishmentId = establishmentId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEstablishmentId() {
        return establishmentId;
    }

    public void setEstablishmentId(String establishmentId) {
        this.establishmentId = establishmentId;
    }
}
