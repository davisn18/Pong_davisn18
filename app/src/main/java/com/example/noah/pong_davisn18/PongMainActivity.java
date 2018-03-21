package com.example.noah.pong_davisn18;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

/**
 * PongMainActivity
 *
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 *
 * Just run the app and watch the ball bounce off the paddle and walls.
 * Press restart for a new ball once the other goes out of play
 * Change the difficulty to have different paddle sizes
 *
 * Enhancements:
 *  1) When a ball leaves the field of play, new ball isn't added until the user
 *     presses the restart button
 *  2) Allow the user to change the size of the paddle through the difficult setting
 *     by selecting the level through the spinner
 *  3) Compatible on any device (Pixel C or Nexus 9, etc.) and can be played in both
 *     horizontal and landscape orientation
 *
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @author Noah Davis
 * @version March 2018
 *
 */
public class PongMainActivity extends Activity {

    public boolean restart = false;
    public int difficulty;
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

        Button bnRestart = (Button)this.findViewById(R.id.bnRestart);
        bnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart = true;
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
                 Resource:
                 http:https://stackoverflow.com/questions/13397933/
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
