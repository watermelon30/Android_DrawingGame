package com.assignment3.jack.learn_imitation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImageToDrawActivity extends AppCompatActivity {
    private ImageView imageToDraw;
    private Bitmap image;
    private Canvas canvas;

    private Button startButton;
    private Button nextButton;
    private Button prevButton;
    private Button addGrid;

    private static int squareCanvasWidth;
    private static boolean gridMode = true;

    private int currentImageIndex;
    private Character routeFrom = ' ';
    private boolean justStarted = true;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_to_draw_activity);

        //Getting the width of user phone for scaling image.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (displayMetrics.heightPixels > displayMetrics.widthPixels)
            squareCanvasWidth = displayMetrics.widthPixels;
        else squareCanvasWidth = displayMetrics.heightPixels;

        //Get the image index from saved instance state in one existed.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentImageIndex = extras.getInt("currentImageIndex");
            routeFrom = extras.getChar("routeFrom");
            //Check where the activity was from. If from canvas it means the user is just checking image.
            if(routeFrom == 'C') {justStarted = false;}
            else justStarted = true;
        } else {
            //Start from first image if bundle is empty.
            currentImageIndex = 0;
        }

        imageToDraw = (ImageView) findViewById(R.id.imageToDraw);
        startButton = (Button) findViewById(R.id.startButton);
        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.previous);
        addGrid = (Button) findViewById(R.id.addGrid);

        //Setting image depends on the currentImageIndex.
        image = BitmapFactory.decodeResource(getResources(), allImages[currentImageIndex]);
        //Scale to fit the image view.
        image = Bitmap.createScaledBitmap(image, squareCanvasWidth, squareCanvasWidth, false);

        //To show grid or not.
        setImageWithGrid(gridMode, image);

        setClickListener();

        if (!justStarted) {
            startButton.setText("Back to canvas");
        }

    }

    private void setClickListener(){

        //Button to start drawing or return to canvas.
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to the canvas if the user is just checking picture.
                if(!justStarted){
                    //Back to the activity who called startActivity(i);
                    finish();
                }
                else{
                    //Start the drawing.
                    Intent i = new Intent(getApplicationContext(), DrawCanvasActivity.class);
                    i.putExtra("currentImageIndex", currentImageIndex);
                    startActivity(i);
                }
            }
        });

        //Check the next picture.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentImageIndex + 1 == allImages.length) {
                    currentImageIndex = 0;
                } else {
                    currentImageIndex++;
                }
                image = BitmapFactory.decodeResource(getResources(), allImages[currentImageIndex]);
                image = Bitmap.createScaledBitmap(image, squareCanvasWidth, squareCanvasWidth, false);
                setImageWithGrid(gridMode, image);
            }
        });

        //Check the previous picture.
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Not allow to check previous if current index is 0.
                if (currentImageIndex > 0) {
                    currentImageIndex--;
                } else {
                    currentImageIndex = allImages.length-1;
                }
                image = BitmapFactory.decodeResource(getResources(), allImages[currentImageIndex]);
                image = Bitmap.createScaledBitmap(image, squareCanvasWidth, squareCanvasWidth, false);
                setImageWithGrid(gridMode, image);
            }
        });

        //Button to allow user to add/remove grid line.
        addGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Switch gridMode. Decode and apply a new bmp to imageView.
                gridMode = !gridMode;
                if(gridMode) addGrid.setText("Remove Grid");
                else addGrid.setText("Show Grid");

                image = BitmapFactory.decodeResource(getResources(), allImages[currentImageIndex]);
                image = Bitmap.createScaledBitmap(image, squareCanvasWidth, squareCanvasWidth, false);
                setImageWithGrid(gridMode, image);
            }
        });
    }

    //Function to either set image with or without grid.
    private void setImageWithGrid(Boolean yes, Bitmap squareBmp) {
        final int LINENUM = 3;
        //Height and width will be the same.
        float height;
        float gap;

        if (yes) {

            //Redraw the bitmap with grid on a temp canvas
            Canvas canvas = new Canvas(squareBmp);
            Paint paint = new Paint();
            Path path = new Path();

            height = squareBmp.getHeight();
            gap = height / (LINENUM + 1);

            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(5f);

            //Draw grid.
            for (int i = 1; i < LINENUM + 1; i++) {
                //Draw parallel grid
                path.moveTo(0, gap * i);
                path.lineTo(height, gap * i);
                canvas.drawPath(path, paint);

                //Draw vertical grid
                path.moveTo(gap * i, 0);
                path.lineTo(gap * i, height);
                canvas.drawPath(path, paint);
            }
            //Set the new bmp with grid onto image view
            imageToDraw.setImageBitmap(squareBmp);
        }
        else {
            //Set the original bmp to image view.
            imageToDraw.setImageBitmap(squareBmp);
        }
    }


    //Handle back button pressed. Ask if user want to be back to menu.
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