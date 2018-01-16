package lab4_203_36.uwaterloo.ca.lab4_203_36;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

public class GameLoopTask extends TimerTask {
    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myLayout;
    private Direction direction = Direction.NO_MOVEMENT;
    private Random rand = new Random();
    private boolean motionComplete = true;

    private final int GRID_WIDTH = 4; //y
    private final int GRID_LENGTH = 4; //x

    private LinkedList<GameBlock> GBList = new LinkedList<>();
    private boolean grid[][] = new boolean[4][4];
    private int[] rowIndex, columnIndex;
    private int numBlocks;
    private boolean gridFilled = false;

    private boolean setMotion = false;
    private boolean gameEnd = false;

    private TextView gameMessage;
    //Default constructor
    public GameLoopTask(Activity activity, Context context, RelativeLayout layout){
        myActivity = activity;
        myContext = context;
        myLayout = layout;
        createBlock();

        gameMessage = new TextView(context);
        gameMessage.setTextSize(72);
        gameMessage.setTextColor(Color.WHITE);
        layout.addView(gameMessage);
    }

    public void run(){
        this.myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (setMotion == false) {
                            motionComplete = true;
                            for (GameBlock myBlock : GBList) {
                                motionComplete = (myBlock.getDirection() == Direction.NO_MOVEMENT && motionComplete) ? true : false;
                                myBlock.move();
                            }
                            if (motionComplete && direction != Direction.NO_MOVEMENT) {
                                direction = Direction.NO_MOVEMENT;
                                updateGrid();
                                if (gridFilled == false) { // only creates new block if the grid is not already full
                                    createBlock();
                                }
                            }

                        }
                    }
                }
        );
    }

    //Creates random block in an empty space (through random number generation
    //Adds to GBList
    private void createBlock(){
        int newCoordX, newCoordY;

        do {
            newCoordX = rand.nextInt(4);
            newCoordY = rand.nextInt(4);
        } while (isOccupied(newCoordX, newCoordY) && gridFilled == false);

        GameBlock newBlock = new GameBlock(myLayout, myContext, newCoordX, newCoordY, GBList);
        GBList.add(newBlock);
    }

    //Refreshes the 4x4 boolean array for which grid spaces are occupied and grid filled
    //Completes block deletion and merge
    //Checks end game conditions (win and loss)
    private void updateGrid (){
        grid = new boolean[4][4];

        LinkedList<GameBlock> removeList = new LinkedList<>();
        for (GameBlock myBlock : GBList){
            if (myBlock.willDelete()){
                removeList.add(myBlock);
            }
            else{
                grid[myBlock.getCoords()[0]][myBlock.getCoords()[1]] = true;
            }
            if (myBlock.willMerge()){
                myBlock.doubleBlockNumber();
            }
            if (myBlock.getBlockNumber() == 256){
                Log.d ("End Game: ", "You Win!");
                gameEnd = true;
            }
        }

        for (GameBlock myBlock : removeList){
            myBlock.destroy();
            GBList.remove(myBlock);
            Log.d ("Delete block", "Deleted");
        }

        gridFilled = true;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                gridFilled = (gridFilled && grid[i][j]);
            }
        }
        if (gridFilled) {
            checkLoss();
        }
    }

    //Function that determines whether there are still mergeable blocks within the grid if grid is filled
    //Will cause null pointer exception if grid is not filled.
    private void checkLoss(){
        GameBlock[][] blockList = new GameBlock[4][4];
        boolean canMerge = false;

        for (GameBlock myBlock: GBList){
            blockList[myBlock.getCoords()[0]][myBlock.getCoords()[1]] = myBlock;
            myBlock.resetMerge();
        }

        for (int x = 0; x <3; x++){
            for (int y = 0; y < 3; y++){
                canMerge = (blockList[x][y].getBlockNumber() == blockList[x+1][y].getBlockNumber() || canMerge == true)? true: false;
                canMerge = (blockList[x][y].getBlockNumber() == blockList[x][y+1].getBlockNumber() || canMerge == true)? true: false;
            }
        }
        if (canMerge == false) {
            Log.d ("End Game: ", "You Lose!");
            gameEnd = true;
        }

    }

    //Returns true if the grid space is occupied, false otherwise
    private boolean isOccupied (int coordX, int coordY){
        for (GameBlock myBlock : GBList){
            if(myBlock.getCoords()[0] == coordX && myBlock.getCoords()[1] == coordY){
                return true;
            }
        }
        return false;
    }

    //Finds the GameBlock at a specified index, returns null if none found.
    private GameBlock findBlock (int coordX, int coordY){
        for (GameBlock myBlock : GBList){
            if(myBlock.getCoords()[0] == coordX && myBlock.getCoords()[1] == coordY){
                return myBlock;
            }
        }
        return null;
    }

    //Block look-ahead algorithm that detects number of blocks in front of current block, returns 2-D array for new grid location of block
    //Ignores all blocks with the flag toDelete == true
    private int[] findDestination (GameBlock block){
        int[] gridDestination = new int[2];
        int blockCount = 0;

        switch(direction){
            case LEFT:
                for (GameBlock myBlock : GBList){
                    if (myBlock.getCoords()[1] == block.getCoords()[1] && myBlock.getCoords()[0] < block.getCoords()[0] && myBlock.willDelete()== false) {
                        blockCount++;
                    }
                }
                gridDestination[0] = blockCount;
                gridDestination[1] = block.getCoords()[1];
                break;

            case RIGHT:
                for (GameBlock myBlock : GBList){
                    if (myBlock.getCoords()[1] == block.getCoords()[1] && myBlock.getCoords()[0] > block.getCoords()[0] && myBlock.willDelete()== false) {
                        blockCount++;
                    }
                }
                gridDestination[0] = 3 - blockCount;
                gridDestination[1] = block.getCoords()[1];
                break;

            case UP:
                for (GameBlock myBlock : GBList){
                    if (myBlock.getCoords()[0] == block.getCoords()[0] && myBlock.getCoords()[1] < block.getCoords()[1] && myBlock.willDelete()== false) {
                        blockCount++;
                    }
                }
                gridDestination[0] = block.getCoords()[0];
                gridDestination[1] = blockCount;
                break;

            case DOWN:
                for (GameBlock myBlock : GBList){
                    if (myBlock.getCoords()[0] == block.getCoords()[0] &&  myBlock.getCoords()[1] > block.getCoords()[1] && myBlock.willDelete()== false) {
                        blockCount++;
                    }
                }
                gridDestination[0] = block.getCoords()[0];
                gridDestination[1] = 3- blockCount;
                break;

            default:
                gridDestination = block.getCoords();
                break;
        }
        return gridDestination;
    }

    //Merge algorithm that maps each gameBlock in the list into 4x4 array
    //Checks blocks next to each other for possible merges
    //Function only sets flags within each GameBlock, actual merging occurs in updateGrid() and findDestination()
    private void merge (){
        GameBlock[][] blockList = new GameBlock[4][4];
        for (GameBlock myBlock: GBList){
            blockList[myBlock.getCoords()[0]][myBlock.getCoords()[1]] = myBlock;
            myBlock.resetMerge();
        }

        switch(direction){
            case LEFT:
                for (int y = 0; y < 4; y++){
                    for (int x = 0; x < 3; x++){
                        if (blockList[x][y] != null && blockList[x+1][y] != null){
                            if (blockList[x][y].willMerge() == false && blockList[x][y].getBlockNumber() == blockList[x+1][y].getBlockNumber()){
                                blockList[x][y].markForDeletion();
                                blockList[x+1][y].markAsMerged();
                                Log.d ("Delete marking", "marked");
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                break;
            case RIGHT:
                for (int y = 0; y < 4; y++){
                    for (int x = 3; x > 0; x--){
                        if (blockList[x][y] != null && blockList[x-1][y] != null){
                            if (blockList[x][y].willMerge()== false && blockList[x][y].getBlockNumber() == blockList[x-1][y].getBlockNumber()){
                                blockList[x][y].markForDeletion();
                                blockList[x-1][y].markAsMerged();
                                Log.d ("Delete marking", "marked");
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                break;
            case UP:
                for (int x = 0; x < 4; x++){
                    for (int y = 0; y < 3; y++){
                        if (blockList[x][y] != null && blockList[x][y+1] != null){
                            if (blockList[x][y].willMerge() == false && blockList[x][y].getBlockNumber() == blockList[x][y+1].getBlockNumber()){
                                blockList[x][y].markForDeletion();
                                blockList[x][y+1].markAsMerged();
                                Log.d ("Delete marking", "marked");
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                break;
            case DOWN:
                for (int x = 0; x< 4; x++){
                    for (int y = 3; y> 0; y--){
                        if (blockList[x][y] != null && blockList[x][y-1] != null){
                            if (blockList[x][y].willMerge() == false && blockList[x][y].getBlockNumber() == blockList[x][y-1].getBlockNumber()){
                                blockList[x][y].markForDeletion();
                                blockList[x][y-1].markAsMerged();
                                Log.d ("Delete marking", "marked");
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    //Sets direction of motion of block, given that gameLoopTask is not currently undergoing any motion.
    //First sets destination of blocks, then refreshes the coordinates of the block (to prevent errors in the block lookahead algorithm)
    public void setDirection(Direction newDirection){
        if (direction == Direction.NO_MOVEMENT && gameEnd == false){
            setMotion = true;
            this.direction = newDirection;
            for (GameBlock myBlock: GBList){
                myBlock.setDestination(newDirection, findDestination(myBlock));
            }
            for (GameBlock myBlock: GBList){
                myBlock.refresh();
            }
            merge();
            for (GameBlock myBlock: GBList){
                myBlock.setDestination(newDirection, findDestination(myBlock));
            }
            Log.d ("setDirection", "direction set");
            setMotion = false;
        }
    }
}