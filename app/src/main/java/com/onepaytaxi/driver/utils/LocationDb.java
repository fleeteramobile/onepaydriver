package com.onepaytaxi.driver.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Locale;

public class LocationDb extends SQLiteOpenHelper {
    Double haverdistance = 0.0;
    public double HTotdistanceKM = 0.0;
    public final double Rad = 6372.8; // In kilometers
    private String distanceKM = "";
    private final Context con;

    public LocationDb(Context context) {
        super(context, "DB", null, 1);
        // TODO Auto-generated constructor stub
        this.con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String create_sql = "create table off_location(_id INTEGER PRIMARY KEY AUTOINCREMENT,trip_id text,locations text,distance text)";
        db.execSQL(create_sql);
    }

    /**
     * insert location into off_locations table
     *
     * @param trip_id
     * @param locations
     * @param distance
     */
    public void insert_locations(String trip_id, String locations, String distance) {
        // TODO Auto-generated method stub
        String search_sql = "select * from off_location where trip_id=" + trip_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery(search_sql, null);
        if (mCursor != null) {
            if (mCursor.getCount() == 0) {
                SQLiteDatabase _db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("trip_id", trip_id);
                values.put("locations", locations);
                values.put("distance", distance);
                _db.insert("off_location", null, values);
            } else {
                locations = getlocation_detail(trip_id) + locations;
                SQLiteDatabase _db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("trip_id", trip_id);
                values.put("locations", locations);
                values.put("distance", distance);
                _db.update("off_location", values, "trip_id" + " = " + trip_id, null);
                _db.close();
            }
            mCursor.close();
            db.close();
        }
    }

    /**
     * delete past locations from database
     */
    public void delete_locations() {
        SQLiteDatabase db = this.getWritableDatabase();
        String delete_sql = "DELETE FROM off_location";
        db.execSQL(delete_sql);
    }

    /**
     * read locations from table
     *
     * @param trip_id
     * @return
     */
    public String getlocation_detail(String trip_id) {
        String offline_loc = "";
        SQLiteDatabase data = this.getReadableDatabase();
        String sql = "select * from off_location where trip_id=" + trip_id;
        Cursor cur = data.rawQuery(sql, null);
        if (cur.moveToFirst()) {
            do {
                offline_loc += cur.getString(2);
            }
            while (cur.moveToNext());
        }
        cur.close();
        data.close();
        return offline_loc;
    }

    /**
     * get distance of trip from table
     *
     * @param trip_id
     * @return
     */
    public String getdistance(String trip_id) {
        String offline_distance = "";
        String[] distance_loc;
        String[] pickuploc;
        String[] droploc;
        double pickuplat = 0.0;
        double pickuplng = 0.0;
        double droplat = 0.0;
        double droplng = 0.0;
        SQLiteDatabase data = this.getReadableDatabase();
        String sql = "select * from off_location where trip_id=" + trip_id;
        Cursor cur = data.rawQuery(sql, null);
        if (cur.moveToFirst()) {
            do {
                offline_distance = cur.getString(2);
            }
            while (cur.moveToNext());
        }
        cur.close();
        data.close();
        if (offline_distance.length() > 0) {
            distance_loc = offline_distance.split("\\|");
            for (int i = 0; i < distance_loc.length; i++) {
                pickuploc = distance_loc[i].split(",");
                if (distance_loc.length == i + 1) {
                    droploc = distance_loc[i].split(",");
                } else {
                    droploc = distance_loc[i + 1].split(",");
                }
                if (pickuploc.length > 0 && droploc.length > 0) {
                    if (!pickuploc[0].equalsIgnoreCase("0.0") && !pickuploc[0].equalsIgnoreCase(null) && !pickuploc[1].equalsIgnoreCase("0.0") && !pickuploc[1].equalsIgnoreCase(null)) {
                        pickuplat = Double.parseDouble(pickuploc[0]);
                        pickuplng = Double.parseDouble(pickuploc[1]);
                    }
                    if (!droploc[0].equalsIgnoreCase("0.0") && !droploc[0].equalsIgnoreCase(null) && !droploc[1].equalsIgnoreCase("0.0") && !droploc[1].equalsIgnoreCase(null)) {
                        droplat = Double.parseDouble(droploc[0]);
                        droplng = Double.parseDouble(droploc[1]);
                    }
                }
                if (pickuplat != 0.0 && droplat != 0.0) {
                    haversine(pickuplat, pickuplng, droplat, droplng);
                }
            }
        }
        return distanceKM;
    }

    public double getLocationOnLocationChange(String sLocations) {
        String offline_distance = sLocations;
        String[] distance_loc;
        String[] pickuploc;
        String[] droploc;
        double pickuplat = 0.0;
        double pickuplng = 0.0;
        double droplat = 0.0;
        double droplng = 0.0;
        if (offline_distance.length() > 0) {
            distance_loc = offline_distance.split("\\|");
            for (int i = 0; i < distance_loc.length; i++) {
                pickuploc = distance_loc[i].split(",");
                if (distance_loc.length == i + 1) {
                    droploc = distance_loc[i].split(",");
                } else {
                    droploc = distance_loc[i + 1].split(",");
                }
                if (pickuploc.length > 0 && droploc.length > 0) {
                    if (!pickuploc[0].equalsIgnoreCase("0.0") && !pickuploc[0].equalsIgnoreCase(null) && !pickuploc[1].equalsIgnoreCase("0.0") && !pickuploc[1].equalsIgnoreCase(null)) {
                        pickuplat = Double.parseDouble(pickuploc[0]);
                        pickuplng = Double.parseDouble(pickuploc[1]);
                    }
                    if (!droploc[0].equalsIgnoreCase("0.0") && !droploc[0].equalsIgnoreCase(null) && !droploc[1].equalsIgnoreCase("0.0") && !droploc[1].equalsIgnoreCase(null)) {
                        droplat = Double.parseDouble(droploc[0]);
                        droplng = Double.parseDouble(droploc[1]);
                    }
                }
                if (pickuplat != 0.0 && droplat != 0.0) {
                    haversine(pickuplat, pickuplng, droplat, droplng);
                }
            }
        }
        return HTotdistanceKM;
    }

    /**
     * haver shine formula for calculate the distance
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     */
    public void haversine(double lat1, double lon1, double lat2, double lon2) {
        // TODO Auto-generated method stub
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        haverdistance = Rad * c;
        HTotdistanceKM += haverdistance;
        distanceKM = String.format(Locale.UK, "%.2f", HTotdistanceKM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS off_location");
        onCreate(db);
    }
}
