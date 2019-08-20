package com.andy_mitchell.flagsquizapp.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.andy_mitchell.flagsquizapp.Model.Question;
import com.andy_mitchell.flagsquizapp.Model.Ranking;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private  static String DB_NAME="MyDB.db";
    private static String DB_PATH = "";
    private SQLiteDatabase mDataBase;
    private Context mContent = null;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        DB_PATH=context.getApplicationInfo().dataDir+"/databases/";
        this.mContent = mContent;
    }

    public void openDatabase(){
        String myPath = DB_PATH+DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public void copyDatabase() throws IOException {
        try {
            InputStream myInput = mContent.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH+DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer,0,length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkDatabase(){
        SQLiteDatabase tempDB = null;
        try {
            String myPath = DB_PATH+DB_NAME;
            tempDB = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tempDB != null) {
            tempDB.close();
        }
        return tempDB != null;
    }

    public void createDatabase() throws IOException {
        boolean isDbExists = checkDatabase();
        if (isDbExists) {

        }else{
            this.getReadableDatabase();
            try{
                copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //CRUD for table
    public List<Question> getAllQuestions(){
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            c =db.rawQuery("SELECT * FROM Question ORDER BY Random()",null);
            if (c ==null) {
                return  null;
            }
            c.moveToFirst();
            do{
                int Id = c.getInt(c.getColumnIndex("ID"));
                String Image = c.getString(c.getColumnIndex("Image"));
                String AnswerA = c.getString(c.getColumnIndex("AnswerA"));
                String AnswerB = c.getString(c.getColumnIndex("AnswerB"));
                String AnswerC = c.getString(c.getColumnIndex("AnswerC"));
                String AnswerD = c.getString(c.getColumnIndex("AnswerD"));
                String CorrectAnswer = c.getString(c.getColumnIndex("CorrectAnswer"));

                Question question = new Question(Id,Image,AnswerA,AnswerB,AnswerC,AnswerD,CorrectAnswer);
                questionList.add(question);
            } while (c.moveToNext());
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return questionList;
    }

    //Insert Score to Ranking table
    public void insertScore(int score){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Score",score);
        db.insert("Ranking",null,contentValues);

    }

    // get score and sort ranking

    public List<Ranking> getRanking(){
        List<Ranking> rankingList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        try {
            c = db.rawQuery("SELECT * FROM Ranking ORDER BY Score DESC;",null);
            if (c == null) {
                return null;
            }
            c.moveToNext();
            do {
                int Id = c.getInt(c.getColumnIndex("Id"));
                int Score = c.getInt(c.getColumnIndex("Score"));

                Ranking ranking = new Ranking(Id,Score);
                rankingList.add(ranking);

            }
            while (c.moveToNext());
            c.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return rankingList;


    }

}
