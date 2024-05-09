//package com.libgdx.undercooked.screen;
//
//import com.badlogic.gdx.physics.box2d.World;
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.utils.Disposable;
//import com.libgdx.undercooked.Main;
//import com.libgdx.undercooked.MapManager;
//import com.libgdx.undercooked.PlayerManager;
//import com.libgdx.undercooked.entities.Stove;
//
//public class GameManager implements Disposable {
//
//    private final Main context;
//    private final World world;
//    private MapManager mapManager;
//    private PlayerManager playerManager;
//    private Array<Stove> stoves;
//
//    public GameManager(Main context, World world) {
//        this.context = context;
//        this.world = world;
//        initialize();
//    }
//
//    private void initialize() {
//        playerManager = new PlayerManager(world);
//        playerManager.run();
//        mapManager = new MapManager(world);
//        stoves = mapManager.getStoves();
//    }
//
//    public PlayerManager getPlayerManager() {
//        return playerManager;
//    }
//
//    public MapManager getMapManager() {
//        return mapManager;
//    }
//
//    public Array<Stove> getStoves() {
//        return stoves;
//    }
//
//    @Override
//    public void dispose() {
//        playerManager.dispose();
//        mapManager.dispose();
//        // Dispose other resources if needed
//    }
//}
