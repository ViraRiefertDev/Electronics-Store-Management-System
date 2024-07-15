package project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        loadingFromFiles();

        System.out.println("Добро пожаловать в систему управления магазином электроники!");

        boolean exit = false;
        while (!exit) {
            System.out.println("*********************************");
            System.out.println("Выберите действие:");
            System.out.println("0. Exit");
            System.out.println("1. Управление товарами");
            System.out.println("2. Управление заказами");
            System.out.println("3. Управление клиентами");
            System.out.println("4. Генерация отчета о продажах");
            System.out.println("**********************************");
            System.out.print("Введите Ваш выбор: ");
            checkValueInt();

            int choise = scanner.nextInt();
            switch (choise) {
                case 0: {
                    exit = true;
                    Catalog.serializeCatalog();
                    CustomerManager.serializeCustomer();
                    scanner.close();
                    LOGGER.info("Сканнер закрыт!");
                    System.out.println("До свидания!");
                    break;
                }
                case 1:
                    productManager();
                    break;
                case 2:
                    orderManager();
                    break;
                case 3:
                    customerManager();
                    break;
                case 4:
                    System.out.println("Вы выбрали генерацию отчета о продажах");
                    showSalesReport();

                    break;
                default:
                    System.out.println("Введите число от 0 до " + 4);
                    break;


            }

        }
    }

    private static void showSalesReport() {
        scanner.nextLine();
        LocalDate startDate = null;
        LocalDate endDate = null;
        boolean incorrectePeriode = true;
        while (incorrectePeriode) {
            System.out.println("Введите начальную дату");
            startDate = inputCorrectData();
            System.out.println("Введите конечную дату");
            endDate = inputCorrectData();
            if (startDate.isAfter(endDate)) {
                LOGGER.error("Вы ввели не корректный период времени! Начальная дата не может быть после конечной");
            } else {
                CustomerManager.salesReport(startDate, endDate);
                incorrectePeriode = false;
            }
        }
    }

    private static void orderManager() {
        boolean exit = false;
        while (!exit) {
            System.out.println("*********************************");
            System.out.println("Выберите действие:");
            System.out.println("0. Exit");
            System.out.println("1. Создание заказа для клиента");
            System.out.println("2. Обновление статуса заказа ");

            System.out.println("**********************************");
            System.out.print("Введите Ваш выбор: ");
            checkValueInt();

            int choise = scanner.nextInt();


            switch (choise) {
                case 0: {
                    exit = true;
                    break;
                }
                case 1: {
                    System.out.println("Вы выбрали создание заказа для клиента");
                    creatingNewOrder();


                    break;
                }
                case 2: {
                    System.out.println("Вы выбрали обновеление статуса заказа");
                    orderStatusUpdate();
                    break;
                }
                default:
                    System.out.println("Введите число от 0 до " + 2);
                    break;

            }
        }

    }

    private static void orderStatusUpdate() {
        scanner.nextLine();
        System.out.println("Введите имя заказчика");
        String customerName = scanner.nextLine();
        Customer customer = CustomerManager.foundCustomerByName(customerName);
        if (customer == null) {
            LOGGER.warn("Заказчик с именем " + customerName + " не найден!");
        } else {
            LocalDate date = inputCorrectData();
            //проверка существует ли заказ с этой датой
            boolean hasData = customer.isOrderWithTargetDate(date);
            if (hasData) {
                DeliveryStatus newStatus = inputCorrectDeliveryStatus();
                customer.updateOrderStatusByDate(date, newStatus);
                LOGGER.info("Статус заказа по дате " + date + " был успешно обновлен!");
            } else {
                LOGGER.warn(date + " не было произведено ни одного заказа!");
            }
        }

    }

    private static DeliveryStatus inputCorrectDeliveryStatus() {
        scanner.nextLine();
        DeliveryStatus newStatus = null;
        while (newStatus == null) {
            System.out.println("Введите новый статус задачи IN_TRANSIT, OUT_FOR_DELIVERY,DELIVERED,RETURNED,CANCELED");
            String newTaskStatusStr = scanner.nextLine();
            try {
                newStatus = DeliveryStatus.valueOf(newTaskStatusStr);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Ошибка: Введен неправильный статус задачи: " + newTaskStatusStr);
            }
        }
        return newStatus;
    }

    private static void creatingNewOrder() {
        boolean stockIsEmpty = Catalog.getAllAvailableProducthsInStock().isEmpty();
        if (stockIsEmpty) {
            LOGGER.info("К сожалению склад пуст!");
            System.out.println("Прийдите позже...");
        } else {
            scanner.nextLine();
            System.out.println("Введите имя клиента");
            String customerName = scanner.nextLine();
            LocalDate date = inputCorrectData();
            Customer customer = CustomerManager.foundCustomerByName(customerName);
            Order order = customer.addNewOrder(date);
            //добавление новых покупок
            boolean exit = false;
            while (!exit) {
                if (stockIsEmpty) {
                    LOGGER.info("К сожалению склад пуст!");
                    LOGGER.info("Невозможно добавить новые товары");
                    exit = true;
                } else {
                    System.out.println("Введите данные о товаре: ");
                    System.out.println("ID товара: ");
                    checkValueInt();
                    int productId = scanner.nextInt();
                    if (order.getOrderItems().containsKey(productId)) {
                        System.out.println("Этот товар уже находится в списке заказа!");
                    } else {
                        boolean isProductInStock = Catalog.getAllAvailableProducthsInStock().containsKey(productId);
                        if (isProductInStock) {
                            int productAmount = 0;
                            Product product = Catalog.findProductById(productId);
                            int quantityOfProductInStock = product.getQuantityInStock();
                            do {
                                System.out.println("Актуальное количество товара на складе: " + quantityOfProductInStock);
                                System.out.print("Введите количество: ");
                                checkValueInt();
                                productAmount = scanner.nextInt();
                            } while (productAmount > quantityOfProductInStock);
                            order.addOrderItem(productAmount, productId);
                            LOGGER.info("Товар Id:" + productId + " в количестве " + productAmount + "шт. был успешно добавлен в корзину");
                            LOGGER.info("Цена за " + productAmount + " товара: " + ((double) (product.getPrice() * productAmount)) / 100);
                            LOGGER.info("Общая цена заказа: " + order.getTotalPrice());
                        } else {
                            System.out.println("Товара с id " + productId + "нет на складе!");
                        }
                    }
                }
                if (!exit) {
                    System.out.println("Хотите добавить еще один товар?Y/N");
                    String answer = scanner.next();
                    if (answer.equalsIgnoreCase("n")) {
                        exit = true;
                    }
                }
            }
            if (order.getOrderItemsAmount() != 0) {
                System.out.println("Оформить заказ? Y/N");
                String answer = scanner.next();
                if (answer.equalsIgnoreCase("Y")) {
                    LOGGER.info("Заказ успешно создан и запланирован на отправку!");
                    order.byProduct();

                } else {
                    customer.removeLastOrder();
                    LOGGER.info("Заказ удален!");

                }
            } else {
                customer.removeLastOrder();
                LOGGER.info("Заказ удален!");
            }
        }
    }

    private static LocalDate inputCorrectData() {
        LocalDate date = null;
        while (date == null) {
            try {
                System.out.print("Введите дату в формате yyyy-MM-dd: ");
                String dataStr = scanner.next();
                date = LocalDate.parse(dataStr);
            } catch (DateTimeParseException exception) {
                LOGGER.warn("Некорректный формат даты. Пожалуйста, используйте формат yyyy-MM-dd.");
            }
        }
        return date;
    }


    private static void customerManager() {
        boolean exit = false;
        while (!exit) {
            System.out.println("*********************************");
            System.out.println("Выберите действие:");
            System.out.println("0. Exit");
            System.out.println("1. Добавить информацию о клиенте");
            System.out.println("2. Посмотреть информацию о всех клиентах");
            System.out.println("3. Просмотр истроии покупок клиента");

            System.out.println("**********************************");
            System.out.print("Введите Ваш выбор: ");
            checkValueInt();

            int choise = scanner.nextInt();


            switch (choise) {
                case 0: {
                    exit = true;
                    break;
                }
                case 1: {
                    System.out.println("Вы выбрали добавление информации о клиенте.");
                    String customerName = CustomerManager.addNewCustomer();
                    LOGGER.info("Клиент " + customerName + " был успешно добавлен");
                    break;
                }
                case 2: {
                    System.out.println("Вы выбрали показать всех клиентов");
                    CustomerManager.printAllCustomers();
                    break;
                }
                case 3: {
                    System.out.println("Вы выбрали Посмотреть истории покупок клиента.");
                    viewPurchaseHistory();
                    break;
                }

                default:
                    System.out.println("Введите число от 0 до " + 3);
                    break;

            }
        }
    }

    private static void viewPurchaseHistory() {
        scanner.nextLine();
        System.out.println("Введите имя клиента");
        String customerName = scanner.nextLine();
        Customer customer = CustomerManager.foundCustomerByName(customerName);
        if (customer != null) {
            customer.showOrderHistory();
        } else {
            System.out.println("Клиент не найден!");
        }

    }


    private static void productManager() {
        boolean exit = false;
        while (!exit) {
            System.out.println("*********************************");
            System.out.println("Выберите действие:");
            System.out.println("0. Exit");
            System.out.println("1. Добавить новый товар");
            System.out.println("2. Обновить количество товара на складе.");
            System.out.println("3. Просмотр всех товаров в магазине");
            System.out.println("4. Удалить товар по id");

            System.out.println("**********************************");
            System.out.print("Введите Ваш выбор: ");
            checkValueInt();

            int choise = scanner.nextInt();


            switch (choise) {
                case 0: {
                    exit = true;
                    break;
                }
                case 1: {
                    System.out.println("Вы выбрали добавление нового товара.");
                    addNewProduct();
                    break;
                }
                case 2: {
                    System.out.println("Вы выбрали Обновить количество товара на складе.");
                    updateStockQuantity();
                    break;
                }
                case 3: {
                    System.out.println("Вы выбрали просмотр всех товаров в магазине");
                    showAllProducts();
                    break;
                }
                case 4: {
                    System.out.println("Вы выбрали удалить товар по id");
                    removeProduct();
                }
                default:
                    System.out.println("Введите число от 0 до " + 3);
                    break;

            }
        }

    }

    private static void removeProduct() {
        System.out.print("Введите id товара: ");
        checkValueInt();
        int productId = scanner.nextInt();
        if (Catalog.checkId(productId)) {
            Catalog.removeProductById(productId);

        } else {
            LOGGER.error("Ошибка! Товара с таким id " + productId + " нет на складе!");
        }
    }

    private static void updateStockQuantity() {

        System.out.print("Введите id товара: ");
        checkValueInt();
        int productId = scanner.nextInt();
        if (Catalog.checkId(productId)) {
            int currentStockQuantit = Catalog.getStockQuantityByProductId(productId);
            System.out.println("Актуальное колличество на складе: " + currentStockQuantit);
            System.out.println("Введите новое количество на складе: ");
            checkValueInt();
            int newQuantity = scanner.nextInt();
            Catalog.updateStockQuantityByProductId(productId, newQuantity);
        } else {
            LOGGER.error("Ошибка! Товара с таким id " + productId + " нет на складе!");
        }
    }

    private static void showAllProducts() {
        System.out.println("*********************************");
        System.out.println("Список всех Товаров по категориям");
        Catalog.showAllProducts();
    }

    private static void addNewProduct() {
        scanner.nextLine();
        System.out.print("Введите категорию товара: ");
        String category = scanner.nextLine();

        System.out.print("Введите id товара: ");
        checkValueInt();
        int productId = scanner.nextInt();
        boolean isIdExist = Catalog.checkId(productId);
        if (!isIdExist) {
            scanner.nextLine();
            System.out.print("Введите название товара: ");
            String productName = scanner.nextLine();
            System.out.println("Введите описание товара");
            String productDesc = scanner.nextLine();
            System.out.println("Введите цену товара в центах!");
            int price = 0;
            while (price <= 0) {
                checkValueInt();
                price = scanner.nextInt();
                if (price <= 0) {
                    System.out.println("Введите цену > 0!");
                }
            }
            System.out.println("Введите количество на складе");
            checkValueInt();
            int quantity = scanner.nextInt();
            Product product = new Product(productId, productName, productDesc, price, quantity, category);
            Catalog.addNewProductInCatalog(category, productId, productName, productDesc, price, quantity);

        } else {
            LOGGER.error("Ошибка добавления объекта, Объект с таким id " + productId + " уже существует!");
        }

    }

    //-------------------------------------------------
    private static void loadingFromFiles() {
        String fileCatalog = "catalog.ser";
        String fileCustomer = "customer.ser";
        File catalog = new File(fileCatalog);
        File customer = new File(fileCustomer);
        if (catalog.exists() && catalog.length() != 0) {
            Catalog.deserializeCatalog();
            LOGGER.info("Информация о каталоге была успешно десериализована");
        }
        if (customer.exists() && customer.length() != 0) {
            CustomerManager.deserializeCustomers();
            LOGGER.info("Информация о покупателях была успешно десериализована");
        }
    }
    //-------------------------------------------------

    private static void checkValueInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Введите пожалуйста целое число");
            scanner.next();
        }
    }

    private static void checkValueDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Введите пожалуйста число!");
            scanner.next();
        }
    }
}

