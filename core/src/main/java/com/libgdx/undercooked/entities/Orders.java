package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.libgdx.undercooked.GameManager;
import java.util.ArrayList;

import static com.libgdx.undercooked.GameManager.score;
import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;

public class Orders {
    static ArrayList<FoodOrder> orderList;
    private int activeOrder = 0;
    public static int totalOrders = 0;
    private float timer = 0;
    public static boolean noOrders = false;

    public Orders() {
        orderList = new ArrayList<>();
        String selectedMap = getSelectedMap();
        switch (selectedMap) {
            case "Map1":
                totalOrders = 5;
                orderList.add(new FoodOrder(FoodType.meat_meal_onion, 5));
                orderList.add(new FoodOrder(FoodType.chopped_pickle, 1));
                orderList.add(new FoodOrder(FoodType.cooked_meat, 1));
                orderList.add(new FoodOrder(FoodType.cooked_fish, 1));
                orderList.add(new FoodOrder(FoodType.rice, .5f));
                break;
            case "Map2":
                totalOrders = 5;
                orderList.add(new FoodOrder(FoodType.sushi, 3));
                orderList.add(new FoodOrder(FoodType.sushi_tomato, 3));
                orderList.add(new FoodOrder(FoodType.sushi_pickle, 5));
                orderList.add(new FoodOrder(FoodType.rice, 0));
                orderList.add(new FoodOrder(FoodType.sushi, 1));
                break;
            case "Map3":
                totalOrders = 5;
                orderList.add(new FoodOrder(FoodType.rice, 3));
                orderList.add(new FoodOrder(FoodType.tomato_soup, 3));
                orderList.add(new FoodOrder(FoodType.cooked_meat, 5));
                orderList.add(new FoodOrder(FoodType.cooked_fish, 0));
                orderList.add(new FoodOrder(FoodType.chopped_pickle, 1));
                break;

            case "Map4":
                totalOrders = 5;
                orderList.add(new FoodOrder(FoodType.rice, 3));
                orderList.add(new FoodOrder(FoodType.tomato_soup, 3));
                orderList.add(new FoodOrder(FoodType.cooked_meat, 5));
                orderList.add(new FoodOrder(FoodType.cooked_fish, 0));
                orderList.add(new FoodOrder(FoodType.chopped_pickle, 1));
                break;

            case "Map5":
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

    public int getActiveOrder() {
        return activeOrder;
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
            case sushi:
                score += 7;
                break;
            case sushi_pickle:
                score += 14;
                break;
            case sushi_tomato:
                score += 12;
                break;
            case meat_meal_tomato:
            case fish_meal_tomato:
                score += 30;
                break;
            case fish_meal_onion:
            case meat_meal_onion:
                score += 31;
                break;
            case cooked_meat_tomato:
            case cooked_chopped_fish:
            case cooked_chopped_meat:
            case cooked_fish_onion:
                score+= 17;
                break;
            case cooked_fish_tomato:
            case cooked_meat_onion:
                score+=14;
            default:
                System.out.println("Unknown food type...");
                break;
        }
    }
    public void update(float deltaTime, EntityList entityList) {
        Gdx.app.log("Orders", " : " + totalOrders );
        if(Gdx.input.isKeyPressed(Input.Keys.F1)){
            totalOrders=0;
            noOrders=true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.F2)){
            GameManager.timesUp=true;
        }

        try{
            for (int i = 0; i < activeOrder; i++) {
                if (orderList.get(i).isActive()) {
                    orderList.get(i).patience -= deltaTime;
                    if (orderList.get(i).patience < 0 && !orderList.get(i).patienceDone) {
                        orderList.get(i).patienceDone = true;
                        //entityList.addGhost();
                    }
                }
            }
            if (timer > 0) {
                timer -= deltaTime;
            } else if (activeOrder < totalOrders && activeOrder >= 0) {
                timer = orderList.get(activeOrder).timer;
                activeOrder++;
                GameManager.setCheckEntry(true);
            }
        }catch (IndexOutOfBoundsException e){
            totalOrders = 0;
            noOrders = true;
        }

    }

    public static void freeOrderList() {
        orderList.clear();
    }

    public static class FoodOrder {
        private final FoodType foodType;
        final float timer;
        float patience = 30f;
        boolean patienceDone = false;
        private boolean active = true;

        public FoodOrder(FoodType foodType, float timer) {
            this.foodType = foodType;
            this.timer = timer;
        }

        public FoodType getFoodType() {
            return foodType;
        }

        public boolean isActive(){
            return this.active;
        }

        public void setInactive(){
            this.active = false;
        }
    }
}
