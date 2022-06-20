package com.example.banking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.Random;

public class LocalDataBaseHelper  {

        public SQLiteDatabase db;
        public Context context;


        //create user table
    public String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS USER_TABLE("+
                "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "NAME VARCHAR,"+
                "LAST_NAME VARCHAR,"+
                "AGE VARCHAR,"+
                "GENDER VARCHAR,"+
                "BALANCE VARCHAR DEFAULT 0,"+
                "CARDNUMBER VARCHAR);";


        //constructor to create a local database
    public LocalDataBaseHelper(Context c){

        this.context=c;
        //create a database
        this.db=context.openOrCreateDatabase("mydb",context.MODE_PRIVATE,null);
        //EXECUTE THE QUERY
        this.db.execSQL(CREATE_USER_TABLE);
        //close database after finishing task
        this.db.close();
    }

    //fucntion to get the user values
    public String insertNewUser(String name,String lastName,String age, String gender){
        this.db=context.openOrCreateDatabase("mydb",Context.MODE_PRIVATE,null);
        //generate new card number
        String cardNumber=generateCardNumber();

        ContentValues values=new ContentValues();
        values.put("NAME",name);
        values.put("LAST_NAME",lastName);
        values.put("AGE",age);
        values.put("GENDER",gender);
        values.put("CARDNUMBER",cardNumber);

        long r=this.db.insert("USER_TABLE","_ID",values);

        this.db.close();

        //if talbe is less than zero or -1 then there is something wrong with the input
        if(r!=-1){
            Toast.makeText(context,"new user inserted",Toast.LENGTH_SHORT).show();
            //return card number if the input works fine
            return cardNumber;
        }else{
            Toast.makeText(context,"Error while inserting user",Toast.LENGTH_SHORT).show();
            return "";//return null
        }


    }



    //FUNCTION TO GENERATE RANDOM NUMBER
    public String generateCardNumber() {
        String cardNumber;
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        //format card number
        //we are using regex to get round of 6 digit number
        cardNumber=String.format("%06",number);
        return cardNumber;
    }

        public String login(String cardNumber){
        this.db=context.openOrCreateDatabase("mydb",Context.MODE_PRIVATE,null);
            //return the list of results with cursor object
            Cursor cursor=this.db.rawQuery("Select * from USER_TABLE where CARDNUMBER="+cardNumber,null);



            if(cursor.getCount()==0) {
                Toast.makeText(context, "wrong card number", Toast.LENGTH_SHORT).show();
                this.db.close();
            return "";
            }else{
                this.db.close();
                return cardNumber;
            }
        }

        //method to get user balance
        public String getUserBalance(String cardNumber){
        String balance;
            this.db=context.openOrCreateDatabase("mydb",Context.MODE_PRIVATE,null);

            Cursor cursor=this.db.rawQuery("Select * from USER_TABLE where CARDNUMBER="+cardNumber,null);
            cursor.moveToFirst();
            balance=cursor.getString(cursor.getColumnIndex("BALANCE"));


            this.db.close();

        return balance;
        }


        //method for topup and if success, returns true else false
        public boolean topUp(String cardNumber,String amount){
            boolean done=false;
            float currentAmount=Float.parseFloat(this.getUserBalance(cardNumber));
            this.db=context.openOrCreateDatabase("mydb",Context.MODE_PRIVATE,null);
        //get current balance of user and set the updated balance

            float newAmount=currentAmount+Float.parseFloat(amount);
            ContentValues values=new ContentValues();
            values.put("BALANCE",String.valueOf(newAmount));
            //number of rows manipulated by this method
            int r=this.db.update("USER_TABLE",values,"CARDNUMBER=?",new String[]{cardNumber});
            if(r>0){
                done=true;
            }else{
                done=false;
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        this.db.close();
        return done;
        }
            //method to increase or decrease the balance in an account
        public boolean updateUserBalance(String cardNumber,String amount){
            boolean done=false;

            this.db=context.openOrCreateDatabase("mydb",Context.MODE_PRIVATE,null);

            ContentValues values=new ContentValues();
            values.put("BALANCE",amount);
            int r = this.db.update("USER_TABLE",values,"CARDNUMBER=?",new String[]{cardNumber});

            if(r>0){
                done=true;
            }else{
                done=false;
                Toast.makeText(context,"Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
        this.db.close();
            return done;

        }
        //method for transfer
    public void transferMoney(String cardNumber,String targetCardnumber,String amount){

            this.db=context.openOrCreateDatabase("mydb",Context.MODE_PRIVATE,null);
            //get the balance of source cardnumber that to send from and also the
            //balance of the target card number
        float sourceBalance=Float.parseFloat(this.getUserBalance(cardNumber));
        float targetBalance=Float.parseFloat(this.getUserBalance(targetCardnumber));
        float transferAmount=Float.parseFloat(amount);

        if(sourceBalance>=transferAmount){
            sourceBalance=sourceBalance-transferAmount;
            targetBalance=targetBalance+transferAmount;


            if(updateUserBalance(cardNumber,String.valueOf(sourceBalance))){

                //if it is done, then we have to do it for target balance also
                if(updateUserBalance(targetCardnumber,String.valueOf(targetBalance))){

                    Toast.makeText(context,"Transfer is Successful",Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(context,"Error!",Toast.LENGTH_SHORT).show();
                }


        }  else {

                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
            }
            }
            else {
            Toast.makeText(context, "Not enough Balance", Toast.LENGTH_SHORT).show();


    }

        this.db.close();
}
}
