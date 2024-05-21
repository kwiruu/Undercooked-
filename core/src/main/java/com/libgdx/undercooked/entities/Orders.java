package com.libgdx.undercooked.entities;

import java.util.ArrayList;

import static com.libgdx.undercooked.GameManager.score;
import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;

public class Orders {

    private static ArrayList<FoodOrder> orderList;
    private int activeOrder = 0;
    public static int totalOrders;
    private float timer = 0;

    public Orders() {
        orderList = new ArrayList<>();
        String selectedMap = getSelectedMap();
        switch (selectedMap) {
            case "Map1":
                totalOrders = 5;
                orderList.add(new FoodOrder(FoodType.rice, 0));
                orderList.add(new FoodOrder(FoodType.chopped_pickle, 3));
                orderList.add(new FoodOrder(FoodType.cooked_meat, 3));
                orderList.add(new FoodOrder(FoodType.cooked_fish, 3));
                orderList.add(new FoodOrder(FoodType.rice, .5f));
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
        for (int i = 0; i < activeOrder; i++) {
            orderList.get(i).patience -= deltaTime;
        }
        if (timer > 0) {
            timer -= deltaTime;
        } else if (activeOrder < totalOrders) {
            timer = orderList.get(activeOrder).getTimer();
            activeOrder++;
        }
    }
    public static void freeOrderList() {
        orderList.clear();
    }

    public FoodOrder getCurrentOrder() {
        if (activeOrder < orderList.size()) {
            return orderList.get(activeOrder);
        }
        return null;
    }

    public void nextOrder() {
        activeOrder++;
        if (activeOrder >= orderList.size()) {
            activeOrder = 0; // Reset to the first order if we've reached the end
        }
    }

    public void removeInactiveOrders() {
        orderList.removeIf(order -> !order.getActive());
    }
    public ArrayList<FoodOrder> getActiveFoods() {
        ArrayList<FoodOrder> activeFoods = new ArrayList<>();
        for (FoodOrder order : orderList) {
            if (order.getActive()) {
                activeFoods.add(order);
            }
        }
        return activeFoods;
    }


    public static class FoodOrder {
        private final FoodType foodType;
        private final float timer;
        float patience = 100;
        private boolean active = true;

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

        public boolean getActive(){
            return this.active;
        }

        void setInactive(){
            this.active = false;
            totalOrders--;
        }
    }
}
