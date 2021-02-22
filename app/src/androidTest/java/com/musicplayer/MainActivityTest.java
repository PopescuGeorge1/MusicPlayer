package com.musicplayer;



import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.Manifest;
import android.os.Environment;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static org.junit.Assert.*;

public class MainActivityTest{
    @Rule
    public ActivityTestRule<MainActivity> mActivityR = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mActivity = null;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityR.getActivity();
    }

    @Test
    public void grantPermissionTest(){
        /*check sd card availability*/
        Assert.assertEquals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);

    }

    @Test
    public void testViews(){
        View view = mActivity.findViewById(R.id.tab_layout);
        assertNotNull(view);
        view = mActivity.findViewById(R.id.viewPager);
        assertNotNull(view);
        view = mActivity.findViewById(R.id.frag_bottom_player);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mActivity=null;
    }
}