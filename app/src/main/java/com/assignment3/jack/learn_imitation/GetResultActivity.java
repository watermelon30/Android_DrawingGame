package com.assignment3.jack.learn_imitation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;

public class GetResultActivity extends AppCompatActivity {

    private Button  checkImage;
    private Button restart;
    private Button startNext;
    private TextView result;
    private TextView imageDescript;
    private ImageView canvas;

    //Index of the current sample drawing.
    private int currentImageIndex;
    private Bitmap userDrawing;
    private Bitmap original;
    private byte[] bytes;
    private int squareCanvasWidth;
    private int strokeNum;
    private Boolean showUserDrawing = true;

    //All the sample images for user to imitate.
    private final int[] allImages = {
            R.drawable.halfblack,
            R.drawable.mickey,
            R.drawable.diagonal,
            R.drawable.wang,
            R.drawable.lin,
            R.drawable.lee,
            R.drawable.chen,
            R.drawable.zhang,
            R.drawable.zheng,
            R.drawable.shi,
            R.drawable.dragon
    };

    //Description of the sample drawings.
    private final String[] imageInfo = {
            "Black and white",
            "Logo of Disney Mickey Mouse\n" +
                "Mickey Mouse is the cartoon character created by Walt Disney and Ub Iwerk in 1928",
            " ",
            "Wang (Surname)\n" +
                    "Ranked 2nd most common surname in China and 6th in Taiwan.\n" +
                    "Also spelled Wong in Hong Kong due to the pronunciation in Cantonese. \n" +
                    "It has the meaning of king",
            "Lin (Surname)\n" +
                    "Ranked 1st most common surname in China and 5th in Taiwan.\n" +
                    "It has the meaning of forest.\n" +
                    "Notable person with surname Lin: Jeremy Lin (Basketball).",
            "Lee/Li (Surname)\n" +
                    "Ranked 16th most common surname in China and 2nd in Taiwan.\n" +
                    "It has the meaning of plum/plum tree.\n" +
                    "Notable person with surname Lee: Bruce Lee (martial artist).",
            "Chen (Surname)\n" +
                    "Ranked 5th most common surname in China and 1st in Taiwan.\n" +
                    "It has the meaning of old.\n" +
                    "Notable people with surname Chen: Wei Yin Chen (Baseball), Jack Chen (Author of this app lmao)",
            "Zhang (Surname)\n" +
                    "Ranked 3rd most common surname in China and 4th in Taiwan.\n" +
                    "It has the meaning of archer.\n" +
                    "Notable person with surname Zhang: Yi Yun Zhang (My roommate lmao).",
            "Zheng (Surname)\n" +
                    "Ranked 24th most common surname in China and 12th in Taiwan.\n" +
                    "It was the name of a state in China long time ago.\n" +
                    "Notable person with surname Zheng: Penny Zheng (My girlfriend lol).",
            "Shi (Surname)\n" +
                    "Ranked 64th most common surname in China and 68th in Taiwan.\n" +
                    "It has the meaning of rock.\n" +
                    "Notable person with surname Shi: Helen Shi (My roommate lol).",
            "Long (pronunciation)\n\n" +
                    "Chinese character with the meaning of dragon."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        //Getting the info sent from draw canvas activity.
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            bytes = extras.getByteArray("bytes");
            currentImageIndex = extras.getInt("currentImageIndex");
            strokeNum = extras.getInt("strokeNum");
        }

        setView();
        setClickListener();

        //Decode the byte array passed from draw canvas activity.
        userDrawing = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        canvas.setImageBitmap(userDrawing);

        //Check if user has drawn anything on canvas.
        if(strokeNum == 0){
            result.setText("You didn't draw anything on the canvas.");
        }
        else{
            result.setText("Getting result ...");
        }

        //Show image on image view.
        imageDescript.setText(imageInfo[currentImageIndex]);

        //Here we are getting the screen width and height to set our image size.
        //This will be the same with the width of user drawing canvas.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(displayMetrics.heightPixels > displayMetrics.widthPixels) squareCanvasWidth = displayMetrics.widthPixels;
        else squareCanvasWidth = displayMetrics.heightPixels;

