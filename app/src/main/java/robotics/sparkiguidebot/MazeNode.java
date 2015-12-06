package robotics.sparkiguidebot;

import android.util.Log;

/**
 * Created by Emily on 11/11/2015.
 */
public class MazeNode implements Comparable<MazeNode> {
    private int x, y;
    private Type typ;
    private double utility, reward;
    private MazeNode parent = null;

    protected enum Type {
        BLANK (0),
        STOP (1),
        WALL (2),
        GREEN (3),
        END (50);

        private final int value;
        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type getType(int value) {
            switch (value) {
                case 50:
                    return END;
                default:
                    return Type.values()[value];
            }
        }
    }

    public MazeNode(int x, int y, int typ) {
        this.x = x;
        this.y = y;
        this.typ = Type.getType(typ);
        Log.d("MazeNode", this.typ.toString());
        this.utility = typ != 2 ? 0 : Double.NEGATIVE_INFINITY;
        switch(this.typ) {
            case STOP: this.reward = -3;
                break;
            case WALL: this.reward = Double.NEGATIVE_INFINITY;
                break;
            case GREEN: this.reward = 2;
                break;
            case END: this.reward = 50;
                break;
            default: this.reward = 0;
                break;
        }
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public Type getTyp() {
        return this.typ;
    }
    public double getUtility() {
        return this.utility;
    }
    public void setUtility(double u) {
        this.utility = u;
    }
    public double getReward() {
        return this.reward;
    }
    public MazeNode getParent() {
        return this.parent;
    }
    public void setParent(MazeNode node) {
        this.parent = node;
    }
    public int compareTo(MazeNode other) {
        return Double.compare(other.getUtility(), this.getUtility());
    }
}
