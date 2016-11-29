package com.example.nhanht.facedetection;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.nhanht.facedetection.fragments.ImageViewDialogFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private GridView mImageGallery;
    private ImageGalleryAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mImageGallery = (GridView) findViewById(R.id.gvImageGallery);
        mImageAdapter = new ImageGalleryAdapter();
        mImageGallery.setAdapter(mImageAdapter);
        mImageGallery.setOnItemClickListener(this);
        mImageGallery.setOnItemLongClickListener(this);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = mImageAdapter.getItem(position);
        FragmentManager fm = getSupportFragmentManager();
        ImageViewDialogFragment dialog = ImageViewDialogFragment.instantiate(this,
                "imgFragDialog", file.getAbsolutePath());
        dialog.show(fm, "imgFragDialog");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        File imgFile = mImageAdapter.mImageFiles.remove(position);
        // Remove image from external
        imgFile.delete();
        mImageAdapter.notifyDataSetChanged();
        return true;
    }

    private class ImageGalleryAdapter extends BaseAdapter {

        List<File> mImageFiles;
        int mImageWidth;
        int mImageHeight;
        ImageGalleryAdapter() {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Log.d("gallery", dir.getAbsolutePath());
            mImageFiles = new ArrayList<>();
            File[] files = dir.listFiles();
            for (File file: files) {
                Log.d("gallery", file.getName());
                if (file.isFile() && getFileExtension(file).equals("jpg")) {
                    mImageFiles.add(file);
                }
            }
            mImageWidth = (int) getResources().getDimension(R.dimen.image_thumbnail_width);
            mImageHeight = (int) getResources().getDimension(R.dimen.image_thumbnail_height);
        }

        @Override
        public int getCount() {
            return mImageFiles.size();
        }

        @Override
        public File getItem(int position) {
            return mImageFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ImageView imView = new ImageView(parent.getContext());
                imView.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                250));
                imView.setPadding(10, 10, 10, 10);
                imView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                convertView = imView;
            }
            Picasso.with(GalleryActivity.this)
                    .load(mImageFiles.get(position).getAbsoluteFile())
                    .resize(mImageWidth, mImageHeight)
                    .centerCrop()
                    .into((ImageView)convertView);
            return convertView;
        }
    }
}
