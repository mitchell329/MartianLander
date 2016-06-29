package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import model.AnimationModel;

/**
 * This class is used to define a SurfaceView to draw the animation.
 *
 * @author Shuai Yuan
 * @version 1.1
 * @since 2016-05-20
 */
public class AnimationView extends SurfaceView implements SurfaceHolder.Callback {

    private AnimationThread mAnimTread;
    private AnimationModel mAnimModel;

    /**
     * This constructor method is used to inflate an AnimationView object from xml file and instantiate
     * a Thread that run the animation.
     * @param context This is an Context parameter which will be passed automatically when the class is instantiated.
     * @param attrs This is an AttributeSet parameter which will be passed automatically when the class is instantiated.
     */
    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        mAnimTread = new AnimationThread(this);
    }

    public AnimationView(Context context) {
        super(context);
    }

    /**
     * This method is not used currently.
     * @param holder This is an SurfaceHolder parameter.
     * @param format This is an int parameter.
     * @param width This is an int parameter.
     * @param height This is an int parameter.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * This method is used to pass parameters to related AnimationModel object and start the Thread
     * object when the SurfaceView object is created.
     * @param holder This is an SurfaceHolder parameter.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int width = this.getWidth();
        int height = this.getHeight();
        int position = width / 2 - 24;

        mAnimModel.setScreenWidth(width);
        mAnimModel.setPosX(position);
        mAnimModel.initPath();
        mAnimTread.setRunning(true);
        mAnimTread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * This method is used to release the Thread when the SurfaceView object is destroyed.
     * @param holder This is an SurfaceHolder parameter.
     * @exception InterruptedException on interruption occur.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                mAnimTread.join();
                retry = false;
            }
            catch (InterruptedException e) {

            }
        }
    }

    /**
     * This method is used to update the position and status of the spacecraft.
     */
    public void update() {
        mAnimModel.move();
    }

    /**
     * This method is used to draw result on a canvas object.
     * @param canvas This is an Canvas parameter to which the result will be drawn.
     */
    public void render(Canvas canvas) {
            mAnimModel.draw(canvas);
    }

    /**
     * This method is used to start the animation thread..
     */
    public void startAnimation() {
        mAnimTread.setRunning(true);
    }

    /**
     * This method is used to stop the animation thread..
     */
    public void stopAnimation() {
        mAnimTread.setRunning(false);
    }

    /**
     * This method is used to set up the relationship between this SurfaceView and the AnimationModel
     * which will be used..
     */
    public void setModel(AnimationModel model) {
        mAnimModel = model;
    }
}
