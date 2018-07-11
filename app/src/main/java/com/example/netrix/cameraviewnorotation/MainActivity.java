package com.example.netrix.cameraviewnorotation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    Context context;
    private ImageView capturedPhoto;
    private static final int TAKE_PICTURE = 1;
    private static final int REQUEST_READ_PERMISSION = 100;
    private Uri capturedImageUri;
    private String selectedImagePath;
    private Bitmap bitmap;
    private ExifInterface exifObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        capturedPhoto = (ImageView) findViewById(R.id.imageView);
        capturedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callbackIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(callbackIntent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK && data != null){
                Bitmap photo =(Bitmap) data.getExtras().get("data");
                createDirectoryAndSaveFile(photo,"tempImage.jpg");
//                Uri imageUri = data.getData();
//                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
//                Cursor cur = managedQuery(imageUri, orientationColumn, null, null, null);
//                int orientation = -1;
//                if (cur != null && cur.moveToFirst()) {
//                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
//                }
//                Matrix matrix = new Matrix();
//                matrix.postRotate(orientation);
//                capturedPhoto.setImageMatrix(matrix);

            }
        }

//        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
//            String result = data.toURI();
//            capturedImageUri = data.getData();
////            try {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
//                } else {
////                    selectedImagePath = getRealPathFromURIPath(capturedImageUri, MainActivity.this);
////                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), capturedImageUri);
////                    capturedPhoto.setImageBitmap(exifRotateImage(capturedImageUri));
//                    createDirectoryAndSaveFile((Bitmap) data.getExtras().get("data"),"tempImage.jpg");
////                    capturedPhoto.setImageBitmap(bitmap);
//                }
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//            Log.d(TAG, "Image path return" + result);
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_READ_PERMISSION) {
//            if (grantResults.length == 0) {
//                // permission denied
//            }else{
//                // permission granted
//            }
//        }
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(capturedImageUri != null){
//            try {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
//                } else {
////                    selectedImagePath = getRealPathFromURIPath(capturedImageUri, MainActivity.this);
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), capturedImageUri);
////                    capturedPhoto.setImageBitmap(bitmap);
//                    capturedPhoto.setImageBitmap(exifRotateImage(capturedImageUri));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/DirName");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/DirName/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/DirName/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri uri = Uri.fromFile(file);
//        try{
//            exifRotateImage(uri);
//            capturedPhoto.setImageBitmap(rotateImageIfRequired(imageToSave,context,uri));
//        } catch (IOException e){
//            Log.d(TAG,"ioException");
//        }

        Log.d(TAG,"uri = "+uri);
        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Log.d(TAG,"rotation in degrees = "+rotationInDegrees);

            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            Bitmap adjustedBitmap = Bitmap.createBitmap(imageToSave, 0, 0, imageToSave.getWidth(), imageToSave.getHeight(), matrix, true);
            capturedPhoto.setImageBitmap(adjustedBitmap);

        } catch (IOException e){
            Log.d(TAG,"ioException");
        }






    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }


    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {

        if (selectedImage.getScheme().equals("content")) {
            String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };
            Log.d(TAG,"rotateImageIfRequired projection = "+projection);
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            Log.d(TAG,"exinterface = "+ei.getAttribute(ExifInterface.TAG_ORIENTATION).toString());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d(TAG,  "orientation: "+ orientation);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    private Bitmap exifRotateImage(Uri photoPath){
        try {
            ExifInterface ei = new ExifInterface(photoPath.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap,  (float) 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, (float) 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, (float) 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e ){

        }
        return null;
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
