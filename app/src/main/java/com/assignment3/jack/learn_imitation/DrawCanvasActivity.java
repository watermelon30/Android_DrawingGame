package com.assignment3.jack.learn_imitation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DrawCanvasActivity extends AppCompatActivity {
    private DrawingCanvas drawingCanvas;
    private Button strokeWidth;
    private Button clearCanvas;
    private Button checkImage;
    private Button finishDrawing;
    private Button undo;
    private Button finishStrokeWidth;
    private SeekBar seekBar;
    private int currentImageIndex;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.drawing_canvas);

        //Getting the info sent from main activity.
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            currentImageIndex = extras.getInt("currentImageIndex");
        }

        setView();

        setOnClickListener();
    }

    private void setView(){
        drawingCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
        clearCanvas = (Button) findViewById(R.id.redraw);
        checkImage = (Button) findViewById(R.id.check);
        finishDrawing = (Button) findViewById(R.id.finish);
        strokeWidth = (Button) findViewById(R.id.strokeWidth);
        finishStrokeWidth = (Button) findViewById(R.id.finishAdjust);
        undo = (Button) findViewById(R.id.undo);
        seekBar = (SeekBar) findViewById(R.id.adjustWidth);

        //Set maximum stroke width.
        seekBar.setMax(85);
        seekBar.setProgress(50);
    }


    private void setOnClickListener(){
        //Button for changing stroke width.
        strokeWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setVisibility(View.VISIBLE);
                finishStrokeWidth.setVisibility(View.VISIBLE);
                strokeWidth.setVisibility(View.GONE);
            }
        });

        //Button for finishing changing stroke width.
        finishStrokeWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setVisibility(View.GONE);
                finishStrokeWidth.setVisibility(View.GONE);
                strokeWidth.setVisibility(View.VISIBLE);
            }
        });

        //Button for restarting canvas
        clearCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DrawCanvasActivity.this);
                builder.setTitle("Sure to clear canvas?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        drawingCanvas.clearCanvas();
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
        });

        //Check the current image to imitate.
        checkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ImageToDrawActivity.class);
                i.putExtra("currentImageIndex", currentImageIndex);
                //Indicate the
                i.putExtra("routeFrom", 'C');
                startActivity(i);
            }
        });

        //Button to undo the most current stroke
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Switch gridMode. Decode and apply a new bmp to imageView.
                drawingCanvas.undo();
            }
        });

        //Seek bar to set the stroke width.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //Tell canvas to change stroke width for the next line drawing.
                drawingCanvas.changeStrokeWidth(progress);
            }

            //Didn't use.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Intent to the checkResult Activity for getting result.
        finishDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GetResultActivity.class);
                //Get the user drawing bitmap.
                Bitmap bmpToSend = drawingCanvas.getDrawing();

                //Compress and convert bitmap to byte array to be able to send between activities.
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmpToSend.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();

                //Cleanup
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Free memory by calling garbage collector to collect bitmap.
                bmpToSend.recycle();
                int strokeNum = drawingCanvas.getStrokeNum();

                //We need to change bmp to bytes array because
                //there is an limitation on the size of the object sent between activities.
                i.putExtra("bytes", bytes);
                i.putExtra("currentImageIndex", currentImageIndex);
                i.putExtra("strokeNum", strokeNum);
                startActivity(i);
            }

        });
    }


    //Handle back button pressed. Ask if user wants to be back to menu.
    @Override
        public void onBackPressed() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Go back to menu?");
            builder.setMessage("Warning: You will lose your current drawing.");
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
