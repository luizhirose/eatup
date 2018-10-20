package fiap.com.br.amapp.model;

import java.util.ArrayList;

public class Order {

    private volatile static Order _order;

    private double generalRating;
    private double attendanceRanting;
    private double foodRating;
    private double speedRating;
    private String comments;
    private String status;
    private double total;
    private String paymentId;
    private User user;
    private String establishmentId;
    private String eventId;
    private String orderId;
    private ArrayList<Dish> itemList;

    public static Order getInstance() {
        if (_order == null) {
            synchronized (Order.class) {
                if (_order == null) _order = new Order();
            }
        }
        return _order;
    }

    public Order() {
        this.itemList = new ArrayList<>();
        this.total=0.0;

    }

    public void resetOrder(){
        itemList = new ArrayList<>();
    }

    public void refreshTotal(){
        Double totalVal = 0.0;

        for (Dish myDish: this.itemList) {
            totalVal+= myDish.getPrice()* myDish.getQuantity().doubleValue();
        }

        this.total = totalVal;
    }

    public ArrayList<Dish> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Dish> itemList) {
        this.itemList = itemList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getGeneralRating() {
        return generalRating;
    }

    public void setGeneralRating(double generalRating) {
        this.generalRating = generalRating;
    }

    public double getAttendanceRanting() {
        return attendanceRanting;
    }

    public void setAttendanceRanting(double attendanceRanting) {
        this.attendanceRanting = attendanceRanting;
    }

    public double getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(double foodRating) {
        this.foodRating = foodRating;
    }

    public double getSpeedRating() {
        return speedRating;
    }

    public void setSpeedRating(double speedRating) {
        this.speedRating = speedRating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEstablishmentId() {
        return establishmentId;
    }

    public void setEstablishmentId(String establishmentId) {
        this.establishmentId = establishmentId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "generalRating=" + generalRating +
                ", attendanceRanting=" + attendanceRanting +
                ", foodRating=" + foodRating +
                ", speedRating=" + speedRating +
                ", comments='" + comments + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", paymentId='" + paymentId + '\'' +
                ", user=" + user +
                ", establishmentId='" + establishmentId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", itemList=" + itemList +
                '}';
    }
}
