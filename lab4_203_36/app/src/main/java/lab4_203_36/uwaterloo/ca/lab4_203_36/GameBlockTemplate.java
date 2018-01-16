package lab4_203_36.uwaterloo.ca.lab4_203_36;

import android.content.Context;
import android.widget.ImageView;


public abstract class GameBlockTemplate extends ImageView {

    public GameBlockTemplate(Context context){
        super(context);
    }

    public abstract void setDestination(Direction newDirection, int coords[]);
    public abstract void move();
}
