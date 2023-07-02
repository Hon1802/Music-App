package hcmute.edu.vn.mp3app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.mp3app.database.DB;
import hcmute.edu.vn.mp3app.model.User;

public class UserDAO {
    private SQLiteDatabase db;
    private final DB my_db;
    private final String[] allColumns={DB.COLUMN_ID,DB.COLUMN_NAME,DB.COLUMN_USERNAME,DB.COLUMN_PASSWORD,DB.COLUMN_EMAIL,
            DB.COLUMN_AVATAR};

    public UserDAO(Context context) {
        my_db = new DB(context);
        db = my_db.getWritableDatabase();
    }
    public void open() throws SQLException
    {
        db = my_db.getWritableDatabase();
    }

    public void close()
    {
        my_db.close();
    }

    public User createUser(String uname, String pass, String name, String email, String avatar)
    {
        ContentValues values=new ContentValues();
        values.put(DB.COLUMN_NAME,name);
        values.put(DB.COLUMN_USERNAME,uname);
        values.put(DB.COLUMN_PASSWORD,pass);
        values.put(DB.COLUMN_EMAIL,email);
        values.put(DB.COLUMN_AVATAR,avatar);

        int insertID= (int) db.insert(DB.TABLE_USER_NAME,null,values);
        Cursor cursor=db.query(DB.TABLE_USER_NAME,allColumns,DB.COLUMN_ID+ " = "+insertID,null,null,null,null);
        cursor.moveToFirst();
        User acc = cursortoUser(cursor);
        cursor.close();
        return acc;
    }

    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DB.COLUMN_NAME, user.getName());
        values.put(DB.COLUMN_USERNAME, user.getUsername());
        values.put(DB.COLUMN_PASSWORD, user.getPassword());
        values.put(DB.COLUMN_EMAIL, user.getEmail());
        values.put(DB.COLUMN_AVATAR, user.getAvatar());

        int rowsAffected = db.update(DB.TABLE_USER_NAME, values,
                DB.COLUMN_ID + " = ?", new String[]{String.valueOf(user.getId())});

        return rowsAffected > 0;
    }

    public User getUserByID(int userID) {
        Cursor cursor = db.query(DB.TABLE_USER_NAME, allColumns, DB.COLUMN_ID + " = " + userID, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = cursortoUser(cursor);
            cursor.close();
            return user;
        } else {
            return null; // Return null if no user found with the given userID
        }
    }

    public User getUserByEmail(String email) {
        Cursor cursor = db.query(DB.TABLE_USER_NAME, allColumns, DB.COLUMN_EMAIL + " = '" + email + "'", null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = cursortoUser(cursor);
            cursor.close();
            return user;
        } else {
            return null; // Return null if no user found with the given userID
        }
    }

    public boolean isUsernameExists(String username) {
        Cursor cursor = db.query(DB.TABLE_USER_NAME, null,
                DB.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

    public boolean isEmailExists(String email) {
        Cursor cursor = db.query(DB.TABLE_USER_NAME, null,
                DB.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }


    public List<User> getAll()
    {
        List<User> ds=new ArrayList<>();

        Cursor cursor=db.query(DB.TABLE_USER_NAME,allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            User acc=cursortoUser(cursor);
            ds.add(acc);
            cursor.moveToNext();
        }
        cursor.close();
        return ds;
    }


    private User cursortoUser(Cursor cursor)
    {
        User user=new User();
        user.setId(cursor.getInt(0));
        user.setName(cursor.getString(1));
        user.setUsername(cursor.getString(2));
        user.setPassword(cursor.getString(3));
        user.setEmail(cursor.getString(4));
        user.setAvatar(cursor.getString(5));
        return user;
    }


}
