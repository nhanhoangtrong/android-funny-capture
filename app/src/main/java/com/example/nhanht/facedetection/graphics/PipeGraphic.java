package com.example.nhanht.facedetection.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.example.nhanht.facedetection.R;
import com.example.nhanht.facedetection.views.GraphicOverlay;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by NhanHT on 11/21/2016.
 */

public class PipeGraphic extends LandmarkGraphic {

    private Bitmap mPipeBitmap;
    private Context mContext;
    private Rect mPipeRect;

    public PipeGraphic(GraphicOverlay overlay) {
        super(overlay);
        mContext = overlay.getContext();
        mPipeBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pipe);
        mPipeRect = new Rect(0, 0, mPipeBitmap.getWidth(), mPipeBitmap.getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        Landmark landmark = mLandmark;
        if (landmark == null) {
            return;
        }

        PointF position = landmark.getPosition();
        float pipeWidth = mPipeBitmap.getWidth();
        float pipeHeight = mPipeBitmap.getHeight();
        float height = scaleY((mFaceWidth / 3) / pipeWidth * pipeHeight);
        float width = scaleY(mFaceWidth / 3);
        Log.d("PipeGraphic", "width: " + width + ", height: " + height);
        float x = translateX(position.x) - 4 * width / 5;
        float y = translateY(position.y);
        canvas.drawBitmap(mPipeBitmap, mPipeRect, new RectF(x, y, x + width, y + height), null);
    }
}
