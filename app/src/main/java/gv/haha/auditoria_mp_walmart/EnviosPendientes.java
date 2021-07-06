package gv.haha.auditoria_mp_walmart;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import gv.haha.auditoria_mp_walmart.clases.Variables;

public class EnviosPendientes extends AppCompatActivity {


    private ViewPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envios_pendientes);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mSectionsPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Set up the ViewPager with the sections adapter.
        mSectionsPagerAdapter.addFragment(FragmentEnvPendientesRevisionPdv.newInstance(Variables.ENV_PEND_REVIS_PDV),"Revisión Pdv");
        mSectionsPagerAdapter.addFragment(FragmentEnvPendientesEvaluacionDisplay.newInstance(Variables.ENV_PEND_EVAL_DISPLAY),"Evaluación Display");
        mSectionsPagerAdapter.addFragment(FragmentEnvPendientesActividadComercial.newInstance(Variables.ENV_PEND_ACTIVID_COMERC),"Actividad Comercial");
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

    }

    // Adapter for the viewpager using FragmentPagerAdapter
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
}
