package controller;

import android.app.Activity;
import android.widget.Button;

import com.example.marshal.animationlab.R;

import model.AnimationModel;

/**
 * This class is used to define "LEFT", "RIGHT" and "UP" buttons and setup the relationship with AnimationModel.
 *
 * @author Shuai Yuan
 * @version 1.1
 * @since 2016-05-20
 */
public class ControlButtons {

    private Button btnLeft, btnRight, btnUp;
    private AnimationModel animModel;

    /**
     * This constructor method is used to instantiate "LEFT", "RIGHT" and "UP" buttons for an activity
     * and setup the relationship with an AnimationModel.
     * @param activity This is an Activity parameter to which the buttons will be instantiated.
     * @param model This is an AnimationModel parameter to which the buttons will be related.
     */
    public ControlButtons(Activity activity, AnimationModel model) {
        animModel = model;

        btnLeft = (Button) activity.findViewById(R.id.button_left);
        btnRight = (Button) activity.findViewById(R.id.button_right);
        btnUp = (Button) activity.findViewById(R.id.button_up);
    }
}
