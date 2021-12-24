package com.example.myalarm2;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
//
//20185542 채희선 모바일 앱 개발 Final Project //

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;






public class AlarmActivity extends AppCompatActivity  {

    private MediaPlayer mediaPlayer;


    //
    //
    Intent intent;
    SpeechRecognizer mRecognizer;
    Button sttBtn;
    TextView textView;
    final int PERMISSION = 1;

    //
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // 알람음 재생
        this.mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        this.mediaPlayer.start();

        findViewById(R.id.btnClose).setOnClickListener(mClickListener);

        //
        //***************************


        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        textView = (TextView)findViewById(R.id.sttResult);
        sttBtn = (Button) findViewById(R.id.sttStart);

        //사용자에게 음성을 요구하고 음성 인식기를 통해 전송하는 활동을 시작.
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //음성 인식을 위한 음석 인식기의 의도에 사용되는 여분의 키.
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        //음성을 번역할 언어를 설정.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        sttBtn.setOnClickListener(v -> {
            //새 sppechRecognizer를 만드는 펙토리 메소드.
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            //모든 콜백을 수신하는 리스너를 설정. 리스너는 아래에서 정의함.
            mRecognizer.setRecognitionListener(listener);
            //내 목스리 듣기 시작.
            mRecognizer.startListening(intent);
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // MediaPlayer release
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    /* 알람 종료 */
    private void close() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }

        finish();
    }

    /////

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnClose:
                    //Toast.makeText(getApplicationContext(),"말한거:"+textView.getText(),Toast.LENGTH_SHORT).show();
                    if(textView.getText().equals("오늘도 즐거운 하루가 되자")){
                        Toast.makeText(getApplicationContext(),"종료",Toast.LENGTH_SHORT).show();
                        // 알람 종료
                        close();
                        break;

                    }
                    else
                        Toast.makeText(getApplicationContext(),"다시",Toast.LENGTH_SHORT).show();

            }
        }
    };



    private RecognitionListener listener = new RecognitionListener() {
        //사용자가 말하기 시작할 준비가 되면 호출 됨.
        //음석인식을 시작하는 알림 알려줌.
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }

        //사용자가 말하기 시작했을 때 호출됨.
        @Override
        public void onBeginningOfSpeech() {}

        //입력받는 소리의 크기를 알려줌.
        @Override
        public void onRmsChanged(float rmsdB) {}

        //사용자가 말을 시작하고 인식이 된 단어를 버퍼에 담음.
        @Override
        public void onBufferReceived(byte[] buffer) {}

        //사용자가 말하기를 중지하면 호출됨.
        @Override
        public void onEndOfSpeech() {}


        //네트워크 또는 인식 오류가 발생했을 때 호출됨.
        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        //인실 결과가 준비되면 호출됨.
        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                textView.setText(matches.get(i));
            }
            //Toast.makeText(getApplicationContext(),"인식된 문장:"+ matches, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(),"인식된 문장2:"+ (String)textView.getText(), Toast.LENGTH_SHORT).show();
        }

        //부분 인식 결과를 사용할 수 있을 때 호출됨.
        @Override
        public void onPartialResults(Bundle partialResults) {}

        //향후 이벤트 추가하기 위해 예약됨.
        @Override
        public void onEvent(int eventType, Bundle params) {}
    };




}