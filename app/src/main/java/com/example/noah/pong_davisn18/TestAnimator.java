package com.example.noah.pong_davisn18;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.graphics.*;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * class that animates a ball repeatedly moving diagonally on
 * simple background
 *
 * @author Noah Davis
 * @version March 2018
 */
public class TestAnimator implements Animator {

    // instance variables
    private AnimationSurface animSurface;
    private PongMainActivity main;
    private Random random = new Random(); //random function
    private boolean start = true; //used to determine if start of app

    private int padCenter; //location of center of paddle

    //ball instance variables
    private double xVelocity, yVelocity;
    private int xVelDir; //positive(1) or negative(-1) x direction
    private int yVelDir; //positive(1) or negative(-1) y direction
    private double randSpeed;
    private float ballXPos, ballYPos;
    private int radius = 30; //radius of ball

    public TestAnimator(PongMainActivity activity, AnimationSurface surface) {
        this.main = activity;
        this.animSurface = surface;

        newVelocity(); //give ball a random velocity
    }

    /**
     * Interval between animation frames: .03 seconds (i.e., about 33 times
     * per second).
     *
     * @return the time interval between frames, in milliseconds.
     */
    public int interval() {
        return 10;
    }

    /**
     * The background color: a light blue.
     *
     * @return the background color onto which we will draw the image.
     */
    public int backgroundColor() {
        // create/return the background color
        return Color.BLACK;
    }

    /**
     * Action to perform on clock tick
     *
     * @param g the graphics object on which to draw
     */
    public void tick(Canvas g) {

        int width = animSurface.getWidth();
        int height = animSurface.getHeight();
        int wallWidth = 50;
        int padHeight = 50;
        int padHalf;

        //used to determine if it's the start of the app, sets ball and paddle to center
        if (start) {
            ballXPos = animSurface.getWidth() / 2;
            ballYPos = animSurface.getHeight() / 2;
            padCenter = width / 2;
        }

        //set paddle variable to correct size depending on difficulty
        if (main.difficulty == 0)
            padHalf = 200;
        else if (main.difficulty == 1)
            padHalf = 125;
        else
            padHalf = 50;

        Paint wallPaint = new Paint();
        wallPaint.setColor(0xFF39FF14); //neon green
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.WHITE);
        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(120f);
        //draw walls
        g.drawRect(0, 0, wallWidth, height, wallPaint); //left wall
        g.drawRect(0, 0, width, wallWidth, wallPaint); //top wall
        g.drawRect(width - wallWidth, 0, width, height, wallPaint); //right wall
        //draw text for score
        g.drawText("Score: " + main.score, width / 2.4f, height / 2, textPaint);

        //check if paddle is hitting wall; stop paddle if it hits wall
        if (padCenter - padHalf - wallWidth <= 0) //left wall
            padCenter = wallWidth + padHalf;
        else if (padCenter + padHalf + wallWidth >= width) //right wall
            padCenter = width - wallWidth - padHalf;
        //draw paddle
        g.drawRect(padCenter - padHalf, height - padHeight, padCenter + padHalf, height, paddlePaint);

        if (!main.gameOver || !main.holdBall) //don't draw ball if game is over or new ball button isn't pressed yet
            //draw ball in correct position
            g.drawCircle(ballXPos, ballYPos, radius, ballPaint);

        randomVelocity();

        //check for collisions
        ballChecker(width, height, wallWidth, padHeight, padHalf);

