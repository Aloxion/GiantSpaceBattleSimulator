package gsbs.common.data;

import gsbs.common.entities.Entity;

import java.util.ArrayList;

public class AsteroidNode {
    private int row;
    private int col;
    private ArrayList<Entity> entities;

    public AsteroidNode(int row, int col) {
        this.row = row;
        this.col = col;
        this.entities = new ArrayList<>();
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }
    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }
}
