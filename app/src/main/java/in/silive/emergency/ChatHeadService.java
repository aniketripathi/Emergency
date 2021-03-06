package in.silive.emergency;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shobhit-pc on 9/16/2016.
 */
public class ChatHeadService extends Service implements View.OnClickListener,View.OnKeyListener
{


    private WindowManager windowManager;
    private Button chatHead;
    WindowManager.LayoutParams params,params2;
    float screenWidth = 0, screenHeight = 0;

    View popupView;
    LinearLayout layout;

    String type = "";

    @Override
    public void onCreate() {
        super.onCreate();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layout = new LinearLayout(this);
        chatHead = new Button(this);
        chatHead.setBackgroundResource(R.drawable.logo);
        chatHead.setClickable(true);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.chatheadlayout, null);
        popupView.setVisibility(View.GONE);

        Button hospital = (Button) popupView.findViewById(R.id.bhospital);
        Button pharmacy = (Button) popupView.findViewById(R.id.bphar);
        Button police = (Button) popupView.findViewById(R.id.bpolic);
        Button contact = (Button) popupView.findViewById(R.id.bcontac);
        Button delete = (Button) popupView.findViewById(R.id.bdel);
        hospital.setOnClickListener(this);
        pharmacy.setOnClickListener(this);
        police.setOnClickListener(this);
        contact.setOnClickListener(this);
        delete.setOnClickListener(this);

        layout.addView(chatHead);
        layout.addView(popupView);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        popupView.setVisibility(View.GONE);

                        return true;
                    case MotionEvent.ACTION_UP:
                        if(popupView.getVisibility() == View.VISIBLE)
                            popupView.setVisibility(View.GONE);
                        else
                            popupView.setVisibility(View.VISIBLE);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(layout, params);
                        return true;


                }
                return false;
            }
        });
        windowManager.addView(layout, params);


    }

    @Override
    public void onStart(Intent intent, int startId) {


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return 1 ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.bhospital:
                type = "hospital";
                emergency();
                break;
            case R.id.bphar:
                type = "pharmacy";
                emergency();
                break;
            case R.id.bpolic:
                type = "police";
                emergency();
                break;
            case R.id.bcontac:
                contacts();
                break;
            case R.id.bdel:
                stopSelf();
                break;
        }
    }

    private void contacts() {
        Intent i = new Intent(getBaseContext(),FragmentCallingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.setClass(getBaseContext(), FragmentCallingActivity.class);
        i.putExtra("type" , type);
        startActivity(i);
        popupView.setVisibility(View.GONE);
        windowManager.removeView(layout);
        windowManager.addView(layout, params);

    }

    public void emergency(){


        Intent i = new Intent(getBaseContext(),MapsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.setClass(getBaseContext(), MapsActivity.class);
        i.putExtra("type" , type);
        startActivity(i);
        popupView.setVisibility(View.GONE);
        windowManager.removeView(layout);
        windowManager.addView(layout, params);
    }
    @Override
    public void onDestroy() {
        if (chatHead != null)
            windowManager.removeView(layout);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            Toast.makeText(getApplicationContext() , "asss" ,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;

        }

    }