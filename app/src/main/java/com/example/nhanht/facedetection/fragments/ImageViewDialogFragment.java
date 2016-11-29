package com.example.nhanht.facedetection.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;

import com.example.nhanht.facedetection.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.example.nhanht.facedetection.R.id.imgView;

/**
 * Created by NhanHT on 11/29/2016.
 */

public class ImageViewDialogFragment extends DialogFragment {

    public static ImageViewDialogFragment instantiate(Context context, String fname, String
            imgPath) {
        ImageViewDialogFragment imgFrag = new ImageViewDialogFragment();
        Bundle args = new Bundle();
        args.putString("imagePath", imgPath);
        imgFrag.setArguments(args);
        return imgFrag;
    }

    String mImagePath;
    ImageView mImageView;
    int mImageWidth;
    int mImageHeight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.);
        mImageWidth = (int) getResources().getDimension(R.dimen.image_view_width);
        mImageHeight = (int) getResources().getDimension(R.dimen.image_view_height);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.dialog_image_view);
        mImageView = (ImageView) dialog.findViewById(imgView);
        mImagePath = getArguments().getString("imagePath", "");
        if (!mImagePath.equals("")) {
//            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imgPath));
//                imgView.setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

        }
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        Picasso.with(getDialog().getContext())
                .setLoggingEnabled(true);
        Picasso.with(getDialog().getContext())
                .load(new File(mImagePath))
                .resize(mImageWidth, mImageHeight)
                .centerInside()
                .into(mImageView);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
