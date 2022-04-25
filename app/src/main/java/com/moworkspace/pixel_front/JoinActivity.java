package com.moworkspace.pixel_front;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinActivity extends AppCompatActivity {

    private String BASE_URL="http://192.168.199.1:3001";
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private EditText name;
    private EditText email;
    private Button check;
    private EditText pass;
    private EditText passCheck;
    private Button emailAuthentication;
    public String EmailCode = " ";

    private LinearLayout email_check_Li;
    private Button email_check_button;
    private EditText email_check;

    private Button signupBtn;
    TextView emailName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joinin);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);

        pass=findViewById(R.id.pwd);
        passCheck=findViewById(R.id.pwd2);

        email_check_Li=findViewById(R.id.email_check_Li);
        email_check_button=findViewById(R.id.email_check_button);
        email_check=findViewById(R.id.email_check);

        emailAuthentication=findViewById(R.id.EmailAuthorizeBtn);
        emailAuthentication.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                email_check_Li.setVisibility(view.VISIBLE);
                // 인증메일 보내기
                HashMap<String, String> map = new HashMap<>();
                map.put("email", email.getText().toString());


//                Call<CheckResult> call = retrofitInterface.executeCheck(map);
                Call<com.moworkspace.pixel_front.CheckResult> call=retrofitInterface.executeCheck(map);
                call.enqueue(new Callback<com.moworkspace.pixel_front.CheckResult>() {
                    @Override
                    public void onResponse(Call<com.moworkspace.pixel_front.CheckResult> call, Response<com.moworkspace.pixel_front.CheckResult> response) {
                        if (true) {
                        //if (response.code() == 200) {
                            com.moworkspace.pixel_front.CheckResult result = response.body();

                            email_check_button.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View view){
                                    if((result.getChecking()).equals(email_check.getText().toString())) {
                                        Toast.makeText(JoinActivity.this, "인증이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                        email_check_button.setText("인증 완료");
                                    }
                                    else{
                                        Toast.makeText(JoinActivity.this, "인증번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                        else if(response.code() == 404){
                            Toast.makeText(JoinActivity.this, "404 오류", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.moworkspace.pixel_front.CheckResult> call, Throwable t) {
                        Toast.makeText(JoinActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
            }
        });
        //이메일 인증 요청  ***이메일 확인 다이얼로그 필요
        check=findViewById(R.id.EmailAuthorizeBtn);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        //회원가입 버튼
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!(pass.getText().toString().equals(passCheck.getText().toString()))){
                    Toast.makeText(JoinActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", name.getText().toString());
                    map.put("email", email.getText().toString());
                    String part[] = email.getText().toString().split("@");
                    map.put("password", pass.getText().toString());
                    custom_dialog(view);

                    Call<Void> call = retrofitInterface.executeSignup(map);
                    if(true){ //이메일 완료인지 봐야함
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(JoinActivity.this,
                                            "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);

                                } else if (response.code() == 400) {
                                    Toast.makeText(JoinActivity.this, "이미 가입된 정보입니다.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(JoinActivity.this, t.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(JoinActivity.this,
                                "이메일 인증이 완료되지 않았습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    public void custom_dialog(View v){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_joinin,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView ok_btn = dialogView.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //키보드 내리기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
