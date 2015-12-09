package com.gejiahui.toastmusic.activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.gejiahui.toastmusic.R;
import com.google.android.gms.appindexing.Action;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    ArrayList<Fragment> list = new ArrayList<Fragment>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.view_page);

        PlayListFragment playListFragment = new PlayListFragment();
        PlayListFragment playListFragment2 = new PlayListFragment();
        list.add(playListFragment);
        list.add(playListFragment2);
        ToastFragmentAdapter mAdapter = new ToastFragmentAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(mAdapter);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

    }



    class ToastFragmentAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> list1 = new ArrayList<Fragment>();

        public ToastFragmentAdapter(FragmentManager fm, ArrayList<Fragment> list1) {
            super(fm);
            this.list1 = list1;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return list1.get(position);
        }

        @Override
        public int getCount() {
            return list1.size();
        }
    }




}
