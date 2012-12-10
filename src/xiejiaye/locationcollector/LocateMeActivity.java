package xiejiaye.locationcollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LocateMeActivity extends Activity {
	
	private static final String LOG_TAG = "LocateMeActivity";
	
	private TextView mMyLocation;
	private ListView mMatchingBuildingListView;
	private ArrayAdapter<Building> mMatchingBuildingListAdapter;

	private List<Building> mAllBuildingList;
	private List<Building> mMatchingBuildingList;
	private Map<Building, Integer> mBuildingIdMap;
	
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locate_me);
		
		BuildingStorage.initialize(this);
		mAllBuildingList = BuildingStorage.getInstance().getSavedBuildingList();
		
		mMyLocation = (TextView) findViewById(R.id.my_location);
		mMatchingBuildingListView = (ListView) findViewById(R.id.matching_building_list);
		mMatchingBuildingList = new ArrayList<Building>();
		mBuildingIdMap = new HashMap<Building, Integer>();
		mMatchingBuildingListAdapter = new ArrayAdapter<Building>(
				this, 
				android.R.layout.simple_list_item_1, 
				mMatchingBuildingList);
		mMatchingBuildingListView.setAdapter(mMatchingBuildingListAdapter);
		mMatchingBuildingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				int buildingId = mBuildingIdMap.get(mMatchingBuildingList.get(position));
				Intent intent = new Intent(LocateMeActivity.this, BuildingDetailActivity.class);
				intent.putExtra(BuildingDetailActivity.KEY_BUILDING_ID, buildingId);
				startActivity(intent);
			}
		});
		
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
				locateMe(location);
			}
		};
		
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
	}

	private void locateMe(Location location) {
		mMyLocation.setText(location.getLatitude()+", "+location.getLongitude());
		mMatchingBuildingList.clear();
		mBuildingIdMap.clear();
		for (int i = 0; i < mAllBuildingList.size(); i++) {
			Building building = mAllBuildingList.get(i);
			if (building.includes(location)) {
				mMatchingBuildingList.add(building);
				mBuildingIdMap.put(building, i);
			}
		}
		mMatchingBuildingListAdapter.notifyDataSetChanged();
	}

	@Override
	public void finish() {
		mLocationManager.removeUpdates(mLocationListener);
		super.finish();
	}
}
