package project;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

@Getter
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);


    private int id;
    private String name;
    private String description;
    private int price;
    @Setter
    private int quantityInStock;

    private Category category;

    public Product(int id,String name, String description, int price, int quantityInStock, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.category = new Category(category);
    }

    @Override
    public String toString() {
        return "++++++++++++++\n" + "#" + this.id + ": " + this.name + "\nОписание: "
                + this.description + "\nКатегория: " + category.getName()+ "\nЦена: " + price  +"\nКоличество на складе: " +quantityInStock+"\n++++++++++++++";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void byProduct(int count){
        quantityInStock -=count;
    }
}


