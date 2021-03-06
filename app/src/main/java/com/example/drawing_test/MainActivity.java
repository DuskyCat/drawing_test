package com.example.drawing_test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageview;
    private final int GET_GALLERY_IMAGE = 200;
    private final int GET_MASK_IMAGE = 300;
    private Button button;
    private Button nbtn;
    Activity activity;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageview = (ImageView)findViewById(R.id.imageView);
        activity = this;
        button = (Button)findViewById(R.id.button);
        nbtn = (Button)findViewById(R.id.button2);
        this.SetListener();
    } // end of onCreate

    public void SetListener()
    {
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Bitmap newImage = Bitmap.createBitmap(bitmap).copy(Bitmap.Config.ARGB_8888,true);
                MyView m = new MyView(MainActivity.this);

            }
        });
        nbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dintent = new Intent(activity,drawActivity.class);
                dintent.putExtra("h", bitmap.getHeight());
                dintent.putExtra("w", bitmap.getWidth());
                startActivity(dintent);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
            bitmap = drawable.getBitmap();
            Log.d("imageview", "Width : " + bitmap.getWidth() );
            Log.d("imageview", "Height : " + bitmap.getHeight() );

        }

    }





    class MyView extends View {
        Paint paint = new Paint();
        Path path = new Path();    // ????????? ????????? ??????
        Bitmap draw_bitmap;
        Bitmap bitmap;

        public MyView(Context context) {
            super(context);
            paint.setStyle(Paint.Style.STROKE); // ?????? ???????????????
            paint.setStrokeWidth(10f); // ?????? ?????? ??????
            draw_bitmap = bitmap;
        }

        public Bitmap getCurrentCanvas() {
            Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);
            return bitmap;
        }

        //@Override
        //protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //    setMeasuredDimension(width, height);
        //}

        @Override
        protected void onDraw(Canvas canvas) { // ????????? ???????????? ?????????
            canvas.drawBitmap(draw_bitmap, 0, 0, null);
            canvas.drawPath(path, paint); // ????????? path ??? ?????????
            //imageview.setImageBitmap(draw_bitmap);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y); // ????????? ????????? ?????? ????????? ????????????
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y); // ????????? ?????? ?????????
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

            invalidate(); // ????????? ???????????????

            return true;
        }

        public void reset() {
            path.reset();
            invalidate();
        }



    }
    public String saveBitmapToJpg(Bitmap bitmap, String name) {
        /**
         * ?????? ??????????????? ???????????? ?????????????????? ???????????? ???????????????.
         *
         * @version target API 28 ??? API29????????? ????????? ?????????????????????.???
         * @param Bitmap bitmap - ??????????????? ?????? ???????????? ?????????
         * @param String fileName - ??????????????? ?????? ???????????? ?????????
         *
         * File storage = ????????? ??? ????????? ??????
         *
         * return = ????????? ???????????? ??????
         *
         * ???????????? ????????? ??????????????? ????????? ???????????? ?????????????????? ???????????????.
         * FileOutputStream?????? ?????????????????? ???????????? ??????????????????.
         */

        File storage = getCacheDir(); //  path = /data/user/0/YOUR_PACKAGE_NAME/cache
        String fileName = name + ".jpg";
        File imgFile = new File(storage, fileName);
        try {
            imgFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out); //???????????? ??????????????? ???????????? ????????????
            out.close();

        } catch (FileNotFoundException e) {
            Log.e("saveBitmapToJpg", "FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmapToJpg", "IOException : " + e.getMessage());
        }
        Log.d("imgPath", getCacheDir() + "/" + fileName);
        return getCacheDir() + "/" + fileName;
    }
} // end of class
