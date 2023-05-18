package gsbs.common.data;

public class Node {
    private int row;
    private int column;
    private boolean isBlocked;

    private float weight = 0;

    public Node(int row, int column, boolean isBlocked) {
        this.row = row;
        this.column = column;
        this.isBlocked = isBlocked;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