        original = BitmapFactory.decodeResource(getResources(),allImages[currentImageIndex]);
        //Scale the original bmp to the same size with user drawing in order to enable calculating accuracy.
        original =  Bitmap.createScaledBitmap(original,squareCanvasWidth, squareCanvasWidth, false);

        //Checking sizes of original and user drawing. They should be the same.
//        Log.d("original", Integer.toString(original.getWidth()));
//        Log.d("original", Integer.toString(original.getHeight()));
//        Log.d("user drawing", Integer.toString(userDrawing.getWidth()));
//        Log.d("user drawing", Integer.toString(userDrawing.getHeight()));

        //Create a background thread to calculate the accuracy if only user has drawn anything.
        if(strokeNum > 0){
            CalculateAccuracy calculateAccuracy = new CalculateAccuracy();
            calculateAccuracy.execute(userDrawing, original);
        }
    }

    private void setView(){
        checkImage = (Button) findViewById(R.id.check);
        restart = (Button) findViewById(R.id.restart);
        startNext = (Button) findViewById(R.id.next);
        result = (TextView) findViewById(R.id.result);
        imageDescript = (TextView) findViewById(R.id.description);
        canvas = (ImageView) findViewById(R.id.userDrawing);
    }

    private void setClickListener(){
        //Button to either check user's drawing or original drawing.
        checkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDrawing = !showUserDrawing;
                if(!showUserDrawing){
                    checkImage.setText("Check your drawing");
                    canvas.setImageBitmap(original);
                } else{
                    checkImage.setText("Check Original");
                    canvas.setImageBitmap(userDrawing);
                }
            }
        });

        //Button to restart the drawing canvas on current sample.
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DrawCanvasActivity.class);
                i.putExtra("currentImageIndex", currentImageIndex);
                startActivity(i);
            }
        });

        //Button to start next drawing.
        startNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Tell the new activity to start from next picture.
                if(currentImageIndex+1 == allImages.length) currentImageIndex = 0;
                else currentImageIndex++;

                Intent i = new Intent(getApplicationContext(), ImageToDrawActivity.class);

                i.putExtra("currentImageIndex", currentImageIndex);

                //Indicate the activity that this is to restart a new game.
                i.putExtra("routeFrom", 'R');

                startActivity(i);
            }
        });
    }

    //Background task calculating image similarity.
    private class CalculateAccuracy extends AsyncTask<Bitmap, String, Float>{
        @Override
        protected Float doInBackground(Bitmap... bitmaps) {
            Bitmap userDrawing = bitmaps[0];
            Bitmap original = bitmaps[1];
            //Width of the pictures. Should be the same for both bmp for both width and height.
            int width = userDrawing.getWidth();
            float accuracy;
            float correctPixel = 0;

            int userPixel, originalPixel;
            //Loop that checks if the color of pixels at the same position are the same.

            //This is not the best algorithm to check the similarity of two drawing.
            //But I wasn't able to find and implement a better one in time.
            for(int i=0; i<width; i++){
                for(int j=0;j<width;j++){
                    userPixel = userDrawing.getPixel(i, j);
                    originalPixel = original.getPixel(i, j);
                    //If the color of two pixels are the same, correct pixel++.
                    if(userPixel == originalPixel){
                        correctPixel = correctPixel+1;
                    }
                    //As we only have two colors, if they both are not background color, we treated them as the same.
                    else if((userPixel != Color.WHITE)&&(originalPixel != Color.WHITE)){
                        correctPixel = correctPixel+1;
                    }
                }
            }

            Log.d("Total correct Pixel: ", Float.toString(correctPixel));
            float totalPixel = width * width;
            accuracy = (correctPixel / totalPixel) * 100;
            return accuracy;
        }

        @Override
        protected void onPostExecute(Float resultAccuracy) {
            super.onPostExecute(resultAccuracy);
            String message = "You drawing accuracy is: " + Float.toString(resultAccuracy) + "%";
            result.setText(message);
        }
    }

    //Handle back button pressed. Ask if user wants to be back to menu.
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go back to menu?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent backHome = new Intent(getApplicationContext(), MainActivity.class);
                //Clear all activities opened before and start the next activity.
                backHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backHome);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog exitAlert = builder.create();
        exitAlert.show();
    }

}
