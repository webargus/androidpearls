package br.com.pearls.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import br.com.pearls.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class CsvReaderViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = SearchPagerAdapter.class.getName();

    public static final int PEARLS_CSV_MEDIA_TAB_FRAGMENT_POSITION = 0;
    public static final int PEARLS_CSV_AREAS_TAB_FRAGMENT_POSITION = PEARLS_CSV_MEDIA_TAB_FRAGMENT_POSITION + 1;
    public static final int PEARLS_CSV_LANGUAGES_TAB_FRAGMENT_POSITION = PEARLS_CSV_AREAS_TAB_FRAGMENT_POSITION + 1;
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.csv_tab_media, R.string.csv_tab_areas, R.string.csv_tab_languages};

    private CsvThreadReaderMediaFragment readerFragment;
    private AreasDomainsTabFragment areasTabFragment;
    private CsvReaderLanguagesFragment languagesFragment;
    private final Context mContext;

    @SuppressWarnings("deprecation")
    public CsvReaderViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        readerFragment = new CsvThreadReaderMediaFragment();
        readerFragment.setRetainInstance(true);
        areasTabFragment = new AreasDomainsTabFragment();
        languagesFragment = new CsvReaderLanguagesFragment();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case PEARLS_CSV_MEDIA_TAB_FRAGMENT_POSITION:
                return readerFragment;
            case PEARLS_CSV_AREAS_TAB_FRAGMENT_POSITION:
                return areasTabFragment;
            case PEARLS_CSV_LANGUAGES_TAB_FRAGMENT_POSITION:
                return languagesFragment;
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return TAB_TITLES.length;
    }

}