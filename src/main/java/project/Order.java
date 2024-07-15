package project;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter
public class Order implements Serializable {
   private Customer customer;

   private LocalDate date;
    @Setter
   private DeliveryStatus status;

   private Map<Integer,OrderItem> orderItems;

   public Order(Customer customer, LocalDate date) {
      this.customer = customer;
      this.date = date;
      this.status = DeliveryStatus.PROCESSING;
      this.orderItems = new HashMap<>();

   }

   public int getOrderItemsAmount(){
       return orderItems.size();
   }

  public void printOrderItems(){
      for (OrderItem orderItem : orderItems.values()) {
          System.out.println("-----------------");
          System.out.println("#"+orderItem.getProduct().getId() +": " +orderItem.getProduct().getName() + " \nКоличество: " + orderItem.getAmount() + "\nЦена: " +((double)orderItem.getPrice())/100 );
      }
  }

  public void byProduct(){
      for (OrderItem orderItem : orderItems.values()) {
          orderItem.getProduct().byProduct(orderItem.getAmount());
      }
  }



   //Добавить в заказ позицию OrderItem
   public void addOrderItem(int amount,int productId){
      OrderItem orderItem = new OrderItem(amount,productId);
      orderItems.put(productId,orderItem);
   }

   //метод возращает цену заказа
    public double getTotalPrice(){
       double sum = 0;
        for (OrderItem orderItem : orderItems.values()) {
           sum += orderItem.getPrice();
        }
        return sum/100;
    }



}
