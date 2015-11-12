package robotics.sparkiguidebot;

/**
 * Created by Emily on 11/11/2015.
 */
public class MazeNode implements Comparable<MazeNode> {
    private int x, y, typ;
    private double utility, reward;
    private MazeNode parent = null;

    public MazeNode(int x, int y, int typ) {
        this.x = x;
        this.y = y;
        this.typ = typ;
        this.utility = typ != 2 ? 0 : Double.NEGATIVE_INFINITY;
        switch(typ) {
            case 1: this.reward = -3;
                break;
            case 2: this.reward = Double.NEGATIVE_INFINITY;
                break;
            case 3: this.reward = 2;
                break;
            case 50: this.reward = 50;
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
    public int getTyp() {
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
