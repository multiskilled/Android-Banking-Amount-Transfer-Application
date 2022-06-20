package com.example.banking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    CardView topUpBtn, transferBtn ;
    TextView balanceTv;
    String cardNumber;
    LocalDataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db=new LocalDataBaseHelper(this);
        findAllViews();

        //get the card number
        cardNumber=getIntent().getExtras().getString("cardNumber");
        balanceTv.setText("$" + db.getUserBalance(cardNumber));

        topUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTopUpDialog(cardNumber);
            }
        });

        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTranferDialog(cardNumber);
            }
        });
    }

    public void findAllViews(){
        topUpBtn = findViewById(R.id.topup_btn);
        transferBtn = findViewById(R.id.tranfermoney_btn);
        balanceTv = findViewById(R.id.account_balance_tv);
    }

    public void showTranferDialog(final String sourceCardNumber){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.transfer_dialog_layout, null);

        final EditText amountEt = dialogView.findViewById(R.id.ammount_et);
        final EditText targetAccountEt = dialogView.findViewById(R.id.tcn_et);

        CardView sendMoneybtn = dialogView.findViewById(R.id.send_money_btn);

        sendMoneybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String amount=amountEt.getText().toString().trim();
                    String targetCardNumber=targetAccountEt.toString().trim();
                    db.transferMoney(cardNumber,targetCardNumber,amount);
                balanceTv.setText("$"+db.getUserBalance(cardNumber));
                //dismiss dialog
                dialogBuilder.dismiss();
            }
        });



        dialogBuilder.setView(dialogView);
        dialogBuilder.show();


    }

    public void showTopUpDialog(final String sourceCardNumber){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.topup_dialog_layout, null);

        final EditText amountEt = dialogView.findViewById(R.id.ammount_et);
        CardView topUpbtn = dialogView.findViewById(R.id.send_money_btn);

        topUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.topUp(cardNumber,amountEt.getText().toString())){
                    Toast.makeText(HomeActivity.this,"Top up is successful",Toast.LENGTH_SHORT).show();
                    //if susscssful then update the text view
                    balanceTv.setText("$"+db.getUserBalance(cardNumber));
                    //dismiss dialog
                    dialogBuilder.dismiss();
                }
            }
        });



        dialogBuilder.setView(dialogView);
        dialogBuilder.show();


    }

}