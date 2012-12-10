package xiejiaye.locationcollector;

import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BuildingDetailActivity extends Activity {
	
	private static final String LOG_TAG = "BuildingDetailActivity";
	
	public static final String KEY_BUILDING_ID = "building_id";
	private static final int NO_ID = -1;
	
	private TextView mTextLatitude;
	private TextView mTextLongitude;
	private Button mUpdateNetwork;
	private Button mUpdateGps;
	private Button mStopUpdate;
	private TextView mTextNewLocation;
	
	private List<Building> mBuildings;
	private int mBuildingId;
	
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_building_detail);
		
		BuildingStorage.initialize(this);
		mBuildings = BuildingStorage.getInstance().getSavedBuildingList();
		mBuildingId = getIntent().getIntExtra(KEY_BUILDING_ID, NO_ID);
		if (mBuildingId == NO_ID) {
			Toast.makeText(this, R.string.invalid_param, Toast.LENGTH_LONG).show();
			finish();
		}
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mLocationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.i(LOG_TAG, provider+" status changed to: "+status);
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				Log.i(LOG_TAG, provider+" enabled.");
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				Log.i(LOG_TAG, provider+" disabled.");
			}
			
			@Override
			public void onLocationChanged(Location location) {
				mTextNewLocation.setText(location.getLatitude()+", "+location.getLongitude());
				mBuildings.get(mBuildingId).updateLocation(location);
				updateDisplay();
				BuildingStorage.getInstance().saveBuildingList(mBuildings);
			}
		};
		
		mTextLatitude = (TextView) findViewById(R.id.building_location_latitude);
		mTextLongitude = (TextView) findViewById(R.id.building_location_longitude);
		mUpdateNetwork = (Button) findViewById(R.id.button_refresh_location_network);
		mUpdateGps = (Button) findViewById(R.id.button_refresh_location_gps);
		mStopUpdate = (Button) findViewById(R.id.button_stop_update);
		mTextNewLocation = (TextView) findViewById(R.id.new_location);
		
		setTitle(mBuildings.get(mBuildingId).getName());
		updateDisplay();
		
		mUpdateNetwork.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestUpdate(LocationManager.NETWORK_PROVIDER);
			}
		});
		mUpdateGps.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestUpdate(LocationManager.GPS_PROVIDER);
			}
		});
		mStopUpdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLocationManager.removeUpdates(mLocationListener);
			}
		});
	}
	
	private void requestUpdate(String locationProvider) {
		mLocationManager.requestLocationUpdates(locationProvider, 0, 0, mLocationListener);
	}
	
	private void updateDisplay() {
		Building building = mBuildings.get(mBuildingId);
		mTextLatitude.setText(building.getlatitudeScope());
		mTextLongitude.setText(building.getLongitudeScope());
	}
	
	@Override
	public void finish() {
		mLocationManager.removeUpdates(mLocationListener);
		super.finish();
	}
}
