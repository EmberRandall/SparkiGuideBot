package robotics.sparkiguidebot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Created by Emily on 11/16/2015.
 */
public class MazeSearch {
    private int[][] mazeList = new int[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0, 50},
            {1, 0, 2, 2, 2, 0, 3, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 2, 0},
            {2, 2, 1, 0, 0, 2, 0, 0, 2, 0},
            {0, 0, 0, 0, 0, 2, 0, 1, 0, 0},
            {0, 0, 2, 0, 0, 2, 0, 0, 0, 0},
            {0, 0, 2, 0, 0, 0, 0, 0, 2, 2},
            {0, 3, 2, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 2, 2, 0, 3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 2}};
    private double epsilon = 0.5;
    private double gamma = 0.9;
    private int startX = mazeList.length - 1;
    private int startY = 0;
    private int endX = 0;
    private int endY = mazeList[0].length - 1;
    private PriorityQueue<MazeNode> openList = new PriorityQueue<MazeNode>();
    private ArrayList<MazeNode> closedList = new ArrayList<MazeNode>();
    private MazeNode[][] maze = new MazeNode[mazeList.length][mazeList[0].length];

    public MazeSearch() {
    }

    private void createMaze() {
        for (int i = 0; i < mazeList.length; i++) {
            for (int j = 0; j < mazeList[i].length; j++) {
                maze[i][j] = new MazeNode(i, j, mazeList[i][j]);
            }
        }
    }

    private void iterate() {
        createMaze();
        double error = Double.POSITIVE_INFINITY;
        while (error > epsilon * (1 - gamma) / gamma) {
            double max_err = 0.0;
            for (int i = 0; i < maze.length; i++) {
                for (int j = maze[i].length - 1; j >= 0; j--) {
                    if (maze[i][j].getTyp() != 2) {
                        double oldUtil = maze[i][j].getUtility();
                        maze[i][j].setUtility(maze[i][j].getReward() + gamma * getMaxMove(i, j));
                        if (Math.abs(oldUtil - maze[i][j].getUtility()) > max_err) {
                            max_err = Math.abs(oldUtil - maze[i][j].getUtility());
                        }
                    }
                }
            }
            error = max_err;
        }
    }

    private double getMaxMove(int x, int y) {
        ArrayList<Double> moves = new ArrayList<>();
        moves.add(0.8 * getUtility(x - 1, y) + 0.1 * getUtility(x, y + 1) + 0.1 * getUtility(x, y - 1));
        moves.add(0.8 * getUtility(x + 1, y) + 0.1 * getUtility(x, y + 1) + 0.1 * getUtility(x, y - 1));
        moves.add(0.8 * getUtility(x, y + 1) + 0.1 * getUtility(x + 1, y) + 0.1 * getUtility(x - 1, y));
        moves.add(0.8 * getUtility(x, y - 1) + 0.1 * getUtility(x + 1, y) + 0.1 * getUtility(x - 1, y));
        return Collections.max(moves);
    }

    private double getUtility(int x, int y) {
        if (x >= 0 && x < maze.length && y >= 0 && y < maze[x].length){
            if (maze[x][y].getUtility() > Double.NEGATIVE_INFINITY){
                return maze[x][y].getUtility();
            }
        }
        return 0.0;
    }

    private ArrayList<MazeNode> getAdjacent(MazeNode node) {
        ArrayList<MazeNode> nodes = new ArrayList<>();
        if (node.getX() > 0) {
            nodes.add(maze[node.getX() - 1][node.getY()]);
        }
        if (node.getY() > 0) {
            nodes.add(maze[node.getX()][node.getY() - 1]);
        }
        if (node.getX() + 1 < maze.length) {
            nodes.add(maze[node.getX() + 1][node.getY()]);
        }
        if (node.getY() + 1 < maze[node.getX()].length) {
            nodes.add(maze[node.getX()][node.getY() + 1]);
        }
        return nodes;
    }

    private ArrayList<MazeNode> getPath(MazeNode node) {
        ArrayList<MazeNode> path = new ArrayList<>();
        while (node.getX() != startX || node.getY() != startY) {
            path.add(node);
            node = node.getParent();
        }
        path.add(node);
        Collections.reverse(path);
        return path;
    }

    public ArrayList<MazeNode> search() {
        iterate();
        openList.add(maze[startX][startY]);
        while (!openList.isEmpty()) {
            MazeNode node = openList.poll();
            closedList.add(node);
            if (node.getX() == endX && node.getY() == endY) {
                return getPath(node);
            }
            ArrayList<MazeNode> adjNodes = getAdjacent(node);
            for (MazeNode n : adjNodes) {
                if (!closedList.contains(n) && !openList.contains(n)) {
                    openList.add(n);
                    n.setParent(node);
                }
            }
        }
        return null;
    }

    public String getAdjacentWalls(int x, int y) {
        String walls = "";
        if (x - 1 >= 0 && mazeList[x - 1][y] == 2) {
            walls += "bn00";
        }
        if (x + 1 < mazeList.length && mazeList[x + 1][y] == 2) {
            walls += "bs00";
        }
        if (y - 1 >= 0 && mazeList[x][y - 1] == 2) {
            walls += "bw00";
        }
        if (y + 1 < mazeList[x].length && mazeList[x][y + 1] == 2) {
            walls += "be00";
        }
        return walls;
    }
}
