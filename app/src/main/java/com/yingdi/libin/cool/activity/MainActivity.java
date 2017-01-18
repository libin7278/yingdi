package com.yingdi.libin.cool.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yingdi.libin.cool.R;
import com.yingdi.libin.cool.test.CatClass;
import com.yingdi.libin.cool.test.DogClass;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DogClass dog  = new DogClass();
        CatClass cat  = new CatClass();

    }
}
