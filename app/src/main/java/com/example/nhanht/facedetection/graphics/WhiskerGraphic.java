package com.example.nhanht.facedetection.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.nhanht.facedetection.R;
import com.example.nhanht.facedetection.views.GraphicOverlay;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by NhanHT on 11/21/2016.
 */

public class WhiskerGraphic extends GraphicOverlay.Graphic {

    private Landmark mLeftMouth;
    private Landmark mRightMouth;
    private float mFaceWidth;
    private float mFaceHeight;
    private Rect mWhiskerRect;
    private Bitmap mWhiskerBitmap;

    private float mEulerZ;
    private float mEulerY;

    public WhiskerGraphic(GraphicOverlay overlay) {
        super(overlay);
        Context context = overlay.getContext();
        mWhiskerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.whisker);
        mWhiskerRect = new Rect(0, 0, mWhiskerBitmap.getWidth(), mWhiskerBitmap.getHeight());
    }

    public void updateLeftMouth(Landmark leftMouth, float faceWidth, float faceHeight) {
        mLeftMouth = leftMouth;
        mFaceWidth = faceWidth;
        mFaceHeight = faceHeight;
        postInvalidate();
    }

    public void updateRightMouth(Landmark rightMouth, float faceWidth, float faceHeight) {
        mRightMouth = rightMouth;
        mFaceWidth = faceWidth;
        mFaceHeight = faceHeight;
        postInvalidate();
    }

    public void updateEulerYZ(float eulerY, float eulerZ) {
        mEulerY = eulerY;
        mEulerZ = eulerZ;
    }

    @Override
    public void draw(Canvas canvas) {
        Landmark leftMouth = mLeftMouth;
        Landmark rightMouth = mRightMouth;
        if (leftMouth == null || rightMouth == null) {
            return;
        }
        float x1 = translateX(rightMouth.getPosition().x);
        float x2 = translateX(leftMouth.getPosition().x);
        float y = translateY((rightMouth.getPosition().y + leftMouth.getPosition().y) / 2 -
                mFaceHeight / 18);
        float width = (x2 - x1) * 1.2f;
        float height = mWhiskerBitmap.getHeight() * (x2 - x1) / mWhiskerBitmap.getWidth();

        canvas.rotate(-mEulerZ, x1 / 2 + x2 / 2, y);
        canvas.drawBitmap(mWhiskerBitmap, mWhiskerRect, new RectF(x1 - 0.1f * width, y, x2 + 0.1f
                * width, y + height), null);
        canvas.rotate(mEulerZ, x1 / 2 + x2 / 2, y);
    }
}
