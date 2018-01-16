package lab4_203_36.uwaterloo.ca.lab4_203_36;

import android.util.Log;

public class SignatureFSM {
    private SignatureState determinedState;


    private final float THRES_A_RISE = 0.22f;
    private final float A_MAX = 2.65f;

    private final float THRES_A_FALL = -0.22f;
    private final float THRES_B_FALL = -0.22f;
    private final float B_MIN = -2.65f;

    private final float THRES_B_RISE = 0.22f;


    public SignatureFSM(){
        resetState();
    }

    public void update(float nextReading, float previousReading){
        float changeAccel = nextReading-previousReading;
        switch (determinedState){
            case WAIT:
                if (changeAccel >= THRES_A_RISE){
                    determinedState = SignatureState.RISE_A;
                }
                if (changeAccel <= THRES_B_FALL){
                    determinedState = SignatureState.FALL_B;
                }
                break;
            case RISE_A:
                if (nextReading >= A_MAX){
                    determinedState = SignatureState.FALL_A;
                } else if (nextReading <=0){
                    determinedState = SignatureState.TYPE_X;
                }
                break;
            case FALL_A:
                if (changeAccel <= THRES_A_FALL){
                    determinedState = SignatureState.TYPE_A;
                } else if (nextReading <= 0){
                    determinedState = SignatureState.TYPE_X;
                }
                break;
            case FALL_B:
                if (nextReading <= B_MIN){
                    determinedState = SignatureState.RISE_B;
                }
                else if (nextReading >= 0){
                    determinedState = SignatureState.TYPE_X;
                }
                break;
            case RISE_B:
                if (changeAccel >= THRES_B_RISE){
                    determinedState = SignatureState.TYPE_B;
                }
                else if(nextReading >= 0){
                    determinedState = SignatureState.TYPE_X;
                }
                break;
            default: //For when the FSM has determined the current signature
                break;
        }
    }


    public SignatureState getDeterminedState(){
        return determinedState;
    }

    public void resetState(){
        determinedState = SignatureState.WAIT;

    }
}
