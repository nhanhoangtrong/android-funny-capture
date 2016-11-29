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

public class GlassesGraphic extends GraphicOverlay.Graphic {

    private Bitmap mGlassesBitmap;
    private Context mContext;
    private Rect mGlassesRect;
    private Landmark mLeftEye;
    private Landmark mRightEye;
    private float mFaceWidth;
    private float mFaceHeight;
    private float mEulerZ;
    private float mEulerY;

    public GlassesGraphic(GraphicOverlay overlay) {
        super(overlay);
        mContext = overlay.getContext();
        mGlassesBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .sunglasses);
        mGlassesRect = new Rect(0, 0, mGlassesBitmap.getWidth(), mGlassesBitmap.getHeight());
    }

    public void updateLeftEye(Landmark leftEye, float faceWidth, float
            faceHeight) {
        mLeftEye = leftEye;
        mFaceWidth = faceWidth;
        mFaceHeight = faceHeight;
        postInvalidate();
    }

    public void updateRightEye(Landmark rightEye, float faceWidth, float
            faceHeight) {
        mRightEye = rightEye;
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
        Landmark leftEye = mLeftEye;
        Landmark rightEye = mRightEye;
        if (leftEye == null || rightEye == null) {
            return;
        }

        float x1 = translateX(rightEye.getPosition().x);
        float x2 = translateX(leftEye.getPosition().x);
        float y = translateY((leftEye.getPosition().y + rightEye.getPosition().y) / 2);
        float width = x2 - x1;
        float height = scaleY(2 * mFaceHeight / 9);

        canvas.rotate(-mEulerZ, x1 / 2 + x2 / 2, y);
        canvas.drawBitmap(mGlassesBitmap, mGlassesRect, new RectF(x1 - width / 2, y - height / 2,
                x2 + width / 2, y + height / 2), null);
        canvas.rotate(mEulerZ, x1 / 2 + x2 / 2, y);
    }
}
