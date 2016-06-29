package view;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * This class is used to define a Thread to run the drawing actions.
 *
 * @author Shuai Yuan
 * @version 1.1
 * @since 2016-05-20
 */
public class AnimationThread extends Thread {

    private AnimationView mAnimView;
    private SurfaceHolder mHolder;
    private Boolean mRunning = false;

    /**
     * This constructor method is used to set up the relationship between this Thread and the SurfaceView
     * on which it will draw.
     * @param animView This is an AnimationView parameter which will hold the Thread..
     */
    public AnimationThread(AnimationView animView) {
        super();
        this.mAnimView = animView;
        this.mHolder = animView.getHolder();
    }

    /**
     * This method is used to set the running status of the thread.
     */
    public void setRunning(boolean running) {
        mRunning = running;
    }

    /**
     * This method is used to run the Thread.
     */
    @Override
    public void run() {
        super.run();

        Canvas canvas;

        while (mRunning) {
            canvas = null;
            try {
                canvas = mHolder.lockCanvas();
                synchronized (mHolder) {
                    mAnimView.update();
                    mAnimView.render(canvas);
                }
            }
            finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
