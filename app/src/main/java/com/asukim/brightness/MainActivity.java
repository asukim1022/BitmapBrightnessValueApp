package com.asukim.brightness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final int REQ_CODE_SELECT_IMAGE = 100;
    Bitmap bitmap;
    Uri url;
    ImageView image;
    private int resizedBackgroundBrightness = 0;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //임의의 이미지를 선택하기 위해 갤러리 사용
        Button galleryBtn = (Button) findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //intent로 이미지 선택
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        image = (ImageView) findViewById(R.id.main_img);
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    url = data.getData();

                    image.setImageBitmap(bitmap);

                    //bitmap의 밝기값 계산
                    float[] temp = calculateBrightnessEstimate(bitmap, 1);
                    resizedBackgroundBrightness = (int) temp[0];

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int brightness = resizedBackgroundBrightness;
                int colorInt = getColorStringFromBrightness(brightness);

                //colorStr : bitmap이미지 위의 텍스트의 컬러값 출력
                String colorStr = "white";

                if (colorInt == 1) {
                    colorStr = "white";
                    //어두운 이미지에는 흰색의 텍스트 출력
                    textView.setTextColor(getResources().getColor(R.color.white));
                } else if (colorInt == 2) {
                    colorStr = "black";
                    //밝은 이미지에는 검정색의 텍스트 출력
                    textView.setTextColor(getResources().getColor(R.color.black));
                }

                Toast.makeText(getApplicationContext(), brightness + " : " + colorStr, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private float[] calculateBrightnessEstimate(Bitmap bitmap, int pixelSpacing) {
        int R = 0;
        int G = 0;
        int B = 0;
        int T = 0;
        float S = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing * 15) {
            int color = pixels[i];
            float[] hsv = new float[3];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            T += getColorToBrightness(color);
            Color.colorToHSV(color, hsv);
            S += hsv[1];
            n++;
        }

        //T = (R + B + G);
        float[] rtn = {(T / n), (S / n)};
        return rtn;
    }


    //컬러의 값을 밝기로값으로 가져오기
    private static int getColorToBrightness(int color) {
        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);
        return (int) Math.sqrt(R * R * .241 + G * G * .691 + B * B * .068);
    }


    //bitmap의 밝기값을 매개값에 넣는다. 리턴값으로 bitmap이미지위의 텍스트 색상 출력
    private int getColorStringFromBrightness(int brightness) {
        if (brightness < 195) {
            return 1;
        } else {
            return 2;
        }
    }
}



