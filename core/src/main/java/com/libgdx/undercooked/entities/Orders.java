package com.libgdx.undercooked.entities;

import com.libgdx.undercooked.entities.PlayerManager.Player;

import java.util.ArrayList;

import static com.libgdx.undercooked.GameManager.score;
import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;

public class Orders {

    private ArrayList<FoodOrder> orderList;
    private int activeOrder = 0;

    public Orders() {
        orderList = new ArrayList<>();
        String selectedMap = getSelectedMap();
        switch (selectedMap) {
            case "Map1":
                orderList.add(new FoodOrder(FoodType.rice, 0));
                orderList.add(new FoodOrder(FoodType.rice, 0));
                orderList.add(new FoodOrder(FoodType.rice, 0));
                orderList.add(new FoodOrder(FoodType.rice, 0));
                orderList.add(new FoodOrder(FoodType.rice, 1));
                break;
            case "Map2":
                orderList.add(new FoodOrder(FoodType.rice, 5));
                orderList.add(new FoodOrder(FoodType.tomato_soup, 5));
                orderList.add(new FoodOrder(FoodType.cooked_meat, 5));
                orderList.add(new FoodOrder(FoodType.cooked_fish, 5));
                orderList.add(new FoodOrder(FoodType.chopped_pickle, 5));
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

    public static class FoodOrder {
        private final FoodType foodType;
        private final int timer;
        private boolean active = true;

        public FoodOrder(FoodType foodType, int timer) {
            this.foodType = foodType;
            this.timer = timer;
        }

        public FoodType getFoodType() {
            return foodType;
        }

        public int getTimer() {
            return timer;
        }

        public boolean getActive(){
            return this.active;
        }

        void setInactive(){
            this.active = false;
        }

    }
}
