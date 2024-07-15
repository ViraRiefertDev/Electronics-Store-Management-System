package project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerManager.class);

    private static Map<String, Customer> allCustomers;

    static {
        String fileName = "customer.ser";
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) {
            allCustomers = new HashMap<>();
            LOGGER.info("Создан новый лист покупателей");
        }
    }


    //-------------------------------------------------------------------------------------
    //Метод сериализации списка сотрудников
    public static void serializeCustomer() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("customer.ser"))) {
            oos.writeObject(allCustomers);
            LOGGER.info("Список сотрудников был сериализован в файл customer.ser");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    //Метод десериализации списка объектов
    public static void deserializeCustomers() {

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("customer.ser"))) {
            allCustomers = (Map<String, Customer>) ois.readObject();
            LOGGER.info("Файл customer.ser был успешно десериализован, и сотрудники сохранены в список сотрудников!");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }
    //-------------------------------------------------------------------------------------

    //метод добавления нового клиента
    public static String addNewCustomer() {
        Customer customer = new Customer();
        allCustomers.put(customer.getName(), customer);
        return customer.getName();
    }

    public static void printAllCustomers() {
        System.out.println("-----Cписок всех клиентов-----");
        if (allCustomers.isEmpty()) {
            LOGGER.info("Еще не было добавлено ни одного клиента :(");
        }
        for (Customer customer : allCustomers.values()) {
            System.out.println("********");
            System.out.println(customer);
        }
    }

//    public static boolean isCustumerExist(String customerId) {
//        return  allCustomers.containsKey(customerId);
//    }

    public static Customer foundCustomerByName(String customerName) {
        return allCustomers.get(customerName);
    }

    //метод генерации отчета о продажах
    public static void salesReport(LocalDate startData, LocalDate endData) {
        double totalPrice = 0;
        int totalAmount = 0;
        for (Customer customer : allCustomers.values()) {
            totalPrice += customer.getTotalPriceForPeriod(startData, endData);
            totalAmount += customer.getTotalAmountForPeriod(startData, endData);
        }


        System.out.println("Отчет о продажах" + startData + " -- " + endData + "успешно сгенерирован");
        System.out.println("Общая выручка: " + totalPrice);
        System.out.println("Продавно товаров:"+ totalAmount);
    }

}
