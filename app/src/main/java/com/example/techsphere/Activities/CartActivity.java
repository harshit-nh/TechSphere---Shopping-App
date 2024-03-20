package com.example.techsphere.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsphere.Adapters.CartListAdapter;
import com.example.techsphere.Helper.ChangeNumberItemsListener;
import com.example.techsphere.Helper.ManageCart;
import com.example.techsphere.Helper.TinyDB;
import com.example.techsphere.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ManageCart manageCart;
    private TextView totalFeeTxt, taxTxt,deliveryTxt,totalTxt, emptyTxt;
    private Button orderBtn, exploreBtn;
    private double tax;
    private ScrollView scrollView;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        manageCart = new ManageCart(this, new TinyDB(this));

        initView();
        setVariable();
        initList();
        calculateCart();

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountTxt = totalTxt.getText().toString();
                double  amount = Double.parseDouble(amountTxt.substring(1));
                payNow(String.valueOf(amount));
            }
        });
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(manageCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                calculateCart();
            }
        });

        recyclerView.setAdapter(adapter);
        if(manageCart.getListCart().isEmpty()){
            emptyTxt.setVisibility(View.VISIBLE);
            exploreBtn.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);


        }else{
            emptyTxt.setVisibility(View.GONE);
            exploreBtn.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private void calculateCart() {
        double percentTax = 0.14;
        double delivery = 90;
        tax = Math.round((manageCart.getTotalFee() * percentTax *100.0)) / 100.0;

        double itemTotal = Math.round(manageCart.getTotalFee() * 100.0)/100.0;
        double total = Math.round((manageCart.getTotalFee()+tax+delivery)*100.0)/100.0;

        totalFeeTxt.setText("₹"+itemTotal);
        taxTxt.setText("₹"+tax);
        deliveryTxt.setText("₹"+delivery);
        totalTxt.setText("₹"+total);
    }

    private void setVariable() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        recyclerView = findViewById(R.id.view3);
        scrollView = findViewById(R.id.scrollView3);
        backBtn = findViewById(R.id.backBtn1);
        emptyTxt = findViewById(R.id.emptyTxt);
        orderBtn = findViewById(R.id.orderBtn);
        exploreBtn = findViewById(R.id.exploreBtn);

    }

    private void payNow(String amount){
        final Activity activity = this;

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_neBRtdReAl9Bus");
        checkout.setImage(R.drawable.razor_pay);

        double finalAmount = Float.parseFloat(amount)*100;

        try{
            JSONObject options = new JSONObject();
            options.put("name","TechSphere");
            options.put("description","Reference No. #12345");
            options.put("image",R.drawable.razor_pay);
            options.put("theme.color","#3399cc");
            options.put("currency","INR");
            options.put("amount",finalAmount+"");
            options.put("prefill.email","something@gmail.com");
            options.put("prefill.contact","7017034157");

            checkout.open(activity,options);

        } catch (JSONException e) {
            Log.e("TAG", "Error in starting RazorPay Checkout",e);
        }

    }


    @Override
    public void onPaymentSuccess(String s) {

        //Clear cart items on successful payment
        manageCart.clearCart();

        //Refresh the cart view
        initList();

        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}