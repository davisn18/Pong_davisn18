package com.example.noah.pong_davisn18;

import android.app.Application;
import android.graphics.*;
import android.view.MotionEvent;

import java.util.Random;

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

    //ball instance variables
    private int xVelocity;
    private int yVelocity;
    private int xVelDir; //positive(1) or negative(-1) x direction
    private int yVelDir; ////positive(1) or negative(-1) y direction
    private float ballXPos;
    private float ballYPos;
    private int radius = 30; //radius of ball
    private boolean holdBall = false; //hold ball out of animation surface

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
        //used to determine if it's the start of the app, sets ball to center
        if (start) {
            ballXPos = animSurface.getWidth() / 2;
            ballYPos = animSurface.getHeight() / 2;
        }

        int wallWidth = 50;
        int padHeight = 50;
        int halfPaddle;

        if (main.difficulty == 0)
            halfPaddle = 200;
        else if (main.difficulty == 1)
            halfPaddle = 125;
        else
            halfPaddle = 50;

        Paint wallPaint = new Paint();
        wallPaint.setColor(0xFF39FF14); //neon green
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.WHITE);
        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);
        //draw walls
        g.drawRect(0, 0, wallWidth, height, wallPaint); //left wall
        g.drawRect(0, 0, width, wallWidth, wallPaint); //top wall
        g.drawRect(width - wallWidth, 0, width, height, wallPaint); //right wall
        //draw paddle
        g.drawRect(width / 2 - halfPaddle, height - padHeight, width / 2 + halfPaddle, height, paddlePaint);
        //draw ball in correct position
        g.drawCircle(ballXPos, ballYPos, radius, ballPaint);

        //check for collisions
        ballChecker(width, height, wallWidth, padHeight, halfPaddle);

        updateBall(); //update ball's position
        start = false; //no longer is the start of the app
    }

    /*
     * collision checker, checks if hits walls or paddle and
     * responds appropriately to the collision
     *
     * @param width - width of surface view
     * @param height - height of surface view
     * @param wallWidth - width of walls that are drawn
     * @param padHeight - height of paddle
     * @param halfPaddle - half the width of the paddle
     */
    private void ballChecker(int width, int height, int wallWidth, int padHeight , int halfPaddle) {
        //top left corner, if ball hits directly in the corner, both x & y velocities are flipped
        if (ballXPos <= (wallWidth + radius) && ballYPos <= (wallWidth + radius)) {
            xVelocity = -xVelocity;
            yVelocity = -yVelocity;
        }
        //top right corner, if ball hits directly in the corner, both x & y velocities are flipped
        else if (ballXPos >= ((width - wallWidth) - radius) && ballYPos <= (wallWidth + radius)) {
            xVelocity = -xVelocity;
            yVelocity = -yVelocity;
        }
        //left wall
        else if (ballXPos <= (wallWidth + radius))
            xVelocity = -xVelocity;
            //top wall
        else if (ballYPos <= (wallWidth + radius))
            yVelocity = -yVelocity;
            //right wall
        else if (ballXPos >= (width - wallWidth) - radius)
            xVelocity = -xVelocity;
            //bottom and hit paddle at the left corner, switch both velocities
        else if (ballXPos >= (width / 2 - halfPaddle - radius) && ballXPos <= (width / 2 - halfPaddle)
                && ballYPos >= (height - padHeight - radius)) {
            xVelocity = -xVelocity;
            yVelocity = -yVelocity;
        }
        //bottom and hit paddle at the right corner, switch both velocities
        else if (ballXPos <= (width / 2 + halfPaddle + radius) && ballXPos >= (width / 2 + halfPaddle)
                && ballYPos >= (height - padHeight - radius)) {
            xVelocity = -xVelocity;
            yVelocity = -yVelocity;
        }
        //bottom and hit paddle
        else if (ballYPos >= (height - padHeight - radius) && ballXPos >= (width / 2 - halfPaddle)
                && ballXPos <= (width / 2 + halfPaddle))
            yVelocity = -yVelocity;
            //bottom and missed paddle
        else if (ballYPos >= (height + radius)) {
            if (main.restart) {
                newVelocity();
                ballXPos = animSurface.getWidth() / 2;
                ballYPos = animSurface.getHeight() / 2;
                main.restart = false; //reset boolean for restart button
                holdBall = false; //release ball
                updateBall(); //update it's position
            }
            else
                holdBall = true; //restart isn't pressed so don't release a new ball
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
     * Helper method to update position of ball
     */
    private void updateBall() {
        if (!holdBall) {
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
     *
     */
    public void onTouch(MotionEvent event) {
    }


}//class TextAnimator

