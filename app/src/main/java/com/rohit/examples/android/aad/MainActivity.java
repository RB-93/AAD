package com.rohit.examples.android.aad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    Button toast_btn;
    Button next_button;
    Button cust_toast_btn;
    Button sb_btn;
    TextView textView;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = findViewById(R.id.parent);
        toast_btn = findViewById(R.id.btn1);
        cust_toast_btn = findViewById(R.id.btn2);
        next_button = findViewById(R.id.next_btn);
        linearLayout = findViewById(R.id.next_btn_root);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

        sb_btn = findViewById(R.id.btn3);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showToast(View view) {
        Toast.makeText(getApplicationContext(), getText(R.string.toast_text), Toast.LENGTH_SHORT).show();
    }

    public void showCustomToast(View view) {

        textView = layout.findViewById(R.id.text);
        textView.setText(R.string.custom_toast_text);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_SHORT);;
        toast.setView(layout);
        toast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showSnackBar(View view) {

        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_text, Snackbar.LENGTH_LONG);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, linearLayout.getHeight());
        snackbar.getView().setLayoutParams(layoutParams);
        snackbar.setAction(R.string.sb_actiontext, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.show();
    }
}
