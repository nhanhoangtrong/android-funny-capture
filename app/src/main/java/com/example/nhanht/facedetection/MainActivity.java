package com.example.nhanht.facedetection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.nhanht.facedetection.trackers.GraphicFaceTracker;
import com.example.nhanht.facedetection.views.CameraSourcePreview;
import com.example.nhanht.facedetection.views.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private final int RC_HANDLE_CAMERA_PERM = 2;
    private final int RC_HANDLE_GMS = 9001;

    CameraSourcePreview mCameraSourcePreview;
    CameraSource mCameraSource = null;
    GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraSourcePreview = (CameraSourcePreview) findViewById(R.id.cameraPreview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.graphicOverlay);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[] {Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not available");
            return;
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        // check that the device has play services available
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS).show();
        }

        if (mCameraSource != null) {
            try {
                mCameraSourcePreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * Restart the camera
     */
    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera
     */
    @Override
    public void onPause() {
        super.onPause();
        mCameraSourcePreview.stop();
    }

    /**
     * Release the resources associated with the camera source, the associated detector,
     * and the rest of the processing pipeline
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
            " Result code: " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        new AlertDialog.Builder(this)
                .setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    public void onClickCapture(View view) {
        if (mCameraSource != null) {
            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes) {
                    float rotation = getResources().getConfiguration().orientation ==
                            Configuration.ORIENTATION_PORTRAIT ? 90.0f : 0.0f;
                    String time = Long.toString(System.currentTimeMillis());
                    File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            , "test_" + time + ".jpg");

                    Bitmap receivedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap drawableBitmap = Bitmap.createBitmap(receivedBitmap.getHeight(),
                            receivedBitmap.getWidth(), Bitmap.Config.ARGB_8888);

                    // Rotate image
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(receivedBitmap,
                            0, 0, receivedBitmap.getWidth(),
                            receivedBitmap.getHeight(),
                            rotateMatrix, false);

                    // Draw rotated image to canvas
                    Canvas canvas = new Canvas(drawableBitmap);
                    canvas.drawBitmap(rotatedBitmap, 0, 0, null);
                    mGraphicOverlay.draw(canvas);
                    try {
                        // Save processed bitmap to external storage
                        FileOutputStream fout = new FileOutputStream(imageFile);
                        drawableBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                        fout.close();
                        Log.d(TAG, "Capture image successfully " + imageFile.getAbsolutePath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onClickSwitch(View view) {
    }

    public void onClickGallery(View view) {
        Intent i = new Intent(this, GalleryActivity.class);
        startActivity(i);
    }

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }
}
