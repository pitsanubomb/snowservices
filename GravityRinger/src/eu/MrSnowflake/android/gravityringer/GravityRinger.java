package eu.MrSnowflake.android.gravityringer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GravityRinger extends Activity  implements SensorListener {
	public static final String TAG = "GravityRinger";
	
	private static final int MODE_NOISY = 0;
	private static final int MODE_SILENCE = 1;
	
	private static final int MENU_CANCEL = Menu.FIRST + 1;
	private static final int MENU_REVERT = Menu.FIRST + 2;
	
	public class Preferences
	{
		public static final String PREFS_NAME = "GravityRinger";
		public static final String DELAY = "DelayMilis";
		public static final String SILENCE_GRAVITY = "SilenceGravity";
		public static final String NOISY_GRAVITY = "NoisyGravity";
		public static final String AUTO_START = "AutoStart";
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		//to be deleted in final
        // always use simulator when in emulator 
        // Android sensor Manager
    	mSensorMgr = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
	    
	    if (!mSensorMgr.registerListener(this, SensorManager.SENSOR_ORIENTATION))
	    {
	    	Log.e(TAG, "Could not register a listener for SENSOR_ORIENTATION");
	    	new AlertDialog.Builder(this)
	    	.setTitle(R.string.alert_sensors_orientation_title)
	    	.setMessage(R.string.alert_sensors_orientation_not_available)
	    	.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setNegativeButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GravityRinger.this.finish();
				}
	    	})
	    	.setCancelable(false)
	    	.show();
	    }
        
	    layMain = (LinearLayout)findViewById(R.id.layMain);
        txtDelay = (EditText)findViewById(R.id.txtDelay);
        txtNoisyThreshold = (EditText)findViewById(R.id.txtNoisyThreshold);
        txtSilenceThreshold = (EditText)findViewById(R.id.txtSilenceThreshold);
		chkAutoStart = (CheckBox)findViewById(R.id.chkAutoStart);

        mSettings = getSharedPreferences(Preferences.PREFS_NAME, 0);
        initSettings();

		btnSetSilence = (Button)findViewById(R.id.btnSilenceThreshold);
		btnSetSilence.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mReadSensor = true;
				mReadMode = MODE_SILENCE;
				btnSetSilence.setEnabled(false);
				btnSetNoisy.setEnabled(false);
			}
		});
		btnSetNoisy = (Button)findViewById(R.id.btnNoisyThreshold);
		btnSetNoisy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mReadSensor = true;
				mReadMode = MODE_NOISY;
				btnSetSilence.setEnabled(false);
				btnSetNoisy.setEnabled(false);
			}
		});
		
        btnActivateService = (Button)findViewById(R.id.activateServiceBtn);
        btnActivateService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Animation anim = AnimationUtils.loadAnimation(GravityRinger.this, R.anim.shake);
				GravityRinger.this.layMain.startAnimation(anim);
				GravityRinger.this.startService();
		        Toast.makeText(GravityRinger.this, R.string.service_started, Toast.LENGTH_SHORT).show();
			}
        });

        btnDeactivateService = (Button)findViewById(R.id.deactivateServiceBtn);
        btnDeactivateService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GravityRinger.this.stopService();
			}
        });
    }
    
    private void initSettings() {
        txtDelay.setText(""+mSettings.getInt(Preferences.DELAY, 1000));
        txtSilenceThreshold.setText(""+mSettings.getFloat(Preferences.SILENCE_GRAVITY, 5.0f));
		txtNoisyThreshold.setText(""+mSettings.getFloat(Preferences.NOISY_GRAVITY, -5.0f));
		chkAutoStart.setChecked(mSettings.getBoolean(Preferences.AUTO_START, true));
    }
    
    private void startService() {
		Intent serviceIntent = new Intent(GravityRinger.this, GravityRingerService.class);
		Log.d(TAG, "Start service");
        startService(serviceIntent);
    }

	private void stopService() {
		Intent serviceIntent = new Intent(GravityRinger.this, GravityRingerService.class);
		Log.d(TAG, "Stop service");
        if (stopService(serviceIntent)) {
	        Toast.makeText(GravityRinger.this, R.string.service_stopped, Toast.LENGTH_SHORT).show();
        	Log.d(TAG, "Service Stopped!");
        }
	}

	@Override
	public void onAccuracyChanged(int arg0, int arg1) {
	}
	
	@Override
	public void onSensorChanged(int sensor, float[] values) {
		if (mReadSensor && (sensor | SensorManager.SENSOR_ORIENTATION) != 0) {
			Log.i(TAG, "Threshold changed "+values[SensorManager.DATA_Y]+" "+mReadMode);
			switch (mReadMode) {
			case MODE_NOISY:
				txtNoisyThreshold.setText(""+values[SensorManager.DATA_Y]);
				break;
			case MODE_SILENCE:
				txtSilenceThreshold.setText(""+values[SensorManager.DATA_Y]);
				break;
			}
			mReadSensor = false;
			btnSetSilence.setEnabled(true);
			btnSetNoisy.setEnabled(true);
		}
	}    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem item = menu.add(0, MENU_REVERT, 0, "Revert settings");
    	item.setIcon(android.R.drawable.ic_menu_revert);
    	item.setAlphabeticShortcut('r');
    	MenuItem mnuClose = menu.add(0, MENU_CANCEL, 0, "Cancel");
    	mnuClose.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	mnuClose.setAlphabeticShortcut('q');
    	
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	    	Log.i(TAG, "Save Settings");
			if (!saveSettings(false)) {
				new AlertDialog.Builder(this)
				.setTitle(R.string.alert_values_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(R.string.alert_values_message)
				.setNeutralButton(R.string.alert_values_btn_edit, null)
				.setNegativeButton(R.string.alert_values_btn_back, new android.content.DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						GravityRinger.this.finish();
					}
				})
				.show();
			} else
				return super.onKeyDown(keyCode, event);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CANCEL:
			finish();
			break;
		case MENU_REVERT:
			initSettings();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private boolean saveSettings(boolean notify) {
		SharedPreferences.Editor editor = mSettings.edit();
		int delay = 0;
		Float silence = 0f, noisy = 0f;
		boolean error = false;
		try {
			delay = Integer.parseInt(this.txtDelay.getText().toString());
		} catch (NumberFormatException ex) {
			txtDelay.setError(getText(R.string.error_values_not_integer));
			error = true;
		}
		try {
			silence = Float.parseFloat(this.txtSilenceThreshold.getText().toString());
		} catch (NumberFormatException ex) {
			txtSilenceThreshold.setError(getText(R.string.error_values_not_number));
			error = true;
		}
		try {
			noisy = Float.parseFloat(this.txtNoisyThreshold.getText().toString());
		} catch (NumberFormatException ex) {
			txtNoisyThreshold.setError(getText(R.string.error_values_not_number));
			error = true;
		}
		
		boolean autoStart = chkAutoStart.isChecked();
		
		if (!error) {
			editor.putInt(Preferences.DELAY, delay);
			editor.putFloat(Preferences.NOISY_GRAVITY, noisy);
			editor.putFloat(Preferences.SILENCE_GRAVITY, silence);
			editor.putBoolean(Preferences.AUTO_START, autoStart);
			editor.commit();
			if (notify)
				Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
			startService();
		}
		return !error;
	}
	
	private LinearLayout layMain;
	private EditText txtDelay;
    private EditText txtNoisyThreshold;
    private EditText txtSilenceThreshold;
    private CheckBox chkAutoStart;
    
    private Button btnSetNoisy;
    private Button btnSetSilence;
    private Button btnActivateService;
    private Button btnDeactivateService;
	private SensorManager mSensorMgr = null;
	
	private SharedPreferences mSettings;
	private boolean mReadSensor = false;
	private int mReadMode = 0;
}