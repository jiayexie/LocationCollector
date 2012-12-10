package xiejiaye.locationcollector;

import java.io.Serializable;

import android.location.Location;

public class Building implements Serializable {

	private static final long serialVersionUID = 6342078714151406172L;

	private String mName;
	private double mLatitudeMin = Double.NaN, mLatitudeMax = Double.NaN;
	private double mLongitudeMin = Double.NaN, mLongitudeMax = Double.NaN;
	
	public Building(String name) {
		setName(name);
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getlatitudeScope() {
		return "["+mLatitudeMin+", "+mLatitudeMax+"]";
	}
	
	public String getLongitudeScope() {
		return "["+mLongitudeMin+", "+mLongitudeMax+"]";
	}
	
	public void updateLocation(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		if (latitude > mLatitudeMax || Double.compare(mLatitudeMax, Double.NaN)==0) {
			mLatitudeMax = latitude;
		}
		if (latitude < mLatitudeMin || Double.compare(mLatitudeMin, Double.NaN)==0) {
			mLatitudeMin = latitude;
		}
		if (longitude > mLongitudeMax || Double.compare(mLongitudeMax, Double.NaN)==0) {
			mLongitudeMax = longitude;
		}
		if (longitude < mLongitudeMin || Double.compare(mLongitudeMin, Double.NaN)==0) {
			mLongitudeMin = longitude;
		}
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public String exportReadable() {
		return mName+"\t"+getlatitudeScope()+"\t"+getLongitudeScope()+"\n";
	}
	
	public boolean includes(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		if (latitude < mLatitudeMin || latitude > mLatitudeMax) return false;
		if (longitude < mLongitudeMin || longitude > mLongitudeMax) return false;
		return true;
	}
}
