package com.hiype.walktrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.animation.content.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper {

    private static SQLiteDatabase read_db, write_db;

    //Database info
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Walktrack.db";

    //Table names
    private static final String TABLE_USER = "user";
    private static final String TABLE_STEPS = "steps_data";
    private static final String TABLE_LOCATIONS = "location_data";

    //User table columns
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_USERNAME = "user_username";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_JOINDATE = "user_joindate";
    private static final String COLUMN_USER_HEIGHT = "user_height";
    private static final String COLUMN_USER_TOTALSTEPCOUNT = "user_totalstepcount";
    private static final String COLUMN_USER_FRIENDSIDS = "user_friendsids";
    private static final String COLUMN_USER_ICONID = "user_iconid";
    private static final String COLUMN_USER_HAS_PC = "user_has_pc";
    private static final String COLUMN_USER_POINTS = "user_points";
    private static final String COLUMN_USER_NIGHTMODE = "user_nightmode";
    private static final String COLUMN_USER_CLAIMED_ICONS = "user_claimed_icons";
    private static final String COLUMN_USER_LANGUAGE = "user_lang";
    private static final String COLUMN_USER_LAST_STEPS = "user_last_steps";

    //Steps table columns
    private static final String COLUMN_STEPS_USER_ID = "user_id";
    private static final String COLUMN_STEPS_STEPCOUNT = "stepcount";
    private static final String COLUMN_STEPS_DATE = "date";
    private static final String COLUMN_STEPS_DATE_ID = "date_id";

    //Locations table columns
    private static final String COLUMN_LOCATIONS_DATE_ID = "date_id";
    private static final String COLUMN_LOCATIONS_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOCATIONS_LONGITUDE = "longitude";
    private static final String COLUMN_LOCATIONS_LATITUDE = "latitude";
    private static final String COLUMN_LOCATIONS_LOCATION_ID = "location_id";

    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY," + COLUMN_USER_USERNAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_JOINDATE + " TEXT," + COLUMN_USER_HEIGHT + " INTEGER,"
            + COLUMN_USER_TOTALSTEPCOUNT + " INTEGER," + COLUMN_USER_FRIENDSIDS + " BLOB,"
            + COLUMN_USER_ICONID + " INTEGER," + COLUMN_USER_HAS_PC + " INTEGER,"
            + COLUMN_USER_POINTS + " INTEGER," + COLUMN_USER_NIGHTMODE +" INTEGER,"
            + COLUMN_USER_CLAIMED_ICONS + " BLOB," + COLUMN_USER_LANGUAGE + " TEXT," + COLUMN_USER_LAST_STEPS + " INTEGER" +")";

    private String CREATE_STEPS_TABLE = "CREATE TABLE " + TABLE_STEPS + "("
            + COLUMN_STEPS_USER_ID + " INTEGER," + COLUMN_STEPS_DATE_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_STEPS_STEPCOUNT + " INTEGER," + COLUMN_STEPS_DATE + " TEXT" + ")";

    private String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
            + COLUMN_LOCATIONS_DATE_ID + " INTEGER," + COLUMN_LOCATIONS_LOCATION_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_LOCATIONS_TIMESTAMP + " TEXT," + COLUMN_LOCATIONS_LONGITUDE + " TEXT," + COLUMN_LOCATIONS_LATITUDE + " INTEGER" + ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    private String DROP_STEPS_TABLE = "DROP TABLE IF EXISTS " + TABLE_STEPS;

    private String DROP_LOCATIONS_TABLE = "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        Log.e("DB", "Called ONCREATE");

        db.execSQL(CREATE_USER_TABLE);
        Log.e("DB", "User table created!");

        db.execSQL(CREATE_STEPS_TABLE);
        db.execSQL(CREATE_LOCATIONS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_STEPS_TABLE);
        db.execSQL(DROP_LOCATIONS_TABLE);
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getId());
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_JOINDATE, user.getDate_joined());
        values.put(COLUMN_USER_HEIGHT, user.getHeight());
        values.put(COLUMN_USER_TOTALSTEPCOUNT, user.getStepCount());
        values.put(COLUMN_USER_FRIENDSIDS, user.getFriends_ids());
        values.put(COLUMN_USER_ICONID, user.getIconID());
        values.put(COLUMN_USER_HAS_PC, user.getHasDesktop());
        values.put(COLUMN_USER_POINTS, user.getPoints());
        values.put(COLUMN_USER_NIGHTMODE, user.getNightMode());
        values.put(COLUMN_USER_CLAIMED_ICONS, user.getClaimed_icons());
        values.put(COLUMN_USER_LANGUAGE, user.getLanguage());

        // Inserting Row
        if(db.insert(TABLE_USER, null, values) != -1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_JOINDATE, user.getDate_joined());
        values.put(COLUMN_USER_HEIGHT, user.getHeight());
        values.put(COLUMN_USER_TOTALSTEPCOUNT, user.getStepCount());
        values.put(COLUMN_USER_FRIENDSIDS, user.getFriends_ids());
        values.put(COLUMN_USER_ICONID, user.getIconID());
        values.put(COLUMN_USER_HAS_PC, user.getHasDesktop());
        values.put(COLUMN_USER_POINTS, user.getPoints());
        values.put(COLUMN_USER_NIGHTMODE, user.getNightMode());
        values.put(COLUMN_USER_CLAIMED_ICONS, user.getClaimed_icons());
        values.put(COLUMN_USER_LANGUAGE, user.getLanguage());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, null, null);
        db.close();
    }

    public boolean updateUserName (String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);

        if(db.update(TABLE_USER, values, null, null) >= 1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }

    }

    public User getUser() {
        User user_obj;

        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_USERNAME,
                COLUMN_USER_EMAIL,
                COLUMN_USER_ICONID,
                COLUMN_USER_HAS_PC,
                COLUMN_USER_FRIENDSIDS,
                COLUMN_USER_HEIGHT,
                COLUMN_USER_JOINDATE,
                COLUMN_USER_TOTALSTEPCOUNT,
                COLUMN_USER_POINTS,
                COLUMN_USER_NIGHTMODE,
                COLUMN_USER_CLAIMED_ICONS,
                COLUMN_USER_LANGUAGE
        };

        String[] columns2 = {
                COLUMN_STEPS_STEPCOUNT
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            do {
                user_obj = new User(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)), cursor.getString(cursor.getColumnIndex(COLUMN_USER_HEIGHT)), cursor.getString(cursor.getColumnIndex(COLUMN_USER_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)), cursor.getString(cursor.getColumnIndex(COLUMN_USER_JOINDATE)),
                        0, cursor.getString(cursor.getColumnIndex(COLUMN_USER_FRIENDSIDS)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ICONID)), cursor.getInt(cursor.getColumnIndex(COLUMN_USER_HAS_PC)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_TOTALSTEPCOUNT)), cursor.getInt(cursor.getColumnIndex(COLUMN_USER_POINTS)), cursor.getInt(cursor.getColumnIndex(COLUMN_USER_NIGHTMODE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_CLAIMED_ICONS)), cursor.getString(cursor.getColumnIndex(COLUMN_USER_LANGUAGE))
                );
            } while (cursor.moveToNext());
        } else {
            user_obj = null;
            Log.e("DB", "No user was found!");
            return user_obj;
        }
        cursor.close();

        Cursor cursor2 = db.query(TABLE_STEPS, //Table to query
                columns2,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if(cursor2.moveToFirst()) {
            do {
                user_obj.setStepCount(cursor2.getInt(cursor2.getColumnIndex(COLUMN_STEPS_STEPCOUNT)));
            } while (cursor2.moveToNext());
        } else {
            Log.e("DB", "Failed to add step count to use obj");
        }

        cursor2.close();
        db.close();

        return user_obj;
    }

    public void resetDb() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_STEPS_TABLE);
        db.execSQL(DROP_LOCATIONS_TABLE);
        onCreate(db);
    }

    public boolean getNightMode() {

        String[] columns = {
                COLUMN_USER_NIGHTMODE
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if(!cursor.moveToFirst())
        {
            Log.e("DB GETNIGHTMODE", "No nightmode was found");
        }

        return cursor.getInt(cursor.getColumnIndex(COLUMN_USER_NIGHTMODE)) == 1;
    }

    public boolean updateNightMode(boolean nightMode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NIGHTMODE, nightMode);

        if(db.update(TABLE_USER, values, null, null) >= 1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    public boolean doesUserExist() {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public int getCurrentUserID() {
        // array of columns to fetch

        int userID;

        String[] columns = {
                COLUMN_USER_ID
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            do {
                userID = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            } while (cursor.moveToNext());
        } else {
            userID = -1;
        }



        cursor.close();
        db.close();

        return userID;

    }

    public int getUserHeight() {

        int user_height;

        String[] columns = {
                COLUMN_USER_HEIGHT
        };

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            do {
                user_height = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_HEIGHT));
            } while (cursor.moveToNext());
        } else {
            user_height = -1;
        }

        cursor.close();
        db.close();

        return user_height;
    }

    public boolean createStepsEntry(int steps) {
        boolean isDateAlreadyPresent = false;
        boolean createSuccessful = false;
        int currentDateStepCounts = steps;

        Calendar calendar = Calendar.getInstance();
        String todayDate = String.valueOf(calendar.get(Calendar.MONTH))+"/" +
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.YEAR));
        String selectQuery = "SELECT " + COLUMN_STEPS_STEPCOUNT + " FROM "
                + TABLE_STEPS + " WHERE " + COLUMN_STEPS_DATE + " = '" + todayDate + "'";

        //Checks if todays date exists inside db
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    isDateAlreadyPresent = true;
                    currentDateStepCounts =
                            c.getInt((c.getColumnIndex(COLUMN_STEPS_STEPCOUNT)));
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_STEPS_DATE, todayDate);
            if(isDateAlreadyPresent)
            {
                values.put(COLUMN_STEPS_STEPCOUNT, currentDateStepCounts);
                values.put(COLUMN_STEPS_USER_ID, getCurrentUserID());
                int row = db.update(TABLE_STEPS, values,
                        COLUMN_STEPS_DATE + " = '" + todayDate + "'", null);
                if(row == 1)
                {
                    createSuccessful = true;
                }
                db.close();
            }
            else
            {
                values.put(COLUMN_STEPS_STEPCOUNT, 0);
                long row = db.insert(TABLE_STEPS, null,
                        values);
                if(row!=-1)
                {
                    createSuccessful = true;
                }
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return createSuccessful;
    }

    public boolean updateStepCount(int stepcount) {

        Calendar calendar = Calendar.getInstance();
        String todayDate = String.valueOf(calendar.get(Calendar.MONTH))+"/" +
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.YEAR));

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_STEPS_STEPCOUNT, stepcount);
        int row = db.update(TABLE_STEPS, values,
                COLUMN_STEPS_DATE + " = '" + todayDate + "'", null);

        db.close();

        return row == 1;
    }

    public int getStepCount() {
        int stepCount;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_STEPS_STEPCOUNT
        };

        Cursor cursor = db.query(TABLE_STEPS, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            do {
                stepCount = cursor.getInt(cursor.getColumnIndex(COLUMN_STEPS_STEPCOUNT));
            } while (cursor.moveToNext());
        } else {
            stepCount = -1;
        }

        cursor.close();
        db.close();

        return stepCount;
    }

    public int hasUserAddedPc() {
        int has_pc;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_USER_HAS_PC
        };

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
                has_pc = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_HAS_PC));
        } else {
            has_pc = -1;
        }

        cursor.close();
        db.close();

        return has_pc;
    }

    public ArrayList<Integer> getStepsAndPoints() {

        Calendar calendar = Calendar.getInstance();
        String todayDate = String.valueOf(calendar.get(Calendar.MONTH))+"/" +
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.YEAR));

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Integer> data = new ArrayList<Integer>();

        String[] columns = {
                COLUMN_USER_TOTALSTEPCOUNT,
                COLUMN_USER_POINTS
        };

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                COLUMN_STEPS_DATE + " = '" + todayDate + "'",                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            data.add(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_TOTALSTEPCOUNT)), 1);
            data.add(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_POINTS)), 2);
        } else {
            Log.e("DBHELPER", "No point or totalstepcount data found in db");
            return new ArrayList<Integer>();
        }

        cursor.close();

        String[] columns2 = {
            COLUMN_STEPS_STEPCOUNT
        };

        cursor = db.query(TABLE_STEPS, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            data.add(cursor.getInt(cursor.getColumnIndex(COLUMN_STEPS_STEPCOUNT)), 0);
        } else {
            Log.e("DBHELPER", "No stepcount data found in db");
            return new ArrayList<Integer>();
        }

        cursor.close();
        db.close();

        return data;
    }

    public int getStepsWalked() {

        Calendar calendar = Calendar.getInstance();
        String todayDate = String.valueOf(calendar.get(Calendar.MONTH))+"/" +
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.YEAR));

        int stepsWalked = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_STEPS_STEPCOUNT
        };

        Cursor cursor = db.query(TABLE_STEPS, //Table to query
                columns,                    //columns to return
                COLUMN_STEPS_DATE + " = '" + todayDate + "'",                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            stepsWalked = cursor.getInt(cursor.getColumnIndex(COLUMN_STEPS_STEPCOUNT));
        } else {
            Log.e("DBHELPER", "No stepcount data was found in db");
        }

        cursor.close();
        db.close();

        return stepsWalked;
    }

    public ArrayList<ArrayList<String>> getAllSteps() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        String[] columns = {
                COLUMN_STEPS_STEPCOUNT,
                COLUMN_STEPS_DATE
        };

        Cursor cursor = db.query(TABLE_STEPS, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            String date;
            String stepCount;
            ArrayList<String> date_data = new ArrayList<String>();
            date_data.add(0, cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_DATE)));
            date_data.add(1, cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_STEPCOUNT)));
            data.add(0 ,date_data);
            for(int i = 1; i < 7; i++) {
                date_data = new ArrayList<String>();
                if(cursor.moveToNext()) {
                    date = cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_DATE));
                    stepCount = cursor.getString(cursor.getColumnIndex(COLUMN_STEPS_STEPCOUNT));
                    Log.e("STATS FOR LOOP", "Iteration: " + i);
                    date_data.add(0, date);
                    date_data.add(1, stepCount);
                    data.add(i, date_data);
                } else {
                    Log.e("DBHELPER GETALLSTEPS", "Less than 7 days in db, only: " + i);
                    break;
                }
            }
        } else {
            Log.e("DBHELPER", "No stepcount history data found in db");
            data = new ArrayList<ArrayList<String>>();
        }

        cursor.close();

        return data;
    }

    public boolean updatePoints(int points) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_POINTS, points);
        int row = db.update(TABLE_USER, values,
                null, null);

        db.close();

        return row == 1;
    }

    public int getPoints() {
        int points = 0;

        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                COLUMN_USER_POINTS
        };

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            points = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_POINTS));
        } else {
            Log.e("DBHELPER", "No points data was found in db");
        }

        cursor.close();
        db.close();

        return points;
    }

    public ArrayList<Integer> getClaimedIcons() {
        String string;
        boolean hasIcons = false;
        ArrayList<String> claimed_icons = new ArrayList<String>();
        ArrayList<Integer> claimed_icons_int = new ArrayList<Integer>();

        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                COLUMN_USER_CLAIMED_ICONS
        };

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst() && !cursor.getString(cursor.getColumnIndex(COLUMN_USER_CLAIMED_ICONS)).isEmpty() ) {
            string = cursor.getString(cursor.getColumnIndex(COLUMN_USER_CLAIMED_ICONS));
            claimed_icons = new ArrayList<String>(Arrays.asList(string.split(", ")));
            hasIcons = true;
        } else {
            Log.e("DBHELPER", "No claimed icons were found in db");
        }

        cursor.close();
        db.close();

        if(hasIcons) {
            for (int i = 0; i < claimed_icons.size(); i++) {
                claimed_icons_int.add(Integer.parseInt(claimed_icons.get(i)));
            }
            return claimed_icons_int;
        } else {
            claimed_icons_int = new ArrayList<Integer>();
            return claimed_icons_int;
        }


    }

    public void addClaimedIcon (int claimed_icon, Context context) {

        ArrayList<Integer> existing = getClaimedIcons();

        if(existing.contains(claimed_icon)) {
            Log.e("DB ADDCLAIMEDICON", "This icon is already claimed!");
            Toast.makeText(context, "You already own this!", Toast.LENGTH_SHORT).show();
            return;
        }

        existing.add(claimed_icon);

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_CLAIMED_ICONS, String.valueOf(existing).replaceAll("\\[", "").replaceAll("\\]",""));

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_USER, values,
                null, null);

        db.close();
    }

    public ArrayList<String> getFriends() {
        String db_ids;
        ArrayList<String> result;

        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                COLUMN_USER_FRIENDSIDS
        };

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            db_ids = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FRIENDSIDS));
            Log.e("GETFRIENDS DB", "Received friends ids: " + db_ids.toString());
            result = new ArrayList<String>(Arrays.asList(db_ids.split(", ")));
        } else {
            Log.e("DBHELPER", "No friends data was found in db");
            result = null;
        }

        cursor.close();
        db.close();

        return result;
    }

    public void addFriend (int friend_id) {
        String db_ids;

        SQLiteDatabase db_read = getReadableDatabase();

        String[] columns = {
                COLUMN_USER_FRIENDSIDS
        };

        Cursor cursor = db_read.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            db_ids = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FRIENDSIDS));
        } else {
            Log.e("DBHELPER", "No friends data was found in db");
            db_ids = null;
        }

        cursor.close();
        db_read.close();

        if(db_ids != null && !db_ids.contains(String.valueOf(friend_id)) && !db_ids.isEmpty()) {
            db_ids = db_ids + ", " + friend_id;
        }

        if(db_ids != null && db_ids.isEmpty()) {
            db_ids = String.valueOf(friend_id);
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FRIENDSIDS, db_ids);

        SQLiteDatabase db_write = getWritableDatabase();
        db_write.update(TABLE_USER, values,
                null, null);

        db_write.close();
    }

    public void removeFriend(int friend_id_int) {
        String db_ids;
        ArrayList<String> db_ids_str;

        SQLiteDatabase db_read = getReadableDatabase();

        String[] columns = {
                COLUMN_USER_FRIENDSIDS
        };

        Cursor cursor = db_read.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            db_ids = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FRIENDSIDS));
        } else {
            Log.e("DBHELPER", "No friends data was found in db");
            db_ids = null;
        }

        cursor.close();
        db_read.close();

        String friend_id = String.valueOf(friend_id_int);
        String result;
        String TAG = "FRIEND REMOVAL";

        if(db_ids != null && db_ids.contains(friend_id) && !db_ids.isEmpty()) {
            Log.e(TAG, "Db_ids from db: " + db_ids);
            db_ids_str = new ArrayList<String>(Arrays.asList(db_ids.split(", ")));
            Log.e(TAG, "DB_IDS_STR : " + db_ids_str.toString());
            db_ids_str.remove(friend_id);
            Log.e(TAG, "DB_IDS_STR after REMOVED FRIEND : " + db_ids_str.toString());
            result = Arrays.toString(db_ids_str.toArray());
            Log.e(TAG, "Result string : " + result);
            result = result.replaceAll("\\[", "");
            result = result.replaceAll("\\]","");
            Log.e(TAG, "Result after bracket removal : " + result);
        } else {
            Log.e("FRIEND REMOVAL", "Unable to remove friend - null, is not your friend or friends list is empty");
            result = null;
        }



        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FRIENDSIDS, result);

        SQLiteDatabase db_write = getWritableDatabase();
        db_write.update(TABLE_USER, values,
                null, null);

        db_write.close();

    }

    public String getLanguage() {
        String lang;

        String[] columns = {
                COLUMN_USER_LANGUAGE
        };

        SQLiteDatabase db_read = getReadableDatabase();

        Cursor cursor = db_read.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            lang = cursor.getString(cursor.getColumnIndex(COLUMN_USER_LANGUAGE));
        } else {
            Log.e("DBHELPER", "No language data was found in db");
            lang = null;
        }

        cursor.close();
        db_read.close();

        return lang;
    }

    public void setLanguage(String lang) {

        //Setting the values
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_LANGUAGE, lang);

        //Getting a writiable database and updating tables
        SQLiteDatabase db_write = getWritableDatabase();
        db_write.update(TABLE_USER, values,
                null, null);
        db_write.close();
    }

    public void setIcon(int iconID) {

        //Setting the values
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ICONID, iconID);

        //Getting a writiable database and updating tables
        SQLiteDatabase db_write = getWritableDatabase();
        db_write.update(TABLE_USER, values,
                null, null);
        db_write.close();
    }

    public int getIcon() {
        int icon;

        SQLiteDatabase db_read = getReadableDatabase();

        String[] columns = {
                COLUMN_USER_ICONID
        };

        Cursor cursor = db_read.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            icon = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ICONID));
        } else {
            Log.e("DBHELPER", "No language data was found in db");
            icon = -1;
        }

        cursor.close();
        db_read.close();

        return icon;
    }

    public void setLastSteps(int lastSteps) {
        //Setting the values
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_LAST_STEPS, lastSteps);

        //Getting a writable database and updating tables
        SQLiteDatabase db_write = getWritableDatabase();
        db_write.update(TABLE_USER, values,
                null, null);
        db_write.close();
    }

    public int getLastSteps() {
        int lastSteps;

        SQLiteDatabase db_read = getReadableDatabase();

        String[] columns = {
                COLUMN_USER_LAST_STEPS
        };

        Cursor cursor = db_read.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order

        if (cursor.moveToFirst()) {
            lastSteps = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_LAST_STEPS));
        } else {
            Log.e("DBHELPER", "No last step data was found in db");
            lastSteps = -1;
        }

        cursor.close();
        db_read.close();

        return lastSteps;
    }

//    public ArrayList<Base.DateStepsModel> readStepsEntries()
//    {
//        ArrayList<Base.DateStepsModel> mStepCountList = new ArrayList<Base.DateStepsModel>();
//        String selectQuery = "SELECT * FROM " + TABLE_STEPS;
//        try {
//
//            SQLiteDatabase db = this.getReadableDatabase();
//            Cursor c = db.rawQuery(selectQuery, null);
//            if (c.moveToFirst()) {
//                do {
//                    Base.DateStepsModel dateStepsModel = new Base.DateStepsModel();
//                    dateStepsModel.date = c.getString((c.getColumnIndex(COLUMN_STEPS_DATE)));
//                    dateStepsModel.stepCount = c.getInt((c.getColumnIndex(COLUMN_STEPS_STEPCOUNT)));
//                    mStepCountList.add(dateStepsModel);
//                } while (c.moveToNext());
//            }
//            db.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return mStepCountList;
//    }
}
