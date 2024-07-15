package project;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Category implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Category.class);

    @Getter
    private String name;
   private List<Product> products;

    public Category(String name) {
        this.name = name;
        products = new ArrayList<>();
    }

    //метод возвращает HashMap всех продуктов в категории
    public Map<Integer,Product> getAllProducts(){
       return products.stream().collect(Collectors.toMap(Product::getId, product -> product));
    }

    //метод для добавления нового товара в список товаров
    public void addNewProduct(int id,String name, String description, int price, int quantityInStock, String category){
        Product product = new Product(id,name, description, price, quantityInStock, category);
        if(!isProductExist(id)){
            products.add(product);
            LOGGER.info("Товар id " + id + ":" + name + "был успешно добавлен в список товаров");
        }
        else{
            LOGGER.error("Товар с id: " + id + " уже существует в списке товаров!");
        }
    }

    public boolean isProductExist(int productId) {
        if (!products.isEmpty()) {
            return products.stream()
                    .anyMatch(product -> (product.getId()==productId));
        } else {
            return false;
        }
    }

    public  void displayAllProducts() {
        if (!products.isEmpty()) {
            for (Product product : products) {
                System.out.println(product);

            }
        }
    }

    public void removeProductById(int productId){
        Iterator<Product> iterator = products.iterator();
        while(iterator.hasNext()){
            Product product = iterator.next();
            iterator.remove();
            LOGGER.info("Товар с id: " +productId + " был успешно удален!");
        }
    }
}
