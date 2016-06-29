package com.example.marshal.animationlab;

import android.graphics.drawable.ClipDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import controller.ControlButtons;
import model.AnimationModel;
import view.AnimationView;

/**
 * <h2>MartianLander</h2>
 * The MartianLander program is a mars landing simulation game allowing user to land a spacecraft
 * on the surface of mars. Landing point should be a flat area of land. Users control the spacecraft
 * to move left or right by "LEFT" and "RIGHT" buttons, or slow down the spacecraft by "UP" button.
 * If landing at tilting area or landing too fast, the spacecraft will crash and explode.
 *
 * @author Shuai Yuan
 * @version 1.1
 * @since 2016-05-20
 * */

public class MainActivity extends AppCompatActivity {

    AnimationModel mModel;
    AnimationView mView;
    ControlButtons mControls;
    ImageView mFuel;
    ClipDrawable mClip;

    /**
     * This method is used to instantiate the SurfaceView, AnimationModel, layout and drawable objects
     * when the activity is created..
     * @param savedInstanceState This is the only parameter which will be passed automatically
     * when the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mModel = new AnimationModel(this);
        mView = (AnimationView) findViewById(R.id.view);
        mView.setModel(mModel);
        mControls = new ControlButtons(this, mModel);
        mFuel = (ImageView) findViewById(R.id.fuel);
        mClip = (ClipDrawable) mFuel.getResources().getDrawable(R.drawable.clip, null); //Instantiate the fuel bar with a ClipDrawable object.
        mClip.setLevel(10000);  //Fuel bar fully visible.
        mFuel.setBackground(mClip);
        mFuel.setImageResource(android.R.color.transparent);
    }

    /**
     * This method is used to stop the animation thread when this activity is paused..
     */
    @Override
    protected void onPause() {
        super.onPause();
        mView.stopAnimation();
    }

    /**
     * This method is used to start and continue the animation thread when this activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mView.startAnimation();
    }

    /**
     * This method is used to response the click event on the "LEFT" button and causes the spacecraft
     * to move right with specific speed. Meanwhile the fuel bar will show the change of fuel.
     * @param v This is the only parameter which will be passed automatically when user clicks the view
     *          which holds this event handler.
     */
    public void clickLeft(View v) {
        mModel.changeSpeedRight();
        if ((mModel.fuel >= 0) && mModel.bottomLeft && mModel.bottomRight) {
            mClip.setLevel(mClip.getLevel() - 1000);
        }
    }

    /**
     * This method is used to response the click event on the "RIGHT" button and causes the spacecraft
     * to move left with specific speed. Meanwhile the fuel bar will show the change of fuel.
     * @param v This is the only parameter which will be passed automatically when user clicks the view
     *          which holds this event handler.
     */
    public void clickRight(View v) {
        mModel.changeSpeedLeft();
        if ((mModel.fuel >= 0) && mModel.bottomLeft && mModel.bottomRight) {
            mClip.setLevel(mClip.getLevel() - 1000);
        }
    }

    /**
     * This method is used to response the click event on the "UP" button and causes the spacecraft
     * to slow down or move upwards with specific speed. Meanwhile the fuel bar will show the change of fuel.
     * @param v This is the only parameter which will be passed automatically when user clicks the view
     *          which holds this event handler.
     */
    public void clickUp(View v) {
        mModel.changeSpeedUp();
        if ((mModel.fuel >= 0) && mModel.bottomLeft && mModel.bottomRight) {
            mClip.setLevel(mClip.getLevel() - 2000);
        }
    }
}
