package com.example.drawing_test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class drawActivity extends AppCompatActivity {
    private DrawCanvas drawCanvas;
    private FloatingActionButton fbPen;
    private FloatingActionButton fbEraser;
    private FloatingActionButton fbSave;

    private ConstraintLayout canvasContainer;
    private int height;
    private int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Intent intent = getIntent();
        height = intent.getIntExtra("h",0);
        width = intent.getIntExtra("w",0);
        canvasContainer = new ConstraintLayout(this);
        findId();
        canvasContainer.addView(drawCanvas);
        setOnClickListener();
    }


    private void findId() {
        canvasContainer = (ConstraintLayout)findViewById(R.id.lo_canvas);
        fbPen = findViewById(R.id.fb_pen);
        fbEraser = findViewById(R.id.fb_eraser);
        fbSave = findViewById(R.id.fb_save);

        drawCanvas = new DrawCanvas(this);


    }


    private void setOnClickListener() {
        fbPen.setOnClickListener((v)->{
            drawCanvas.changeTool(DrawCanvas.MODE_PEN);
        });

        fbEraser.setOnClickListener((v)->{
            //drawCanvas.changeTool(DrawCanvas.MODE_ERASER);
            drawCanvas.init();
            drawCanvas.invalidate();
        });

        fbSave.setOnClickListener((v)->{
            drawCanvas.invalidate();
            Bitmap saveBitmap = drawCanvas.getCurrentCanvas();
            CanvasIO.saveBitmap(this, saveBitmap);
            saveBitmapToJpg(saveBitmap,"mask");
        });


    }


    class Pen {
        public static final int STATE_START = 0;        //펜의 상태(움직임 시작)
        public static final int STATE_MOVE = 1;         //펜의 상태(움직이는 중)
        float x, y;                                     //펜의 좌표
        int moveStatus;                                 //현재 움직임 여부
        int color;                                      //펜 색
        int size;                                       //펜 두께

        public Pen(float x, float y, int moveStatus, int color, int size) {
            this.x = x;
            this.y = y;
            this.moveStatus = moveStatus;
            this.color = color;
            this.size = size;
        }


        public boolean isMove() {
            return moveStatus == STATE_MOVE;
        }
    }


    class DrawCanvas extends View {
        public static final int MODE_PEN = 1;                     //모드 (펜)
        public static final int MODE_ERASER = 0;                  //모드 (지우개)
        final int PEN_SIZE = 30;                                  //펜 사이즈
        final int ERASER_SIZE = 30;                               //지우개 사이즈

        ArrayList<Pen> drawCommandList;                           //그리기 경로가 기록된 리스트
        Paint paint;                                              //펜
        Bitmap loadDrawImage;                                     //호출된 이전 그림
        int color;                                                //현재 펜 색상
        int size;                                                 //현재 펜 크기

        public DrawCanvas(Context context) {
            super(context);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            drawCommandList = new ArrayList<>();
            loadDrawImage = null;
            color = Color.WHITE;
            size = PEN_SIZE;
        }


        public Bitmap getCurrentCanvas() {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Log.d("width","width = "+this.getWidth());
            Log.d("height","height = "+this.getHeight());
            Log.d("imgwidth","width = "+width);
            Log.d("imgheight","height = "+height);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);
            return bitmap;
        }


        private void changeTool(int toolMode) {
            if (toolMode == MODE_PEN) {
                this.color = Color.WHITE;
                size = PEN_SIZE;
            } else {
                this.color = Color.WHITE;
                size = ERASER_SIZE;
            }
            paint.setColor(color);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.drawColor(Color.WHITE);

            if (loadDrawImage != null) {
                canvas.drawBitmap(loadDrawImage, 0, 0, null);
            }

            for (int i = 0; i < drawCommandList.size(); i++) {
                Pen p = drawCommandList.get(i);
                paint.setColor(p.color);
                paint.setStrokeWidth(p.size);

                if (p.isMove()) {
                    Pen prevP = drawCommandList.get(i - 1);
                    canvas.drawLine(prevP.x, prevP.y, p.x, p.y, paint);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            int action = e.getAction();
            int state = action == MotionEvent.ACTION_DOWN ? Pen.STATE_START : Pen.STATE_MOVE;
            drawCommandList.add(new Pen(e.getX(), e.getY(), state, color, size));
            invalidate();
            return true;
        }
    }

    public String saveBitmapToJpg(Bitmap bitmap , String name) {

        File storage = getCacheDir(); //  path = /data/user/0/package_name/cache
        String fileName = name + ".jpg";
        File imgFile = new File(storage, fileName);
        try {
            imgFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
            out.close();

        } catch (FileNotFoundException e) {
            Log.e("saveBitmapToJpg","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmapToJpg","IOException : " + e.getMessage());
        }
        Log.d("imgPath" , getCacheDir() + "/" +fileName);
        return getCacheDir() + "/" +fileName;
    }
}