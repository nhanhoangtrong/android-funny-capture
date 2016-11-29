package com.example.nhanht.facedetection.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.nhanht.facedetection.views.GraphicOverlay;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by NhanHT on 11/18/2016.
 */

public class LandmarkGraphic extends GraphicOverlay.Graphic {

    public static final float EYE_WIDTH_PERCENT = 0.2f;
    public static final float NOSE_WIDTH_PERCENT = 0.2f;
    public static final float EAR_WIDTH_PERCENT = 0.1f;
    public static final float MOUNTH_WIDTH_PERCENT = 0.33f;

    public static final float EYE_HEIGHT_PERCENT = 0.11f;
    public static final float NOSE_HEIGHT_PERCENT = 0.33f;
    public static final float EAR_HEIGHT_PERCENT = 0.33f;
    public static final float MOUNTH_HEIGHT_PERCENT = 0.11f;

    protected Landmark mLandmark;
    protected float mWidth;
    protected float mHeight;
    protected float mFaceWidth;
    protected float mFaceHeight;
    protected Paint mPaint;

    public LandmarkGraphic(GraphicOverlay overlay) {
        super(overlay);
        mPaint = new Paint();
    }

    public void updateLandmark(Landmark landmark, float faceWidth, float faceHeight) {
        mLandmark = landmark;
        mWidth = 10.0f;
        mHeight = 10.0f;
        mFaceWidth = faceWidth;
        mFaceHeight = faceHeight;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Landmark landmark = mLandmark;
        if (landmark == null) {
            return;
        }
        PointF position = landmark.getPosition();
        float x = translateX(position.x);
        float y = translateY(position.y);
        canvas.drawCircle(x, y, 10.0f, mPaint);
    }
}
