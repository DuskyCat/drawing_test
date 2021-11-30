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
        Path path = new Path();    // 자취를 저장할 객체
        Bitmap draw_bitmap;
        Bitmap bitmap;

        public MyView(Context context) {
            super(context);
            paint.setStyle(Paint.Style.STROKE); // 선이 그려지도록
            paint.setStrokeWidth(10f); // 선의 굵기 지정
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
        protected void onDraw(Canvas canvas) { // 화면을 그려주는 메서드
            canvas.drawBitmap(draw_bitmap, 0, 0, null);
            canvas.drawPath(path, paint); // 저장된 path 를 그려라
            //imageview.setImageBitmap(draw_bitmap);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y); // 자취에 그리지 말고 위치만 이동해라
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y); // 자취에 선을 그려라
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

            invalidate(); // 화면을 다시그려라

            return true;
        }

        public void reset() {
            path.reset();
            invalidate();
        }



    }
    public String saveBitmapToJpg(Bitmap bitmap, String name) {
        /**
         * 캐시 디렉토리에 비트맵을 이미지파일로 저장하는 코드입니다.
         *
         * @version target API 28 ★ API29이상은 테스트 하지않았습니다.★
         * @param Bitmap bitmap - 저장하고자 하는 이미지의 비트맵
         * @param String fileName - 저장하고자 하는 이미지의 비트맵
         *
         * File storage = 저장이 될 저장소 위치
         *
         * return = 저장된 이미지의 경로
         *
         * 비트맵에 사용될 스토리지와 이름을 지정하고 이미지파일을 생성합니다.
         * FileOutputStream으로 이미지파일에 비트맵을 추가해줍니다.
         */

        File storage = getCacheDir(); //  path = /data/user/0/YOUR_PACKAGE_NAME/cache
        String fileName = name + ".jpg";
        File imgFile = new File(storage, fileName);
        try {
            imgFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out); //썸네일로 사용하므로 퀄리티를 낮게설정
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
