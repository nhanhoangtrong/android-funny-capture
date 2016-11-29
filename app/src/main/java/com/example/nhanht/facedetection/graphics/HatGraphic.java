package com.example.nhanht.facedetection.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.nhanht.facedetection.R;
import com.example.nhanht.facedetection.views.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

/**
 * Created by NhanHT on 11/17/2016.
 */

public class HatGraphic extends GraphicOverlay.Graphic {

    private Context mContext;
    private Bitmap mBitmapHat;

    private volatile Face mFace;
    private Rect mRectHat;

    public HatGraphic(GraphicOverlay overlay) {
        super(overlay);
        mContext = overlay.getContext();
        mBitmapHat = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hat_small);
        mRectHat = new Rect(0, 0, mBitmapHat.getWidth(), mBitmapHat.getHeight());
    }

    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Compute hat base on face size
        float hatWidth = mBitmapHat.getWidth();
        float hatHeight = mBitmapHat.getHeight();
        float faceWidth = face.getWidth();
        float faceHeight = face.getHeight();

        hatHeight = scaleY(0.8f * faceWidth * 2 / hatWidth * hatHeight);
        hatWidth = scaleX(0.8f * faceWidth * 2);

        float x = translateX(face.getPosition().x - faceWidth / 5);
        float y = translateY(face.getPosition().y - faceHeight * 5 / 11);
        RectF dst = new RectF(x, y, x + hatWidth, y + hatHeight);
        canvas.drawBitmap(mBitmapHat, mRectHat, dst, null);
    }
}
