package com.example.nhanht.facedetection.trackers;

import com.example.nhanht.facedetection.graphics.GlassesGraphic;
import com.example.nhanht.facedetection.graphics.HatGraphic;
import com.example.nhanht.facedetection.graphics.PipeGraphic;
import com.example.nhanht.facedetection.graphics.WhiskerGraphic;
import com.example.nhanht.facedetection.views.GraphicOverlay;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;


/**
 * Face tracker for each detected individual. This maintains a face graphic within the app's
 * associated face overlay.
 */

public class GraphicFaceTracker extends Tracker<Face> {
//    private FaceGraphic mFaceGraphic;
    private GraphicOverlay mOverlay;
    private HatGraphic mHatGraphic;

    private PipeGraphic mPipeGraphic;
    private WhiskerGraphic mWhiskerGraphic;

    private GlassesGraphic mGlassesGraphic;

    public GraphicFaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
//        mFaceGraphic = new FaceGraphic(overlay);
        mHatGraphic = new HatGraphic(overlay);
        mPipeGraphic = new PipeGraphic(overlay);
        mGlassesGraphic = new GlassesGraphic(overlay);
        mWhiskerGraphic = new WhiskerGraphic(overlay);
    }

    /**
     * Start tracking the detected face instance within the face overlay.
     */
    @Override
    public void onNewItem(int faceId, Face item) {
//        mFaceGraphic.setId(faceId);

    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
//        mOverlay.add(mFaceGraphic);
//        mFaceGraphic.updateFace(face);
        mOverlay.add(mHatGraphic);
        mHatGraphic.updateFace(face);


        List<Landmark> landmarks = face.getLandmarks();
        float faceWidth = face.getWidth();
        float faceHeight = face.getHeight();
        int lmSize = landmarks.size();
        for (int i = 0; i < lmSize; ++i) {
            Landmark lm = landmarks.get(i);
            // Filter by left eye and right eye
            if (lm.getType() == Landmark.LEFT_EYE) {

                mGlassesGraphic.updateLeftEye(landmarks.get(i), faceWidth, faceHeight);

            } else if (lm.getType() == Landmark.RIGHT_EYE) {

                mGlassesGraphic.updateRightEye(landmarks.get(i), faceWidth, faceHeight);
                mGlassesGraphic.updateEulerYZ(face.getEulerY(), face.getEulerZ());
                mOverlay.add(mGlassesGraphic);
            }
            else if (lm.getType() == Landmark.RIGHT_MOUTH) {
                mOverlay.add(mPipeGraphic);
                mPipeGraphic.updateLandmark(landmarks.get(i), faceWidth, faceHeight);

                mWhiskerGraphic.updateRightMouth(landmarks.get(i), faceWidth, faceHeight);
            } else if (lm.getType() == Landmark.LEFT_MOUTH) {
                mWhiskerGraphic.updateLeftMouth(landmarks.get(i), faceWidth, faceHeight);
                mWhiskerGraphic.updateEulerYZ(face.getEulerY(), face.getEulerZ());
                mOverlay.add(mWhiskerGraphic);
            }
        }
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        removeFromOverlay();
    }

    private void removeFromOverlay() {
//        mOverlay.remove(mFaceGraphic);
        mOverlay.remove(mHatGraphic);
        mOverlay.remove(mPipeGraphic);
        mOverlay.remove(mGlassesGraphic);
        mOverlay.remove(mWhiskerGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        removeFromOverlay();
    }
}
