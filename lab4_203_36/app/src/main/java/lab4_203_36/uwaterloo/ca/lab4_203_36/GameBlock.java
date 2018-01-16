package lab4_203_36.uwaterloo.ca.lab4_203_36;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

public class GameBlock extends GameBlockTemplate {
    private final float IMAGE_FLOAT = 0.65f;
    private int coordX;
    private int coordY;
    private Direction direction = Direction.NO_MOVEMENT;
    private LinkedList<GameBlock> list;

    private int gridCoordinates [] = new int[2];

    private int targetX;
    private int targetY;

    private final int A = 8;
    private int V = 3;

    //Edge coordinates with Nexus 6P (shared across x and y)
    // starting from top right hand, going clockwise (x, y) =(min, min), (max, min), (max, max), (min, max)
    private final int MIN_COORD = -60;
    private final int MAX_COORD = 750;

    private final int GRID_WIDTH = 270;

    private int blockNumber;
    private TextView tv;
    private Random rand = new Random();

    private final int X_OFFSET = 150;
    private final int Y_OFFSET = 100;

    private RelativeLayout layout;
    private boolean toDelete = false;
    private boolean toMerge = false;


    //Public constructor, takes in int paramters 0-3 to create a block in the respective index of the 4x4 grid
    public GameBlock(RelativeLayout rl, Context myContext, int gridX, int gridY, LinkedList<GameBlock> GBList){
        super(myContext);
        coordX = gridX*GRID_WIDTH + MIN_COORD;
        coordY = gridY*GRID_WIDTH + MIN_COORD;
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_FLOAT);
        this.setScaleY(IMAGE_FLOAT);

        this.setX(coordX);
        this.setY(coordY);

        targetX = coordX;
        targetY = coordY;

        blockNumber = 2*(rand.nextInt(2)+1);
        tv = new TextView(myContext);
        tv.setText(Integer.toString(blockNumber));
        tv.setX(coordX+X_OFFSET);
        tv.setY(coordY+Y_OFFSET);
        tv.bringToFront();
        tv.setTextSize(48);
        tv.setTextColor(Color.BLACK);

        rl.addView(this);
        rl.addView(tv);

        gridCoordinates[0] = gridX;
        gridCoordinates[1] = gridY;

        this.layout = rl;
        list = GBList;
    }

    //Sets destination of the block in terms of pixels
    public void setDestination (Direction newDirection, int[] coords){
        direction = newDirection;
        V = 5;
        targetX = coords[0]*GRID_WIDTH + MIN_COORD;
        targetY = coords[1]*GRID_WIDTH + MIN_COORD;
    }

    public int[] getCoords(){
        return gridCoordinates;
    }

    //Recalculated the gridCoordinates according to targetX and targetY values.
    public void refresh (){
        gridCoordinates[0] = (targetX - MIN_COORD)/GRID_WIDTH;
        gridCoordinates[1] = (targetY - MIN_COORD)/GRID_WIDTH;
    }

    public Direction getDirection (){
        return direction;
    }

    //Motion of block with the application of newtonian acceleration
    //Moves the blocks and textView towards the target location, when reached, sets direction of block to NO_MOVEMENT
    public void move(){
        refresh();
        switch (direction) {
            case UP:
                coordY -= V;
                if (coordY < targetY){
                    coordY = targetY;
                    direction = Direction.NO_MOVEMENT;
                }
                break;
            case DOWN:
                coordY += V;
                if (coordY > targetY){
                    coordY = targetY;
                    direction = Direction.NO_MOVEMENT;
                }
                break;
            case LEFT:
                coordX -= V;
                if (coordX < targetX) {
                    coordX = targetX;
                    direction = Direction.NO_MOVEMENT;
                }
                break;
            case RIGHT:
                coordX += V;
                if (coordX > targetX){
                    coordX = targetX;
                    direction = Direction.NO_MOVEMENT;
                }
                break;
            case NO_MOVEMENT:
                break;
            default:
                break;
        }
        V += A;
        this.setX(coordX);
        tv.setX(coordX+X_OFFSET);
        this.setY(coordY);
        tv.setY(coordY+Y_OFFSET);
    }

    //Getter function for blockNumber
    public int getBlockNumber() { return this.blockNumber; }

    //Function that doubles blockNumber and updates textView when block is merged
    public void doubleBlockNumber() {
        blockNumber *= 2;
        tv.setText(Integer.toString(blockNumber));
    }

    //Destructor function that removes the block from the relative layout
    public void destroy() {
        layout.removeView(this);
        layout.removeView(tv);
    }

    //Sets deletion flag
    public void markForDeletion() {
        toDelete = true;
    }

    //Sets toMerge flag so there are no duplicate (or triple) merges
    public void markAsMerged(){ toMerge = true;}

    //Rests toMerge flag after merge completed
    public void resetMerge(){ toMerge = false;}

    //Getter function for toMerge
    public boolean willMerge() {return toMerge;}

    //Getter function for deletion flag
    public boolean willDelete () {return toDelete;}
}