package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

record Order(long orderId, String item, int qty) {
};

public class ShoeWarehouse {

    private List<Order> shippingItems;

    private final ExecutorService fulfillmentService;

    public final static String[] PRODUCT_LIST =
            {"Running Shoes", "Sandals", "Boots", "Slippers", "High Tops"};

    public ShoeWarehouse() {
        this.shippingItems = new ArrayList<>();
        fulfillmentService = Executors.newFixedThreadPool(3);
    }

    public void shutDown() {
        fulfillmentService.shutdown();
    }

    public synchronized void receiveOrder(Order item) {
        while (shippingItems.size() > 20) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        shippingItems.add(item);
        System.out.println(Thread.currentThread().getName() + " Incoming: ");
        fulfillmentService.submit(this::fulfillOrder);
        notifyAll();
    }

    public synchronized Order fulfillOrder() {

        while (shippingItems.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Order item = shippingItems.remove(0);
        System.out.println(Thread.currentThread().getName() + " Fulfilled");
        notifyAll();
        return item;
    }
}