        updateBall(); //update ball's position
        start = false; //no longer is the start of the app
    }

    /*
     * collision checker, checks if hits walls or paddle and
     * responds appropriately to the collision
     * multiplies velocities by random speed to change velocity a bit
     *
     * @param width - width of surface view
     * @param height - height of surface view
     * @param wallWidth - width of walls that are drawn
     * @param padHeight - height of paddle
     * @param halfPaddle - half the width of the paddle
     */
    private void ballChecker(int width, int height, int wallWidth, int padHeight, int halfPaddle) {
        //top left corner, if ball hits directly in the corner, both x & y velocities are flipped
        if (ballXPos <= (wallWidth + radius) && ballYPos <= (wallWidth + radius)) {
            xVelocity = -xVelocity * randSpeed;
            yVelocity = -yVelocity * randSpeed;
        }
        //top right corner, if ball hits directly in the corner, both x & y velocities are flipped
        else if (ballXPos >= ((width - wallWidth) - radius) && ballYPos <= (wallWidth + radius)) {
            xVelocity = -xVelocity * randSpeed;
            yVelocity = -yVelocity * randSpeed;
        }
        //left wall
        else if (ballXPos <= (wallWidth + radius))
            xVelocity = -xVelocity * randSpeed;
        //top wall
        else if (ballYPos <= (wallWidth + radius))
            yVelocity = -yVelocity * randSpeed;
        //right wall
        else if (ballXPos >= (width - wallWidth) - radius)
            xVelocity = -xVelocity * randSpeed;
        //bottom and hit paddle at the left corner, switch both velocities
        else if (ballXPos >= (padCenter - halfPaddle - radius) && ballXPos <= (padCenter - halfPaddle)
                && ballYPos >= (height - padHeight - radius)) {
            xVelocity = -xVelocity * randSpeed;
            yVelocity = -yVelocity * randSpeed;
            main.score++;
        }
        //bottom and hit paddle at the right corner, switch both velocities
        else if (ballXPos <= (padCenter + halfPaddle + radius) && ballXPos >= (padCenter + halfPaddle)
                && ballYPos >= (height - padHeight - radius)) {
            xVelocity = -xVelocity * randSpeed;
            yVelocity = -yVelocity * randSpeed;
            main.score++;
        }
        //bottom and hit paddle
        else if (ballYPos >= (height - padHeight - radius) && ballXPos >= (padCenter - halfPaddle)
                && ballXPos <= (padCenter + halfPaddle)) {
            yVelocity = -yVelocity * randSpeed;
            main.score++;
        }
        //bottom and missed paddle
        else if (ballYPos >= (height + radius)) {
            if (!main.gameOver) { //if game is over, don't start another ball
                //restartClicked
                if (main.newBall && !main.holdBall) {
                    newVelocity();
                    main.holdBall = false; //release ball
                    ballXPos = animSurface.getWidth() / 2;
                    ballYPos = animSurface.getHeight() / 2;
                    if (!main.gameOver) //don't lose points when game is over
                        main.score -= 3; //lose 3 points when miss ball
                    main.newBall = false; //reset boolean for restart (new ball) button

                    updateBall(); //update it's position
                } else {
                    main.holdBall = true; //restart isn't pressed so don't release a new ball
                    main.restartClickedBN = false;
                    if (main.holdBall && main.numLives == 0) { //check if game is over
                        main.gameOver = true;
                    }
                }
            }
        }
    }

    /*
     * Helper method to give the ball a new random velocity and direction
     */
    private void newVelocity() {
        //determines which X direction ball will move
        if (random.nextInt(2) == 1)
            xVelDir = 1;
        else
            xVelDir = -1;
        //determines which Y direction ball will move
        if (random.nextInt(2) == 1)
            yVelDir = 1;
        else
            yVelDir = -1;

        xVelocity = xVelDir * (random.nextInt(20) + 5); //random int from 5 to 25
        yVelocity = yVelDir * (random.nextInt(20) + 5);
    }

    /*
     * Helper method to give the ball a new random velocity when hitting walls/paddle
     */
    private void randomVelocity() {
        double max = 1.2;
        double min = 0.8;
        //random double between 0.8 and 1.2
        randSpeed = min + (max - min) * random.nextDouble();
    }


    /*
     * Helper method to update position of ball
     */
    private void updateBall() {
        if (!main.holdBall) {
            //set ball position to velocity every tick so ball constantly updates
            ballXPos += xVelocity;
            ballYPos += yVelocity;
        }
    }

    /**
     * Tells that we never pause.
     *
     * @return indication of whether to pause
     */
    public boolean doPause() {
        return false;
    }

    /**
     * Tells that we never stop the animation.
     *
     * @return indication of whether to quit.
     */
    public boolean doQuit() {
        return false;
    }

    /**
     * External Citation
     * Date: 25 March 2018
     * Problem: Didn't know how to use MotionEvent to get paddle position
     * Resource: https://stackoverflow.com/questions/29792496/
     * motionevent-action-down-in-android-is-too-sensitive-this-event-is-received-even
     * Solution: I used the example code from this post.
     */
    public void onTouch(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        if (action == MotionEvent.ACTION_MOVE)
            padCenter = x;
    }

}//class TextAnimator

