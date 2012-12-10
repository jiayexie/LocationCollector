package xiejiaye.locationcollector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BuildingListActivity extends ListActivity {
	
	private List<Building> mBuildings;
	private ArrayAdapter<Building> mAdapter;
	
    @Override
    public void onResume() {
    	super.onResume();
    	
        BuildingStorage.initialize(this);
        mBuildings = BuildingStorage.getInstance().getSavedBuildingList();
        mAdapter = new ArrayAdapter<Building>(
        		this,
        		android.R.layout.simple_list_item_1,
        		mBuildings);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(BuildingListActivity.this, BuildingDetailActivity.class);
				intent.putExtra(BuildingDetailActivity.KEY_BUILDING_ID, position);
				startActivity(intent);
			}
		});
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new AlertDialog.Builder(BuildingListActivity.this)
					.setItems(R.array.action_items, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:	// edit
								editBuilding(position);
								break;
							case 1:	// delete
								deleteBuilding(position);
								break;
							}
						}
					})
					.show();
				return true;
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_building_list, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                addBuilding();
            	return true;
            case R.id.menu_export:
            	export();
            	return true;
            case R.id.menu_locate_me:
            	startActivity(new Intent(this, LocateMeActivity.class));
            	return true;
            case R.id.menu_sort:
            	sort();
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addBuilding() {
    	final View view = LayoutInflater.from(this)
    			.inflate(R.layout.dialog_add_building, null);
    	new AlertDialog.Builder(this)
    		.setTitle(R.string.title_dialog_add_building)
    		.setView(view)
    		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mBuildings.add(new Building(((EditText) view.findViewById(
							R.id.building_name_edit)).getText().toString()));
					BuildingStorage.getInstance().saveBuildingList(mBuildings);
					mAdapter.notifyDataSetChanged();
				}
			})
			.setNegativeButton(R.string.cancel, null)
			.show();
    }
    
    private void editBuilding(int buildingId) {
    	final View view = LayoutInflater.from(this)
    			.inflate(R.layout.dialog_add_building, null);
    	final TextView textView = (TextView) view.findViewById(R.id.building_name_edit);
    	final Building building = mBuildings.get(buildingId);
    	textView.setText(building.getName());
    	new AlertDialog.Builder(this)
			.setTitle(R.string.title_dialog_edit_building)
			.setView(view)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					building.setName(textView.getText().toString());
					BuildingStorage.getInstance().saveBuildingList(mBuildings);
					mAdapter.notifyDataSetChanged();
				}
			})
			.setNegativeButton(R.string.cancel, null)
			.show();
    }
    
    private void deleteBuilding(final int buildingId) {
    	new AlertDialog.Builder(this)
			.setMessage(getString(R.string.confirm_delete_, 
					mBuildings.get(buildingId).getName()))
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mBuildings.remove(buildingId);
					mAdapter.notifyDataSetChanged();
					BuildingStorage.getInstance().saveBuildingList(mBuildings);
				}
			})
			.setNegativeButton(R.string.cancel, null)
			.show();
    }
    
    private void export() {
    	Toast.makeText(this, BuildingStorage.getInstance().exportBuildingList(),
    			Toast.LENGTH_LONG).show();
    }
    
    private void sort() {
    	Collections.sort(mBuildings, new Comparator<Building>() {

			@Override
			public int compare(Building lhs, Building rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
		});
    	mAdapter.notifyDataSetChanged();
    	BuildingStorage.getInstance().saveBuildingList(mBuildings);
    }
}
