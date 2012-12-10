package xiejiaye.locationcollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

public class BuildingStorage {

	static private BuildingStorage sBuildingStorage; 
	
	public static void initialize(Context context) {
		sBuildingStorage = new BuildingStorage(context);
	}
	
	/**
	 * Be sure to call {@link #initialize(Context)} before calling this!!!
	 * @return
	 */
	public static BuildingStorage getInstance() {
		return sBuildingStorage;
	}

	private static final String LOCATION_FILE = "location";
	private static final String READABLE_LOCATION = "location_exported.txt";
	
	private Context mContext;
	
	private BuildingStorage(Context context) {
		mContext = context;
	}

	@SuppressWarnings("unchecked")
	public List<Building> getSavedBuildingList() {
		List<Building> ans = null;
		try {
			FileInputStream fis = mContext.openFileInput(LOCATION_FILE);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ans = (List<Building>) ois.readObject();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ans == null) {
				ans = new ArrayList<Building>();
			}
		}
		return ans;
	}
	
	public void saveBuildingList(List<Building> buildings) {
		try {
			FileOutputStream fos = mContext.openFileOutput(LOCATION_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(buildings);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String exportBuildingList() {
		List<Building> buildings = getSavedBuildingList();
		File root = Environment.getExternalStorageDirectory();
		File outDir = new File(root.getAbsolutePath() + File.separator
						+ mContext.getString(R.string.app_name));
		if (!outDir.isDirectory()) {
	    	outDir.mkdir();
	    }
		try {
			File outputFile = new File(outDir, READABLE_LOCATION);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			for (Building building : buildings) {
				writer.write(building.exportReadable());
			}
			writer.close();
			return mContext.getString(R.string.exported_to_, outputFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mContext.getString(R.string.export_failed);
	}
}
