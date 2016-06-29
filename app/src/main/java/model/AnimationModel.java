package model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.ImageView;

import com.example.marshal.animationlab.R;

/**
 * This class is used to define an AnimationModel which holds the code to describe the movement of
 * spacecraft and draw the result to a canvas object.
 *
 * @author Shuai Yuan
 * @version 1.1
 * @since 2016-05-20
 */
public class AnimationModel {

    public final int GRAVITY = 1;
    public final double TIME_INCREMENT = 0.02;

    private int INIT_POS_X;
    private int INIT_POS_Y = 0;
    private int INIT_SPEED_X = 0;
    private int INIT_SPEED_Y = 0;

    int xcor[] = { 0, 686, 686, 577, 548, 526, 512, 498, 382, 368, 336, 327,
            309, 298, 275, 260, 218, 190, 150, 0, 0 };
    int ycor[] = { 0, 0, 450, 605, 605, 594, 530, 520, 520, 527, 626, 636,
            636, 623, 535, 504, 481, 481, 650, 650, 0 };

    private Path mPath;
    private Bitmap craftImage, leftThruster, rightThruster, mainEngine, explosionImage, wreckageImage;
    private int craftPosX, craftPoxY, bottomLeftX, bottomRightX, bottom, screenWidth;
    private float craftSpeedX, craftSpeedY;
    private float time;

    public int fuel = 10;
    public boolean bottomLeft, bottomRight;
    public boolean landed = false;
    public boolean crashed = false;

    private boolean flameLeft = false;
    private boolean flameRight = false;
    private boolean flameMain = false;
    private float flameTimer, explorTimer = 0;

    private Paint backgroundPaint = new Paint();

