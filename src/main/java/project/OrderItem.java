package project;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;
@Getter
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);

    private Product product; //товар
    private int amount; //количество товара одной категории
    @Getter
    private int price;

    public OrderItem(int amount, int productId) {
        this.amount = amount;
        this.product = Catalog.findProductById(productId);
        this.price = product.getPrice()*amount;

    }

    @Override
    public String toString() {
        return "++++++++++++++\n" + "Продукт " + this.product.getName() + " -" + amount +"\n++++++++++++++";
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(product, orderItem.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product);
    }




}
