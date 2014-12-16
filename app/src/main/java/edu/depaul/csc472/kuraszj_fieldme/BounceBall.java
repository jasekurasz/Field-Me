package edu.depaul.csc472.kuraszj_fieldme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jasekurasz on 11/20/14.
 */
public class BounceBall extends View {

    private int width, height;
    private boolean paused;
    Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.field);

    class MyShape {
        ShapeDrawable drawable;
        int dx = 10, dy = 0;
        MyShape(Shape shape) {
            drawable = new ShapeDrawable(shape);
        }
        MyShape() {}

        void move() {
            Rect bounds = drawable.getBounds();
            if (bounds.right >= width && dx > 0 || bounds.left < 0 && dx < 0) dx = -dx;
            if (bounds.bottom >= height && dy > 0 || bounds.top < 0 && dy < 0) dy = -dy;
            bounds.left += dx;
            bounds.right += dx;
            bounds.top += dy;
            bounds.bottom += dy;
        }

        void setBounds(int left, int top, int right, int bottom) {
            drawable.setBounds(left, top, right, bottom);
        }

        void setVelocity(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        void draw(Canvas canvas) { drawable.draw(canvas); }
    }

    class MyBitmap extends MyShape {
        Bitmap bitmap;
        int x, y;
        MyBitmap(int resId) {
            bitmap = BitmapFactory.decodeResource(getResources(), resId);

        }

        void setBounds(int left, int top, int right, int bottom) {
            x = left;
            y = top;
        }

        void move() {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if (x + w  >= width && dx > 0 || x < 0 && dx < 0) dx = -dx;
            if (y + h >= height && dy > 0 || y < 0 && dy < 0) dy = -dy;
            x += dx;
            y += 0;
        }

        void draw(Canvas canvas) {
            canvas.drawBitmap(bitmap, x, y, null);
        }

    }

    private MyShape shape = new MyBitmap(R.drawable.soccer_ball);

    private Handler mHandler = new Handler();

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BounceBall(Context context) {
        super(context);
    }

    public BounceBall(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void restart() {
        stopAnimation();
        positionShapes();
        startAnimation();
    }

    public void startAnimation() {
        paused = false;
        mHandler.post(update);
    }

    public void stopAnimation() {
        paused = true;
    }

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    @Override
    protected void onSizeChanged(int neww, int newh, int oldw, int oldh) {
        super.onSizeChanged(neww, newh, oldw, oldh);
        width = neww;
        height = newh;
        mBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, true);
        positionShapes();
    }

    private void positionShapes() {
        shape.setBounds(width, width, 0, 0);
        shape.setVelocity(10,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        paint.setColor(Color.BLACK);
        paint.setTextSize(200);
        paint.setFakeBoldText(true);
        paint.setShadowLayer(20, 0, 0, Color.WHITE);
        Rect bounds = new Rect();
        paint.getTextBounds("Field Me", 0, "Field Me".length(), bounds);
        int x = (canvas.getWidth() - bounds.width())/2;
        int y = (canvas.getHeight() - bounds.height())/6;
        canvas.drawText("Field Me", x, y, paint);
        shape.move();
        shape.draw(canvas);

        if (!paused) mHandler.postDelayed(update, 15);
    }

}
