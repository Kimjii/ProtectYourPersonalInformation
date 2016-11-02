package com.example.lgx.pypi;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

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
public class TakePictureActivity extends AppCompatActivity  {

    private Camera camera;
    Button button;
    private TimerTask mTask;
    private Timer mTimer;

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
        cameraPreview.getHolder().setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(shutterButtonListener);
    }

    private View.OnClickListener shutterButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            camera.takePicture(null, null, new Camera.PictureCallback(){
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    //SD카드에 저장 처리 호출
                    try{
                        saveSD(data);// SD카드에 저장
                    }catch(Exception e){
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
    private SurfaceHolder.Callback previewCallback = new SurfaceHolder.Callback2(){
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {

        }

        public void surfaceCreated(SurfaceHolder holder){
            //카메라 초기화
            try{
                int camNo = Camera.getNumberOfCameras();
                //Log.i("Number of cameras," + camNo);

                //카메라 오픈
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

                //미리보기 디스플레이 세트
                camera.setPreviewDisplay(holder);

                camera.setDisplayOrientation(90);

            }catch (IOException e){
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
            params.setPreviewSize( params.getPreviewSize().width, params.getPreviewSize().height );

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

    private void saveSD( byte[] data ) throws  Exception {
        // SD카드/패키지명 디렉터리 작성
        File dataDir = new File(Environment.getExternalStorageDirectory(), this.getPackageName());
        if( dataDir.exists() == false ){
            dataDir.mkdir();
        }

        // SD카드에 데이터 저장 파일명은 yyyyMMdd_HHmmss.jpg
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
        FileOutputStream fos = new FileOutputStream(dataDir + "/" + dateFormat.format(today) + ".jpg");
        fos.write(data);
        fos.close();
    }
}
