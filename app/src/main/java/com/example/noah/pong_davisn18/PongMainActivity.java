package com.example.noah.pong_davisn18;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * PongMainActivity
 *
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 *
 * Just run the app and try to move the paddle so that it stays within play
 * Press New Ball button for a new ball once the ball goes out of play
 * Change the difficulty to have different paddle sizes
 * Once out of lives, user will be prompted with message, can close application
 * or can start over with 3 lives and a score of 0 again.
 *
 * Enhancements Part A:
 *  1) When a ball leaves the field of play, new ball isn't added until the user
 *     presses the restart button
 *  2) Allow the user to change the size of the paddle through the difficult setting
 *     by selecting the level through the spinner
 *  3) Compatible on any device (Pixel C or Nexus 9, etc.) and can be played in both
 *     horizontal and landscape orientation
 *
 * Enhancements Part B:
 *  1) When a ball leaves the field of play, the player loses a life. The player is allotted
 *     3 lives and once there is none remaining, the game is over and the user is prompted
 *     with a message to either start over or quit.
 *  2) Keep a running score, plus 1 for each paddle hit and -3 for a missed hit
 *  3) Incorporated randomness to the ball's bouncing velocity when hitting walls/paddle
 *     (sometimes will speed up, sometimes will slow down, sometimes will have no change)
 *
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @author Noah Davis
 * @version March 2018
 *
 */
public class PongMainActivity extends Activity {

    private Context context = this;
    public boolean gameOver; //out of lives
    public boolean newBall = false; //when newBall is in play
    public boolean restartClickedBN = false; //used when ball is out of play with button presses
    public boolean holdBall = false; //hold ball out of animation surface
    public int difficulty;
    public int numLives = 3, score = 0; //keep track of number of lives and score of user
    private int check = 0; //used to not call code when first time spinner is pressed

    /**
     * creates an AnimationSurface containing a TestAnimator.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong_main);

        // Connect the animation surface with the animator
        AnimationSurface mySurface = (AnimationSurface) this
                .findViewById(R.id.animationSurface);
        mySurface.setAnimator(new TestAnimator(this, mySurface));

        final TextView livesNum = (TextView) findViewById(R.id.numLivesTV);

        Button newBallBN = (Button)this.findViewById(R.id.bnRestart);
        newBallBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //used so that user can't press new ball a bunch and keep having lives decrement
                if (!restartClickedBN) {
                    restartClickedBN = true;
                    numLives--; //decrement lives
                    newBall = true; //newBall is in play
                    holdBall = false; //no longer holding ball
                    if (!gameOver)
                        livesNum.setText(String.valueOf(numLives)); //set text to remaining lives

                /*
                 * code below sets up alert dialog pop up when user tries to restart without any lives left
                 *
                 * External Citation
                 * Date: 26 March 2018
                 * Problem: Didn't know how to create a pop up for the user when game is over
                 * Resource: https://www.mkyong.com/android/android-alert-dialog-example/
                 * Solution: I used the example code from this post.
                 */
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set title
                    alertDialogBuilder.setTitle("Game Over");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("You are out of lives! Your score was " + score + ". You suck!\n" +
                                    "Would you like to play again or be a quitter?")
                            .setCancelable(false)
                            .setPositiveButton("Start Over", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, reset game (score & number of lives)
                                    score = 3; //starts score at -3 if set to 0 so score is 0 when set to 3
                                    numLives = 3;
                                    livesNum.setText(String.valueOf(numLives)); //set text to remaining lives
                                    gameOver = false;
                                }
                            })
                            .setNegativeButton("Exit Application", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    System.exit(0);
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    //show dialog when user is out of lives so game is over
                    if (numLives == -1 && gameOver)
                        alertDialog.show();

                }
            }
        });



        Spinner levels = (Spinner)findViewById(R.id.spLevel);
        levels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if statement in place so it doesn't run when user just clicks on the spinner
                //waits for the second selection
                /**
                 External Citation
                 Date: 18 March 2018
                 Problem: Select Item code would run when first clicking on the spinner
                 Resource: http:https://stackoverflow.com/questions/13397933/
                    android-spinner-avoid-onitemselected-calls-during-initialization
                 Solution: I used the example code from this post.
                 */
                if (++check > 1) {
                    difficulty = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }
}
