package com.example.travelguide;

//import com.example.texttospeech.R;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.gson.*;

import java.net.*;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;  
import java.util.Locale;  

public class MainActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener, TextToSpeech.OnInitListener{

	private GoogleApiClient mGoogleApiClient;
	private TextToSpeech tts;
	private Button buttonSpeak;
	private EditText editText;
	
	private class Position {
		
		private double latitude;
		private double longitude;
		
		private Position(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		private double getLatitude() {
			return latitude;
		}
		
		private double getLongitude() {
			return longitude;
		}
	}
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		buildGoogleApiClient();
		
		tts = new TextToSpeech(this, this);
		buttonSpeak = (Button) findViewById(R.id.button1);

		buttonSpeak.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		        speakOut();
		    }

		});
	}

	@Override
	public void onDestroy() {
	// Don't forget to shutdown tts!
	if (tts != null) {
	    tts.stop();
	    tts.shutdown();
	}
	super.onDestroy();
	}
	
	@Override
	public void onInit(int status) {

	if (status == TextToSpeech.SUCCESS) {

	    int result = tts.setLanguage(Locale.CHINA);

	    if (result == TextToSpeech.LANG_MISSING_DATA
	            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	        Log.e("TTS", "This Language is not supported");
	    } else {
	    	buttonSpeak.setEnabled(true);
	        speakOut();
	    }

	} else {
	    Log.e("TTS", "Initilization Failed!");
	}
	}
	
	private void speakOut() {
	    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
        	String text = getNearestPlace(new Position(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        	tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
   	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onConnected(Bundle connectionHint) {
    }
	
	public String getNearestPlace(Position curPos) {
		try {
			URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + curPos.getLatitude() + "," + curPos.getLongitude());
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
		    request.connect();
		    JsonParser jp = new JsonParser();
		    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		    JsonObject rootobj = root.getAsJsonObject();
		    String country = rootobj.get("country").getAsString();
		    return country;
		} catch(Exception e) {
			System.err.print("URL Error");
			return null;
		}
	}

	
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
