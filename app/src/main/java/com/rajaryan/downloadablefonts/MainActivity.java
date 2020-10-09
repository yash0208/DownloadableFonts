package com.rajaryan.downloadablefonts;



import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;

import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR;
import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND;
import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_UNAVAILABLE;
import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_MALFORMED_QUERY;
import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_PROVIDER_NOT_FOUND;
import static androidx.core.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_WRONG_CERTIFICATES;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private EditText editTextFont;
    private Button buttonLoadFont;

    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);
        editTextFont = findViewById(R.id.edit_text_font);
        buttonLoadFont = findViewById(R.id.button_load_font);

        buttonLoadFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFont();
            }
        });

        handlerThread = new HandlerThread("FontThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private void loadFont() {
        String query = editTextFont.getText().toString();

        FontRequest request = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                query,
                R.array.com_google_android_gms_fonts_certs
        );

        FontsContractCompat.FontRequestCallback callback =
                new FontsContractCompat.FontRequestCallback() {
                    @Override
                    public void onTypefaceRetrieved(Typeface typeface) {
                        textView.setTypeface(typeface);
                    }

                    @Override
                    public void onTypefaceRequestFailed(int reason) {
                        showError(reason);
                    }
                };

        FontsContractCompat.requestFont(this, request, callback, handler);
    }

    private void showError(int reason) {
        String errorText;

        switch (reason) {
            case FAIL_REASON_PROVIDER_NOT_FOUND:
                errorText = "FAIL_REASON_PROVIDER_NOT_FOUND";
                break;
            case FAIL_REASON_FONT_NOT_FOUND:
                errorText = "FAIL_REASON_FONT_NOT_FOUND";
                break;
            case FAIL_REASON_FONT_LOAD_ERROR:
                errorText = "FAIL_REASON_FONT_LOAD_ERROR";
                break;
            case FAIL_REASON_FONT_UNAVAILABLE:
                errorText = "FAIL_REASON_FONT_UNAVAILABLE";
                break;
            case FAIL_REASON_MALFORMED_QUERY:
                errorText = "FAIL_REASON_MALFORMED_QUERY";
                break;
            case FAIL_REASON_WRONG_CERTIFICATES:
                errorText = "FAIL_REASON_WRONG_CERTIFICATES";
                break;
            default:
                errorText = "Unknown error number: " + reason;
                break;
        }

        Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }
}