    /**
     * This constructor method is used to inflate the bitmap objects from drawable resources and initialize
     * necessary variables which are needed at the first beginning.
     * @param context This is an Context parameter which will be passed automatically when the class is instantiated.
     */
    public AnimationModel(Context context) {
        super();

        craftImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.craftmain);
        leftThruster = BitmapFactory.decodeResource(context.getResources(), R.drawable.left_thruster);
        rightThruster = BitmapFactory.decodeResource(context.getResources(), R.drawable.right_thruster);
        mainEngine = BitmapFactory.decodeResource(context.getResources(), R.drawable.main_engine);
        explosionImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion);
        wreckageImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.wreckage);
        craftPoxY = INIT_POS_Y;
        craftSpeedX = INIT_SPEED_X;
        craftSpeedY = INIT_SPEED_Y;

        backgroundPaint.setColor(Color.BLACK);
    }

    /**
     * This method is used to describe the movement of spacecraft by calculating the (x, y) position.
     */
    public void move() {

        getBottom();

        //  Collision detection
        bottomLeft = contains(xcor, ycor, bottomLeftX, bottom);
        bottomRight = contains(xcor, ycor, bottomRightX, bottom);

        //  Calculate the next position of spacecraft if there's no collision. Flying out of the top of the screen is allowed.
        if ((bottomLeft && bottomRight) || (bottom <= 0)) {
            time += TIME_INCREMENT;
            craftPosX += craftSpeedX;
            craftPoxY += (int) ((craftSpeedY * time) + (0.5 * GRAVITY * time * time));
        }
    }

    /**
     * This method is used to calculate left bottom and right bottom positions of the spacecraft.
     * If the spacecraft flies out of the left or right boundary of the game field, it will pass through
     * to the opposite side of the game field.
     */
    private void getBottom() {
        bottomLeftX = craftPosX + 5;
        bottomRightX = craftPosX + 89;
        if (bottomLeftX < 0) {
            bottomLeftX += screenWidth;
            if (bottomRightX < 0){
                bottomRightX += screenWidth;
            }
        }
        if (bottomRightX > screenWidth) {
            bottomRightX -= screenWidth;
            if (bottomLeftX > screenWidth) {
                bottomLeftX -= screenWidth;
            }
        }
        bottom = craftPoxY + 92;
    }

    /**
     * This method is used to draw the result to a canvas object.
     * @param canvas This is an Canvas parameter to which the result will be drawn.
     */
    public void draw(Canvas canvas) {

        canvas.drawPath(mPath, backgroundPaint);    //Draw the black portion above the terrain.

        if ((bottomLeft && bottomRight) || (bottom <= 0)) {
            canvas.drawBitmap(craftImage, craftPosX, craftPoxY, null);
            flameDraw(canvas, craftPosX);   //If user clicks "LEFT", "RIGHT", or "UP" buttons, draw a flame at the given position.
            drawWrapAround(canvas); //If the spacecraft flies out of the left or right boundary of the game field, draw on the opposite side of the game field.
        }
        //If there's a collision with the terrain, draw the crash or landing.
        else {
            craftSpeedY = craftSpeedY + (GRAVITY * time);
            time = 0;
            if ((bottomLeft != bottomRight) || (craftSpeedY > 3)){
                craftCrash(canvas);
            }
            else {
                craftLand(canvas);
            }
        }
    }

    /**
     * This method is used to draw the spacecraft on the opposite side of the game field in case
     * the spacecraft flies out of the left or right boundary of the game field.
     * @param canvas This is an Canvas parameter to which the spacecraft will be drawn.
     */
    private void drawWrapAround(Canvas canvas) {
        //The spacecraft flies out of the left boundary.
        if (craftPosX < 0) {
            canvas.drawBitmap(craftImage, craftPosX + screenWidth, craftPoxY, null);
            flameDraw(canvas, craftPosX + screenWidth);
            if ((craftPosX + 94) < 0) {
                craftPosX += screenWidth;
            }
        }
        //The spacecraft flies out of the right boundary.
        if ((craftPosX + 94) > screenWidth) {
            canvas.drawBitmap(craftImage, craftPosX - screenWidth, craftPoxY, null);
            flameDraw(canvas, craftPosX - screenWidth);
            if (craftPosX > screenWidth) {
                craftPosX -= screenWidth;
            }
        }
    }

    /**
     * This method is used to draw the spacecraft if it landed successfully.
     * @param canvas This is an Canvas parameter to which the spacecraft will be drawn.
     */
    private void craftLand(Canvas canvas) {
        canvas.drawBitmap(craftImage, craftPosX, craftPoxY, null);
        landed = true;
    }

    /**
     * This method is used to draw an explosion, a crater and the wreckage of the spacecraft if it
     * crashed to the terrain.
     * @param canvas This is an Canvas parameter to which the result will be drawn.
     */
    private void craftCrash(Canvas canvas) {
        if (explorTimer < 1) {
            canvas.drawBitmap(explosionImage, craftPosX, craftPoxY, null);
            explorTimer += 0.05;
        }
        else {
            canvas.drawCircle(craftPosX + 60, craftPoxY + 60, 100, backgroundPaint);
            canvas.drawBitmap(wreckageImage, craftPosX, craftPoxY + 50, null);
            crashed = true;
        }
    }

    /**
     * This method is used to change the speed to left. If the fuel is exhausted, the speed will not change.
     */
    public void changeSpeedLeft() {
        if (fuel > 0) {
            craftSpeedX -= 1;
            fuel -= 1;
            flameRight = true;
            flameTimer = 0;
        }
        else {
            flameRight = false;
        }
    }

    /**
     * This method is used to change the speed to right. If the fuel is exhausted, the speed will not change.
     */
    public void changeSpeedRight() {
        if (fuel > 0) {
            craftSpeedX += 1;
            fuel -= 1;
            flameLeft = true;
            flameTimer = 0;
        }
        else {
            flameLeft = false;
        }
    }

    /**
     * This method is used to change the speed upwards or downwards. If the fuel is exhausted, the speed will not change.
     */
    public void changeSpeedUp() {
        if (fuel > 0) {
            craftSpeedY = craftSpeedY + (GRAVITY * time) - 3;
            time = 0;
            fuel -= 2;
            flameMain = true;
            flameTimer = 0;
        }
        else {
            flameMain = false;
        }
    }

    /**
     * This method is used to set the position of spacecraft on x direction.
     * @param posX This is an int parameter as the value on x direction.
     */
    public void setPosX(int posX) {
        INIT_POS_X = posX;
        craftPosX = INIT_POS_X;
    }

    /**
     * This method is used to draw a spacecraft with flame.
     * @param canvas This is an Canvas parameter to which the spacecraft will be drawn.
     * @param positionX This is an int parameter to determine the x position where the spacecraft should be drawn.
     */
    public void flameDraw(Canvas canvas, int positionX) {
        // Set a timer for the flame to allow the flame last for a short while.
        if (flameTimer < 1) {
            if (flameMain) {
                canvas.drawBitmap(mainEngine, positionX, craftPoxY, null);
            }
            if (flameLeft) {
                canvas.drawBitmap(leftThruster, positionX, craftPoxY, null);
            }
            if (flameRight) {
                canvas.drawBitmap(rightThruster, positionX, craftPoxY, null);
            }
            flameTimer += 0.05;
        }
        else {
            flameMain = false;
            flameRight = false;
            flameLeft = false;
        }
    }

    /**
     * This method is used to draw a closed polygon to form a terrain
     */
    public void initPath() {

        mPath = new Path();
        for (int i = 0; i < xcor.length; i++) {
            mPath.lineTo(xcor[i], ycor[i]);
        }
    }

    /**
     * This method is used to judge whether a given position is within a closed polygon.
     * @param xcor This is an int array parameter to define a closed polygon.
     * @param ycor This is an int array parameter to define a closed polygon.
     * @param x0 This is a double parameter to indicate a given x position.
     * @param y0 This is a double parameter to indicate a given y position.
     * @return boolean If the given position is within the closed polygon, return "true".
     */
    public boolean contains(int[] xcor, int[] ycor, double x0, double y0) {
        int crossings = 0;

        for (int i = 0; i < xcor.length - 1; i++) {
            int x1 = xcor[i];
            int x2 = xcor[i + 1];

            int y1 = ycor[i];
            int y2 = ycor[i + 1];

            int dy = y2 - y1;
            int dx = x2 - x1;

            double slope = 0;
            if (dx != 0) {
                slope = (double) dy / dx;
            }

            boolean cond1 = (x1 <= x0) && (x0 < x2); // is it in the range?
            boolean cond2 = (x2 <= x0) && (x0 < x1); // is it in the reverse
            // range?
            boolean below = (y0 > slope * (x0 - x1) + y1); // point slope y - y1

            if ((cond1 || cond2) && below) {
                crossings++;
            }
        }
        return (crossings % 2 != 0); // even or odd
    }

    /**
     * This method is used to set the width of the game field.
     * @param w This is an int parameter as the value of the width.
     */
    public void setScreenWidth(int w) {
        screenWidth = w;
    }
}
