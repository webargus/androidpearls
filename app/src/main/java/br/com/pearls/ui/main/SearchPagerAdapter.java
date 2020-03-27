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
public class SearchPagerAdapter extends FragmentPagerAdapter {

    public static final int PEARLS_SEARCH_TAB_FRAGMENT_POSITION = 0;
    public static final int PEARLS_AREAS_TAB_FRAGMENT_POSITION = PEARLS_SEARCH_TAB_FRAGMENT_POSITION + 1;
    public static final int PEARLS_LANGUAGES_TAB_FRAGMENT_POSITION = PEARLS_AREAS_TAB_FRAGMENT_POSITION + 1;
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.search_tab_caption,
                                                      R.string.search_tab_caption_areas,
                                                      R.string.search_tab_caption_languages};

    private SearchTabFragment searchTabFragment;
    private AreasDomainsTabFragment areasTabFragment;
    private CsvReaderLanguagesFragment languagesFragment;
    private final Context mContext;

    public SearchPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        searchTabFragment = new SearchTabFragment();
        areasTabFragment = new AreasDomainsTabFragment();
        languagesFragment = new CsvReaderLanguagesFragment();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case PEARLS_SEARCH_TAB_FRAGMENT_POSITION:
                return searchTabFragment;
            case PEARLS_AREAS_TAB_FRAGMENT_POSITION:
                return areasTabFragment;
            case PEARLS_LANGUAGES_TAB_FRAGMENT_POSITION:
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
        // Show 3 total pages.
        return TAB_TITLES.length;
    }

}