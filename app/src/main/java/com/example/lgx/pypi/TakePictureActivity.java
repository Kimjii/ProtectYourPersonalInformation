package com.example.lgx.pypi;


import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Jiwon on 2016-11-01.
 */
public class TakePictureActivity extends AppCompatActivity {
    public static final int SEND_INFORMATION = 0;
    public static final int SEND_STOP = 1;

    private Camera camera;
    Button button;
    private TimerTask mTask;
    private Timer mTimer;

    Handler mHandler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        // 전체 화면 지정( 화면 상부의 아이콘이나 시계 X )
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 카메라 미리보기 화면 설정
        SurfaceView cameraPreview = (SurfaceView) this.findViewById(R.id.surfaceView);

        //서페이스 홀더 생성
        cameraPreview.getHolder().addCallback(previewCallback);

        // 서페이스 홀더 유형 설정 ( 외부 버퍼 사용 )
        cameraPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(shutterButtonListener);
    }

    private View.OnClickListener shutterButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    //SD카드에 저장 처리 호출
                    try {
                        saveGallery(data);// SD카드에 저장
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        mTask = new TimerTask() {
            @Override
            public void run() {
                button.callOnClick();
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 500);

    }

    // 서페이스 홀더의 callback() 메소드 구현
    private SurfaceHolder.Callback previewCallback = new SurfaceHolder.Callback2() {
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {

        }

        public void surfaceCreated(SurfaceHolder holder) {
            //카메라 초기화
            try {
                int camNo = Camera.getNumberOfCameras();
                //Log.i("Number of cameras," + camNo);

                //카메라 오픈
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

                //미리보기 디스플레이 세트
                camera.setPreviewDisplay(holder);

                camera.setDisplayOrientation(90);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // 미리보기 정지
            camera.stopPreview();

            //카메라 매개 변수 취득
            Camera.Parameters params = camera.getParameters();

            //매개 변수에 미리보기 크기 설정
            params.setPreviewSize(params.getPreviewSize().width, params.getPreviewSize().height);

            // 설정한 매개 변수를 지정
            camera.setParameters(params);

            //카메라 미리보기 시작
            camera.startPreview();
        }

        // 서페이스 해제 처리
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // 카메라 미리보기 정지
            camera.stopPreview();

            //카메라 릴리스
            camera.release();
        }
    };

    private void saveGallery(byte[] data) throws Exception {
        String saveFolderName = "PYPI";
        try {

            //메모리에 찍은 사진을 저장하기 위한 부분
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date currentDate = new Date();
            String dateString = formatter.format(currentDate);
            File sdCardPath = Environment.getExternalStorageDirectory();

            File dirs = new File(Environment.getExternalStorageDirectory(), saveFolderName);
            if (dirs.mkdirs()) {
                Log.d("CAMERA_TEST", "Directory Created");
            }

            FileOutputStream out = null;
            String pictureName = sdCardPath.getPath() + "/" + saveFolderName + "/pic" + dateString + ".jpg";
            out = new FileOutputStream(pictureName);
            out.write(data);
            out.close();

            // 갤러리에 저장된 이미지를 띄우기 위한 부분 <<<<<<<<<<<<<<<<<<<<<<<<<<안됨>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            MediaScanner scanner = MediaScanner.newInstance(TakePictureActivity.this);
            scanner.mediaScanning( Environment.getExternalStorageState() +"/" + saveFolderName );

        } catch (IOException e) {
            Log.e("CAMERA_TEST", "" + e.toString());
        }
    }

}
