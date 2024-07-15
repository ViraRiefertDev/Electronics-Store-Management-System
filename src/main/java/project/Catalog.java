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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Catalog implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Catalog.class);
    private static List<Category> categoryList;

    static {
        String fileName = "catalog.ser";
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) {
            categoryList = new ArrayList<>();
            LOGGER.info("Создан новый каталог");
        }
    }


    //метод добавления нового товара в каталог
    //если каталога с таким именнем еще нет, создается новый каталог
    public static void addNewProductInCatalog(String categoryName, int id, String name, String description, int price, int quantityInStock) {
        Category category;
        if (isCategoryExist(categoryName)) {
            category = foundCategoryByName(categoryName);

        } else {
            category = new Category(categoryName);
            categoryList.add(category);
        }
        category.addNewProduct(id, name, description, price, quantityInStock, categoryName);


    }


    //метод возвращает Map из списка всех товаров в каталоге
    public static Map<Integer, Product> getAllProductsInCatalog() {
        Map<Integer, Product> allProductsInCatalog = new HashMap<>();
        for (Category category : categoryList) {
            allProductsInCatalog.putAll(category.getAllProducts());
        }
        return allProductsInCatalog;
    }

    //метод возвращает список только тех товаров, которые реальное есть в магазине  quantityInStock >0
    public static Map<Integer,Product> getAllAvailableProducthsInStock(){
        Map<Integer, Product> products = getAllProductsInCatalog();
        products.entrySet().stream()
                .filter(entry->entry.getValue().getQuantityInStock()>0)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        return products;
    }

    public static Product findProductById(int productId) {
        return getAllProductsInCatalog().get(productId);
    }

    public static void updateStockQuantityByProductId(int productId, int newProductQuantity) {
        if (checkId(productId)) {
            Map<Integer, Product> listToCheck = getAllProductsInCatalog();
            Product product = listToCheck.get(productId);
            product.setQuantityInStock(newProductQuantity);
            LOGGER.info("Количество товара " + productId + " на складе теперь " + product.getQuantityInStock());
        } else {
            LOGGER.info("Товара с id " + productId + " на складе нет!");
        }
    }

    //метод возвращает количество товара на складе по id
    public static int getStockQuantityByProductId(int productId) {

        Map<Integer, Product> listToCheck = getAllProductsInCatalog();
        return listToCheck.get(productId).getQuantityInStock();


    }

    //Метод сериализации списка проектов
    public static void serializeCatalog() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("catalog.ser"))) {
            oos.writeObject(categoryList);
            LOGGER.info("Каталог был сериализован в файл catalog.ser");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    //Метод десериализации каталога
    public static void deserializeCatalog() {

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("catalog.ser"))) {
            categoryList = (List<Category>) ois.readObject();
            LOGGER.info("Файл catalog.ser был успешно десериализован, и каталог сохранен!");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static boolean isCategoryExist(String categoryName) {
        if (!categoryList.isEmpty()) {
            return categoryList.stream()
                    .anyMatch(category -> (category.getName().equals(categoryName)));
        } else {
            return false;
        }
    }

    public static Category foundCategoryByName(String categoryName) {
        return categoryList.stream().filter(category -> category.getName().equals(categoryName)).findFirst().orElse(null);
    }


    public static boolean checkId(int id) {
        return getAllProductsInCatalog().containsKey(id);
    }

    public static void showAllProducts() {
        if (categoryList.isEmpty()) {
            LOGGER.info("Пока каталог товаров пуст");
        } else {
            for (Category category : categoryList) {
                if (!category.getAllProducts().isEmpty()) {
                    System.out.println("----------Категория: " + category.getName() + "-----------");

                    category.displayAllProducts();
                }
            }
        }
    }

    //метод удаления товара по id
    public static void removeProductById(int productId) {
        boolean isFount = false;
        for (Category category : categoryList) {
            if (category.isProductExist(productId)) {
                category.removeProductById(productId);
                isFount = true;
                break;
            }
        }
        if (!isFount) {
            LOGGER.info("Товара с id: " + productId + " в каталоге не найдено!");
        }

    }


}
