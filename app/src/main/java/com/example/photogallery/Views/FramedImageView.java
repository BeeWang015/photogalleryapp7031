package com.example.photogallery.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.photogallery.R;

public class FramedImageView extends View {
    private int mExampleColor = Color.RED;
    private Bitmap mBitmap;
    private float mExampleDimension = 0;
public void setMExampleDimension(float mExampleDimension) {
    this.mExampleDimension = mExampleDimension;
}
public float getMExampleDimension() {
    return mExampleDimension;
}
    public FramedImageView(Context context) {
        super(context);
        init(null, 0);
    }
    public FramedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public FramedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.FramedImageView, defStyle, 0);
        mExampleColor = a.getColor(
                R.styleable.FramedImageView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.FramedImageView_exampleDimension,
                mExampleDimension);
        a.recycle();
    }
    public void setContent(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            double scale = drawBitmap(canvas);
            drawImageBorder(canvas);
        }
    }
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }
    private void drawImageBorder(Canvas canvas) {
        Paint paint = new Paint();
        int borderColour = mExampleColor;
        paint.setColor(borderColour);
        paint.setStyle(Paint.Style.STROKE);
        int stroke = (int) mExampleDimension;
        paint.setStrokeWidth(stroke);
        float viewWidth = canvas.getWidth();
        float viewHeight = canvas.getHeight();
        float imageWidth = mBitmap.getWidth();
        float imageHeight = mBitmap.getHeight();
        float scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
        float left = stroke / 2;
        float top = stroke / 2;
        float right = (imageWidth * scale) - (stroke / 2);
        float bottom = (imageHeight * scale) - (stroke / 2);
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
