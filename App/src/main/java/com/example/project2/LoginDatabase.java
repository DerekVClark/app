package com.example.project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class LoginDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database"; //Database name

    public LoginDatabase(Context context) { //Constructor
        super(context, DATABASE_NAME, null, 1); //Calls the superclass constructor with the database name, null, and version 1
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // Creates tables
        db.execSQL("create table user(username text primary key, password text)"); //Created a table called user with two columns username and password (username is the primary key)
        db.execSQL("create table inventory(item_id INTEGER PRIMARY KEY, item_name TEXT, quantity INTEGER, username TEXT, FOREIGN KEY(username) REFERENCES user(username))"); //Created a table called inventory with three columns item_id, item_name, and quantity (item_id is the primary key), and a foreign key to the user table username column
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //On upgrade
        db.execSQL("drop table if exists user"); //If the table exists, it will be dropped
        db.execSQL("drop table if exists inventory"); //If the table exists, it will be dropped
        onCreate(db); //And the table will be created again updated

    }

    public boolean insertData(String username, String password) { //Inserts data into the user table
        SQLiteDatabase db = this.getWritableDatabase(); //Gets database
        ContentValues contentValues = new ContentValues(); //Creates a content values object
        contentValues.put("username", username); //Puts the username and password into the content values object
        contentValues.put("password", password);
        long result = db.insert("user", null, contentValues); //Inserts the content values object into the user table
        return result != -1; //Returns true if the result is not -1 (meaning it was inserted)
    }

    public boolean insertItem(Integer item_id, String item_name, Integer quantity, String username){ //Inserts data into the inventory table
        SQLiteDatabase db = this.getWritableDatabase(); //Gets database
        ContentValues contentValues = new ContentValues(); //Creates a content values object
        contentValues.put("item_id", item_id); //Puts the item_id, item_name, quantity, and username into the content values object
        contentValues.put("item_name", item_name);
        contentValues.put("quantity", quantity);
        contentValues.put("username", username);
        long result = db.insert("inventory", null, contentValues); //Inserts the content values object into the inventory table
        return result != -1;  //Returns true if the result is not -1 (meaning it was inserted)
    }

    public boolean checkExistsUser(String username){ //Checks if the username already exists in the user table
        SQLiteDatabase db = this.getReadableDatabase(); //Gets database
        String query = "select * from user where username = ?"; //Selects all from user table where username = ? as placeholder
        String[] args = {username}; //Sets the placeholder to the username
        Cursor cursor = db.rawQuery(query, args); //Queries the database with the query and args

        boolean exists = cursor.getCount() > 0; //Checks if the cursor count is greater than 0 meaning it exists

        cursor.close(); //Closes the cursor
        return exists; //returns the boolean value if username already exists or not
    }

    public boolean checkUser(String username, String password) { //Checks if the username and password match in the user table
        SQLiteDatabase db = this.getReadableDatabase(); //Gets database
        String query = "select * from user where username = ? and password = ?"; //queries username and password ? placeholders
        String[] args = {username, password}; //Sets the placeholders to the username and password
        Cursor cursor = db.rawQuery(query, args); //Queries the database with the query and args
        boolean exists = cursor.getCount() > 0; //Checks if the cursor count is greater than 0 meaning it exists
        cursor.close(); //Closes the cursor
        return exists; //returns the boolean value if username and password exists
    }

    public Cursor getUserPassword(String username){ //Gets all items from the inventory table where the username matches the username passed in
        SQLiteDatabase db = this.getReadableDatabase(); //Gets database
        String query = "select password from user where username = ?"; //queries username ? placeholder
        String[] args = {username}; //Sets the placeholder to the username
        return db.rawQuery(query, args); //Returns the cursor with the query and args
    }

    public Cursor getUserItems(String username){ //Gets all items from the inventory table where the username matches the username passed in
        SQLiteDatabase db = this.getReadableDatabase(); //Gets database
        String query = "select * from inventory where username = ?"; //queries username ? placeholder
        String[] args = {username}; //Sets the placeholder to the username
        return db.rawQuery(query, args); //Returns the cursor with the query and args
    }

    public void updateItem(Integer itemId, String item_name, Integer quantity){ //Updates the item in the inventory table where the item_id matches the item_id passed in
        SQLiteDatabase db = this.getWritableDatabase(); //Gets database
        ContentValues contentValues = new ContentValues(); //Creates a content values object
        contentValues.put("item_name", item_name); //Puts the item_name and quantity into the content values object
        contentValues.put("quantity", quantity);
        db.update("inventory", contentValues, "item_id = ?", new String[] {String.valueOf(itemId)}); //Updates the inventory table with the content values object where the item_id matches the item_id passed in
        db.close(); //Closes the database
    }
    public void deleteItem(Integer itemId) { //Deletes the item in the inventory table where the item_id matches the item_id passed in
        SQLiteDatabase db = this.getWritableDatabase(); //Gets database
        db.delete("inventory", "item_id = ?", new String[]{String.valueOf(itemId)}); //Deletes the inventory table where the item_id matches the item_id passed in
        db.close(); //Closes the database
    }
    public Cursor getZeroItems(String username) { //Gets all items from the inventory table where the quantity is 0
        SQLiteDatabase db = this.getReadableDatabase(); //Gets database
        String query = "select * from inventory where username = ? and quantity = 0"; //queries username ? placeholder
        String[] args = {username}; //Sets the placeholder to the username
        return db.rawQuery(query, args); //Returns the cursor with the query and args
    }
}
