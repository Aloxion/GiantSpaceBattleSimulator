package gsbs.common.data;

public class Node {
    private int row;
    private int column;
    private boolean isBlocked;

    public Node(int row, int column, boolean isBlocked) {
        this.row = row;
        this.column = column;
        this.isBlocked = isBlocked;
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
