package vqnguye2.inf_133_kitkat;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.media.AudioManager;

public class MainActivity extends Activity {
	private MediaPlayer md;
	private TextView mTextViewLightLabel;
	private AssetFileDescriptor[] sounds;
	private OrientationEventListener mOrientationEventListener;
	private int mOrientation;
	private int mOldOrientation;
	
	
	private boolean isFlat(int orientation) {
		return orientation == -1;
	}
	
	private boolean isUp(int orientation) {
		return orientation < 10 || orientation > 350;
	}
	
	private boolean isRight(int orientation) {
		return orientation < 100 && orientation > 80;
	}
	
	private boolean isLeft(int orientation) {
		return orientation < 280 && orientation > 260;
	}
	
	private boolean isDown(int orientation) {
		return orientation < 190 && orientation > 170;
	}
	
	
	private boolean shouldPlaySound() {
		return  (isFlat(mOrientation) && !isFlat(mOldOrientation)) || 
				(isUp(mOrientation) && !isUp(mOldOrientation)) ||
				(isRight(mOrientation) && !isRight(mOldOrientation)) ||
				(isLeft(mOrientation) && !isLeft(mOldOrientation)) ||
				(isDown(mOrientation) && !isDown(mOldOrientation));
	}
	
	private AssetFileDescriptor selectSound() {
		if (isFlat(mOrientation)){
			return sounds[0];
		}
		else if (isUp(mOrientation)) {
			return sounds[1];
		}
		else if (isRight(mOrientation)) {
			return sounds[2];
		}
		else if (isLeft(mOrientation)) {
			return sounds[4];
		}
		else {
			return sounds[3];
		}
	}
	
	synchronized void playAudio() {
		if (!md.isPlaying() && shouldPlaySound()) {
			AssetFileDescriptor afd = selectSound();
			try {
				updateUI();	
				md.reset();
				md.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				md.prepare();
				md.start();
			} catch (IllegalArgumentException e) {
				Log.d("playAudio:", "" + e  + "\n afd: "+ afd);
			} catch (IllegalStateException e) {
				Log.d("playAudio:", "" + e  + "\n afd: "+ afd);
			} catch (IOException e) {
				Log.d("playAudio:", "" + e  + "\n afd: "+ afd);
			}
		}
	}
	
	private void updateUI() {
		runOnUiThread(new Runnable(){
			public void run() {
				mTextViewLightLabel.setText(" " + mOldOrientation + " " + mOrientation);
			}
		});
	}
	
	
	public void onResume() {
		super.onResume();
		mOrientationEventListener.enable();
	}
	
	
	public void onStop() {
		mOrientationEventListener.disable();
		super.onStop();
	}
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextViewLightLabel = (TextView) findViewById(R.id.editText2);
        mOldOrientation = 230;
    	mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_FASTEST) {
			public void onOrientationChanged(int orientation) {
				mOldOrientation = mOrientation;
				mOrientation = orientation;
				playAudio();
			}
    	};
    	
        md = new MediaPlayer();
        sounds = new AssetFileDescriptor[5];
        sounds[0] = getApplicationContext().getResources().openRawResourceFd(R.raw.first);
		sounds[1] = getApplicationContext().getResources().openRawResourceFd(R.raw.second);
		sounds[2] = getApplicationContext().getResources().openRawResourceFd(R.raw.third);
		sounds[3] = getApplicationContext().getResources().openRawResourceFd(R.raw.fourth);
		sounds[4] = getApplicationContext().getResources().openRawResourceFd(R.raw.fifth);
    }
}
