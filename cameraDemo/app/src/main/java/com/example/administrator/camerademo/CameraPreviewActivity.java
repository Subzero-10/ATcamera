package com.example.administrator.camerademo;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.TextureView;

import static android.content.ContentValues.TAG;

public class CameraPreviewActivity extends Activity {
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private DemoCamera mCamera;
    private AutoFitTextureView mTextureView;
    //public ImageReader.OnImageAvailableListener mOnImageAvailableListener ;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d("CameraDemo", "onSurfaceTextureAvailable, width: " + width + ", height: " + height);
            startCameraPreview(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d("CameraDemo", "onSurfaceTextureSizeChanged, width: " + width + ", height: " + height);
            mCamera.configureTransform(CameraPreviewActivity.this , width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.d("CameraDemo", "onSurfaceTextureDestroyed");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            Log.d("CameraDemo", "onSurfaceTextureUpdated");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        mTextureView = findViewById(R.id.texture);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        startBackgroundThread();
        mCamera = new DemoCamera(mOnImageAvailableListener, mCameraHandler, mTextureView);

        if (mTextureView.isAvailable()) {
            Log.d(TAG, "到这里了");
            startCameraPreview(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            Log.d(TAG, "没到这里");
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        mCamera.shutDown();
        stopBackgroundThread();
    }

    private void startCameraPreview(int width, int height) {
        mCamera.setUpCameraOutputs(this, width, height);
        mCamera.configureTransform(this, width, height);
        mCamera.openCamera(this);
    }

    private void startBackgroundThread() {
        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    private void stopBackgroundThread() {
        mCameraThread.quitSafely();
        try {
            mCameraThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        /**
         *  当有一张图片可用时会回调此方法，但有一点一定要注意：
         *  一定要调用 reader.acquireNextImage()和close()方法，否则画面就会卡住！！！！！我被这个坑坑了好久！！！
         *    很多人可能写Demo就在这里打一个Log，结果卡住了，或者方法不能一直被回调。
         **/
        @Override
        public void onImageAvailable(ImageReader reader) {
/*            Image img = reader.acquireNextImage();
            *//**
             *  因为Camera2并没有Camera1的Priview回调！！！所以该怎么能到预览图像的byte[]呢？就是在这里了！！！我找了好久的办法！！！
             **//*
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            img.close();*/
        }
    };
/*    private void mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
            Log.d(TAG, "Image available now")
        // Do whatever you want here with the new still picture.
        *//*
        val image = reader.acquireLatestImage()
        val imageBuf = image.planes[0].buffer
        val imageBytes = ByteArray(imageBuf.remaining())
        imageBuf.get(imageBytes)
        image.close()
        Log.d(TAG, "Still image size: ${imageBytes.size}")
        *//*
    }*/
}
