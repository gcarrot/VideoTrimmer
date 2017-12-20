package si.gcarrot.videotrimmer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;


/*
    Video player with custom and Media Controller buttons.
 */

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private int PICK_VIDEO_SAMPLE = 100;
    MediaMetadataRetriever retriever;

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Buttons
        Button btnPlay = this.findViewById(R.id.btnPlay);
        Button btnPause = this.findViewById(R.id.btnPause);
        Button btnSelect = this.findViewById(R.id.btnSelect);
        Button btnTrimm = this.findViewById(R.id.btnTrim);

        // Seek bar
        seekBar = this.findViewById(R.id.seekBar);

        final TextView seekBarValue = findViewById(R.id.seekBarValue);
        seekBarValue.setGravity(Gravity.CENTER);

        retriever = new MediaMetadataRetriever();

        //Create VideoView
       videoView = this.findViewById(R.id.videoView);

        //Create MediaController
        final MediaController mc  = new MediaController(this);

        videoView.setMediaController(mc);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            private int milSec = 0;
            private int sec = 0;
            private int min = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                milSec = progress / 100;
                sec = milSec / 10;
                min = sec / 60;

                if(milSec > 99){
                    milSec = milSec / sec;
                }
                if(sec > 59) {
                    min += 1;
                    sec = sec / min;
                }

                seekBarValue.setText(getTime(sec));
                //seekBarValue.setText(String.valueOf(min) + ":" + String.valueOf(sec));

                if(fromUser){
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mc.hide();
                videoView.start();
                seekBar.postDelayed(onEverySecond, 1000);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mc.hide();
                videoView.pause();
            }
        });



        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_SAMPLE);
            }
        });

        btnTrimm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Trimmer.class);
                startActivity(intent);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_VIDEO_SAMPLE) {
                //TODO: action
                Uri selectedMediaUri = data.getData();

                retriever.setDataSource(this, selectedMediaUri);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                seekBar.setMax(Integer.parseInt(time));

                videoView.setVideoURI(selectedMediaUri);
                videoView.start();
                seekBar.postDelayed(onEverySecond, 1000);
            }
        }
    }


    private Runnable onEverySecond = new Runnable() {

        @Override
        public void run() {
        if (seekBar != null) {
            seekBar.setProgress(videoView.getCurrentPosition());
        }

        if (videoView.isPlaying()) {
            int mCurrentPosition = videoView.getCurrentPosition();
            seekBar.setProgress(mCurrentPosition);
            seekBar.postDelayed(onEverySecond, 1000);
        }
        }
    };

    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }
}
