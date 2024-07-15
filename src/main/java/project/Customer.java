package project;

import com.github.javafaker.Faker;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Customer.class);


    private String name;
    private contactInfo contactInfo;
    private List<Order> orderHistory;


    public Customer() {
        Faker faker = new Faker(new Locale("de-De"));
        this.name = faker.name().fullName();
        this.contactInfo = new contactInfo(name);
        orderHistory = new ArrayList<>();
    }

    //удалить последний заказ
    public void removeLastOrder(){
        orderHistory.remove(orderHistory.size()-1);
    }

    @Override
    public String toString() {
        return "++++++++++++++\n" + "Имя " + this.name + "\nКонтактная информация: \n" + this.contactInfo +"\n++++++++++++++";
    }
    
    
    //добавить новый заказ
    public Order addNewOrder(LocalDate date){
        Order order = new Order(this, date);
        orderHistory.add(order);
        LOGGER.info("New Order for customer" + this.name + "was successfull created");
        return order;
    }
    

    //метод показывает историю покупок пользователя
    public void showOrderHistory() {
        if (orderHistory.isEmpty()) {
            System.out.println("Список заказов пуст");;
        } else {
          List<Order> filteredList = orderHistory.stream().sorted(Comparator.comparing(Order::getDate)).collect(Collectors.toList());
            for (Order order : filteredList) {
                System.out.println("Дата заказа " + order.getDate());
                System.out.println("Статус заказа: " + order.getStatus());
                order.printOrderItems();
            }
        }
    }

    public boolean isOrderWithTargetDate(LocalDate date){
        return orderHistory.stream()
                .anyMatch(order -> order.getDate().equals(date));
    }

    //Метод меняет статус заказа(ов) за определенную датой
    public void updateOrderStatusByDate(LocalDate date,DeliveryStatus newStatus){
        orderHistory.stream()
                .filter(order->order.getDate().equals(date))
                .forEach(order -> order.setStatus(newStatus));
    }

    //метод возвращает общую сумму заказа за определенный период времени
    public double getTotalPriceForPeriod(LocalDate startDate,LocalDate endDate){
        double totalPrice =0;
        for (Order order : orderHistory) {
           if(order.getDate().isAfter(startDate)&&order.getDate().isBefore(endDate)){
              totalPrice += order.getTotalPrice();
           }
        }
        return totalPrice;
    }

    //метод возвращает общее количество товаров за определенный период времени
    public int getTotalAmountForPeriod(LocalDate startDate,LocalDate endDate){
        int totalAmount =0;
        for (Order order : orderHistory) {
            if(order.getDate().isAfter(startDate)&&order.getDate().isBefore(endDate)){
                totalAmount += order.getOrderItemsAmount();
            }
        }
        return totalAmount;
    }

    private class contactInfo implements Serializable {
        private String address;
        private String phoneNumber;
        private String email;


        public contactInfo(String name) {
            Faker faker = new Faker(new Locale("de-De"));
            this.address = faker.address().fullAddress();
            this.phoneNumber = faker.phoneNumber().phoneNumber();
            this.email = name.replaceAll("\\s+", "") + "@.gmail";
        }

        @Override
        public String toString() {
            return "*****\n" + "Адресс " + this.address + "\nТелефон: " + this.phoneNumber + "\nemail: " + email
                    + "\n*****";
        }
    }


}


