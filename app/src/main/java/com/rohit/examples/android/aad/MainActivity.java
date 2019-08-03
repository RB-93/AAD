package com.rohit.examples.android.aad;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

/**
 * Class definition to handle Toast
 */
public class MainActivity extends AppCompatActivity {

    //Member variables for different view layouts
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    Button toastBtn;
    Button nextButton;
    Button custToastBtn;
    Button sbBtn;
    TextView textView;
    View viewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting layout view ID from resource
        relativeLayout = findViewById(R.id.parent);
        toastBtn = findViewById(R.id.btn1);
        custToastBtn = findViewById(R.id.btn2);
        nextButton = findViewById(R.id.next_btn);
        linearLayout = findViewById(R.id.next_btn_root);

        /*
         * Inflating a custom toast layout on parent view
         */
        LayoutInflater inflater = getLayoutInflater();
        viewLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

        sbBtn = findViewById(R.id.btn3);

        /*
         * Handling click events for next button
         * Defining intent to next activity
         */
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Method to show a normal Toast
     *
     * @param view View to be used for showing toast
     */
    public void showToast(View view) {
        Toast.makeText(getApplicationContext(), getText(R.string.toast_text), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to show a custom toast
     * @param view View to be used for showing custom toast
     */
    public void showCustomToast(View view) {

        // Getting text view ID and setting text string to show in Toast
        textView = viewLayout.findViewById(R.id.text);
        textView.setText(R.string.custom_toast_text);

        // Instantiating Toast object with current context and setting gravity, duration and view to be used.
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(viewLayout);
        toast.show();
    }

    /**
     * Method to handle SnackBar
     * @param view View to be used for showing SnackBar
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showSnackBar(View view) {

        // Instantiating SnackBar object passing in view to used, text to show and duration
        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_text, Snackbar.LENGTH_LONG);

        //Positioning the SnackBar relative to other views
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, linearLayout.getHeight());
        snackbar.getView().setLayoutParams(layoutParams);

        /*
         * Setting action for SnackBar to be handled by user and showing it using show() method.
         */
        snackbar.setAction(R.string.sb_actiontext, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.show();
    }
}
