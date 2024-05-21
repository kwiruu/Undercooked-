package com.libgdx.undercooked.entities;

import java.util.ArrayList;

import static com.libgdx.undercooked.GameManager.score;
import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;

public class Orders {

    private static ArrayList<FoodOrder> orderList;
    private static ArrayList<FoodOrder> activeOrderList;
    private static int activeOrderIndex = 0;
    public static int totalOrders;
    private float timer = 0;

    public Orders() {
        orderList = new ArrayList<>();
        activeOrderList = new ArrayList<>();
        String selectedMap = getSelectedMap();
        switch (selectedMap) {
            case "Map1":
                totalOrders = 5;
                orderList.add(new FoodOrder(FoodType.rice, 0));
                orderList.add(new FoodOrder(FoodType.chopped_pickle, 3));
                orderList.add(new FoodOrder(FoodType.cooked_meat, 3));
                orderList.add(new FoodOrder(FoodType.cooked_fish, 3));
                orderList.add(new FoodOrder(FoodType.rice, 0.5f));
                break;
            case "Map2":
                totalOrders = 5;
                orderList.add(new FoodOrder(FoodType.rice, 3));
                orderList.add(new FoodOrder(FoodType.tomato_soup, 3));
                orderList.add(new FoodOrder(FoodType.cooked_meat, 5));
                orderList.add(new FoodOrder(FoodType.cooked_fish, 0));
                orderList.add(new FoodOrder(FoodType.chopped_pickle, 1));
                break;
        }
    }

    public ArrayList<FoodOrder> getOrderList() {
        return orderList;
    }

    public ArrayList<FoodOrder> getActiveOrderList() {
        return new ArrayList<>(activeOrderList);
    }

    public void rewardPoints(FoodType foodType) {
        switch (foodType) {
            case tomato_soup:
            case rice:
                score += 10;
                break;
            case cooked_meat:
                score += 20;
                break;
            case cooked_fish:
                score += 15;
                break;
            case pickle:
                score += 1;
                break;
            case chopped_pickle:
                score += 2;
                break;
            case meat_meal:
            case fish_meal:
                score += 30;
                break;
            case struggle_meal:
                score += 7;
                break;
            default:
                System.out.println("Unknown food type...");
                break;
        }
    }

    public void update(float deltaTime) {
        timer += deltaTime;

        // Activate orders based on the elapsed timer
        for (int i = 0; i < orderList.size(); i++) {
            FoodOrder order = orderList.get(i);
            if (timer >= order.getTimer() && !order.getActive()) {
                System.out.println(order.getTimer());
                order.activate();
                activeOrderList.add(order);
                activeOrderIndex++;
            }
        }

        // Update patience for active orders
        for (FoodOrder order : activeOrderList) {
            order.updatePatience(deltaTime);
        }

        // Remove inactive orders from activeOrderList
        activeOrderList.removeIf(order -> !order.getActive());
    }

    public static void freeOrderList() {
        orderList.clear();
        activeOrderList.clear();
        activeOrderIndex = 0;
    }

    public void removeInactiveOrders() {
        orderList.removeIf(order -> !order.getActive());
    }

    public static class FoodOrder {
        private final FoodType foodType;
        private final float timer;
        private final float maxPatience = 100;
        private float patience = maxPatience;
        private boolean active = false;

        public FoodOrder(FoodType foodType, float timer) {
            this.foodType = foodType;
            this.timer = timer;
        }

        public FoodType getFoodType() {
            return foodType;
        }

        public float getTimer() {
            return timer;
        }

        public void activate() {
            this.active = true;
        }

        public boolean getActive() {
            return this.active;
        }

        void setInactive() {
            this.active = false;
            totalOrders--;
        }

        public void updatePatience(float deltaTime) {
            if (active && patience > 0) {
                patience -= deltaTime;
                if (patience <= 0) {
                    setInactive();
                }
            }
        }
    }
}
