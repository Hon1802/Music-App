package hcmute.edu.vn.mp3app.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tunebox.db";
    public static final int DATABASE_VERSION = 1;

    // Table user
    public static final String TABLE_USER_NAME = "user";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_AVATAR = "avatar";

    private static final String DATABASE_USER_CREATE = "create table " +
            TABLE_USER_NAME+"("
            +COLUMN_ID+" integer primary key autoincrement, "
            +COLUMN_NAME+" text not null, "
            +COLUMN_USERNAME+" text not null, "
            +COLUMN_PASSWORD+" text not null, "
            +COLUMN_EMAIL +" text not null, "
            +COLUMN_AVATAR + " text not null)";

    public DB (Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_USER_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(DB.class.getName(),"Upgrading database from version "+oldVersion+" to "+newVersion);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER_NAME);
        onCreate(db);
    }
}
