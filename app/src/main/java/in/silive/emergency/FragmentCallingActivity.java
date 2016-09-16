package in.silive.emergency;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;


public class FragmentCallingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters params;
    String MyChat = "Chat";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentcallingactivity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        sharedPreferences = getSharedPreferences(MyChat, MODE_PRIVATE);
        String chat = (sharedPreferences.getString("chat", ""));
        if(chat.equals("hospital")){
            Intent i = new Intent(this ,MapsActivity.class );
            i.putExtra("type","hospital");
            startActivity(i);
        }
        if(chat.equals("pharmacy")){
            Intent i = new Intent(this ,MapsActivity.class );
            i.putExtra("type","pharmacy");
            startActivity(i);
        }
        if(chat.equals("police")){
            Intent i = new Intent(this ,MapsActivity.class );
            i.putExtra("type","police");
            startActivity(i);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TypeOfEmergency(), "EMERGENCY'S");
        adapter.addFragment(new ContactsFragment(), "CONTACTS");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.emergencymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.profile){

            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);

        }
        if(id == R.id.flashlight){
            hasFlash = getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if (!hasFlash) {
                // device doesn't support flash
                // Show alert message and close the application

                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry, your device doesn't support flash light!");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.setCancelable(true);
                    }
                });
                alertDialog.create();
                alertDialog.show();

            }

            getCamera();

            if (isFlashOn) {
                // turn off flash
                turnOffFlash();
            } else {
                // turn on flash
                turnOnFlash();
            }

        }
        if(id == R.id.addContacts){
            Intent intent = new Intent(this , SelectContacts.class);
            startActivity(intent);

        }

        if(id == R.id.deleteContacts){

            Intent intent = new Intent(this , DeleteContacts.class);
            startActivity(intent);

        }
        if(id == R.id.mstartChatHead){

            Intent intent = new Intent(this , ChatHeadService.class);
            startService(intent);

        }
        if(id == R.id.mstopChatHead){

            Intent intent = new Intent(this , ChatHeadService.class);
            stopService(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    private void turnOnFlash() {
        if(!isFlashOn){
            if(camera == null || params == null)
                return;
        }
        params =camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        isFlashOn=true;
    }

    private void turnOffFlash() {

        if(isFlashOn){
            if(camera == null || params == null)
                return;
        }
        try {
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
        turnOffFlash();
    }
    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}