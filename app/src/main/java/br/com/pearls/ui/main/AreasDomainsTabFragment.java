package br.com.pearls.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.com.pearls.DB.AreasViewModel;
import br.com.pearls.DB.AreasWithDomains;
import br.com.pearls.DB.Domain;
import br.com.pearls.DB.DomainsViewModel;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.R;
import br.com.pearls.utils.LiveDataUtil;
import br.com.pearls.utils.RemoveDiacritics;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class AreasDomainsTabFragment extends Fragment
        implements AreasDomainsTabSection.OnAreasDomainsTabSectionClick,
        NewAreaDialog.OnNewAreaInput,
        NewDomainDialog.OnNewDomainInput,
        EditAreaDialog.OnAreaEditIFace,
        EditDomainDialog.OnEditDomain {

    private static final String TAG = AreasDomainsTabFragment.class.getName();

    private SectionedRecyclerViewAdapter sectionAdapter;

    private AreasViewModel areasViewModel;
    private DomainsViewModel domainsViewModel;
    private Observer<KnowledgeArea[]> areaObserver;
    private Observer<Domain[]> domainObserver;

    private KnowledgeArea mSelectedArea;
    private Domain mSelectedDomain;
    private OnDomainSelectedListener domainListener;

    /* TODO: merge area create/edit and domain create/edit into one single dialog and insert/update code
     */
    public AreasDomainsTabFragment() {/* Default constructor: prevents app from crashing when finishing */}

    @Override
    public void onAreaEdit(String areaName) {
        if (areaName.isEmpty()) {
            return;
        }
        String area_ascii = RemoveDiacritics.removeDiacritics(areaName).toLowerCase();
        Log.v(TAG, "got area '" + areaName + "' to replace '" + mSelectedArea.getArea() + "'");
        Log.v(TAG, "area_ref = " + mSelectedArea.getId());
        KnowledgeArea area = new KnowledgeArea(mSelectedArea);
        area.setArea(areaName);
        area.setArea_ascii(area_ascii);
        Observer<KnowledgeArea[]> areaObserver = new Observer<KnowledgeArea[]>() {
            @Override
            public void onChanged(KnowledgeArea[] areas) {
                if (areas.length > 0) {
                    for (KnowledgeArea dbArea : areas) {
                        if (dbArea.getArea_ascii().equals(area.getArea_ascii()) &&
                                dbArea.getId() != area.getId()) {
                            Toast.makeText(getContext(),
                                    "Area '" + dbArea.getArea() + "' already exists",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                Log.v(TAG, "passed: will update area");
                area.debugDump();
                areasViewModel.update(area);
                Toast.makeText(getContext(),
                        "Area '" + areaName + "' edited successfully",
                        Toast.LENGTH_SHORT).show();
            }
        };
        LiveDataUtil.observeOnce(areasViewModel.getAreaByName(area_ascii), areaObserver);
    }

    @Override
    public String getKnowledgeArea() {
        return mSelectedArea.getArea();
    }

    @Override
    public void onEditDomainInput(String domainName) {

        if (domainName.isEmpty()) {
            return;
        }
        // IMPORTANT! Shallow-copy new Domain obj from selected domain instead of using reference to it;
        // otherwise we'll be working on the click-bound Domain obj in the AreasDomainsTabFragment
        // instance directly, so that any changes we make here would show on the AreasDomainsTabFragment
        // recycler view, which we don't want.
        Domain domain = new Domain(mSelectedDomain);
        domain.setDomain(domainName);
        domain.setDomain_ascii(RemoveDiacritics.removeDiacritics(domainName).toLowerCase());
        Observer<Domain[]> domainObserver = new Observer<Domain[]>() {
            @Override
            public void onChanged(Domain[] domains) {
                if (domains.length > 0) {
                    for (Domain d : domains) {
                        if (d.getDomain_ascii().equals(domain.getDomain_ascii()) &&
                                d.getId() != domain.getId()) {
                            Toast.makeText(getContext(),
                                    "Domain '" + d.getDomain() + "' already exists",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                Log.v(TAG, "passed: will update domain");
                domainsViewModel.update(domain);
                mSelectedDomain = new Domain(domain);
                domainListener.setSelectedDomain(mSelectedArea, mSelectedDomain);
                Toast.makeText(getContext(),
                        "Domain '" + domain.getDomain() + "' edited successfully",
                        Toast.LENGTH_SHORT).show();
            }
        };
        LiveDataUtil.observeOnce(
                domainsViewModel.getDomainByName(domain.getDomain_ascii(),
                                                 domain.getArea_ref()), domainObserver);
    }

    @Override
    public String getDomainName() {
        return mSelectedDomain.getDomain();
    }

    public interface OnDomainSelectedListener {
        void setSelectedDomain(KnowledgeArea area, Domain domain);
    }

    @Override
    public void onDomainClicked(@NonNull KnowledgeArea area, @NonNull Domain domain) {
        domainListener.setSelectedDomain(area, domain);
    }

    @Override
    public boolean onDomainLongClicked(@NonNull KnowledgeArea area, @NonNull Domain domain) {
        mSelectedArea = new KnowledgeArea(area);
        mSelectedDomain = new Domain(domain);
        domainListener.setSelectedDomain(area, domain);
        // open domain name edit dialog
        EditDomainDialog dlg = new EditDomainDialog();
        dlg.setTargetFragment(AreasDomainsTabFragment.this, 1);
        dlg.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "AreasDomainsTabFragment");
        return false;
    }

    @Override
    public void onNewDomainInput(String domainName) {
        if (domainName.isEmpty()) {
            return;
        }
        String domain_ascii = RemoveDiacritics.removeDiacritics(domainName).toLowerCase();
        Log.v(TAG, "Got new domain '" + domainName + "' to insert into: " + mSelectedArea.getArea());
        Log.v(TAG, "domain_ascii = '" + domain_ascii + "' *************************");
        Log.v(TAG, "area_ref = " + mSelectedArea.getId());
        domainObserver = new Observer<Domain[]>() {
            @Override
            public void onChanged(Domain[] domains) {
                if (domains.length > 0) {
                    Toast.makeText(getContext(),
                            "Domain '" + domains[0].getDomain() + "' already exists",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.v(TAG, "passed: will insert domain");
                Domain domain = new Domain();
                domain.setDomain(domainName);
                domain.setDomain_ascii(domain_ascii);
                domain.setArea_ref(mSelectedArea.getId());
                domainsViewModel.insert(domain);
                Toast.makeText(getContext(),
                        "Domain '" + domainName + "' created successfully",
                        Toast.LENGTH_SHORT).show();
                sectionAdapter.notifyDataSetChanged();
            }
        };
        LiveDataUtil.observeOnce(domainsViewModel.getDomainByName(domain_ascii, mSelectedArea.getId()),
                domainObserver);
    }

    @Override
    public boolean onHeaderLongClicked(@NonNull KnowledgeArea area, @NonNull AreasViewHolder viewHolder) {
        mSelectedArea = new KnowledgeArea(area);
        return deployHeaderLongClickMenu(area, viewHolder);
    }

    @Override
    public void onNewAreaInput(String area) {
        if (area.isEmpty()) {
            return;
        }
        String area_ascii = RemoveDiacritics.removeDiacritics(area).toLowerCase();
        areaObserver = new Observer<KnowledgeArea[]>() {
            @Override
            public void onChanged(KnowledgeArea[] knowledgeAreas) {
                if (knowledgeAreas.length > 0) {
                    Toast.makeText(getContext(),
                            "Area '" + knowledgeAreas[0].getArea() + "' already exists",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.v(TAG, "passed to insert");

                KnowledgeArea knowledgeArea = new KnowledgeArea();
                knowledgeArea.setArea(area);
                knowledgeArea.setArea_ascii(area_ascii);
                areasViewModel.insert(knowledgeArea);
                Toast.makeText(getContext(),
                        "Area '" + area + "' created successfully",
                        Toast.LENGTH_SHORT).show();
            }
        };
        LiveDataUtil.observeOnce(areasViewModel.getAreaByName(area_ascii), areaObserver);
    }

    @Override
    public void onHeaderClicked(@NonNull AreasDomainsTabSection section) {
        final SectionAdapter adapterForSection = sectionAdapter.getAdapterForSection(section);

        // store info of current section state before changing its state
        final boolean wasExpanded = section.isExpanded();
        final int previousItemsTotal = section.getContentItemsTotal();

        section.setExpanded(!wasExpanded);
        adapterForSection.notifyHeaderChanged();

        if (wasExpanded) {
            adapterForSection.notifyItemRangeRemoved(0, previousItemsTotal);
        } else {
            adapterForSection.notifyAllItemsInserted();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_areas_domains, container, false);

        FloatingActionButton fab = root.findViewById(R.id.area_create_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAreaCreateDialog();
            }
        });

        sectionAdapter = new SectionedRecyclerViewAdapter();

        RecyclerView recyclerView = root.findViewById(R.id.areas_domains_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        domainsViewModel = new ViewModelProvider(this).get(DomainsViewModel.class);

        // this will update the recycler view after any change performed by any of this
        // class methods; so, there's no need to call notifyDataSetChanged() in any of those.
        areasViewModel = new ViewModelProvider(this).get(AreasViewModel.class);
        areasViewModel.getmAreasWithDomains().observe(getViewLifecycleOwner(),
                                                    new Observer<List<AreasWithDomains>>() {
            @Override
            public void onChanged(List<AreasWithDomains> areasWithDomains) {
                sectionAdapter.removeAllSections();
                for (AreasWithDomains awd : areasWithDomains) {
                    AreasDomainsTabSection section =
                            new AreasDomainsTabSection(awd, AreasDomainsTabFragment.this);
                    sectionAdapter.addSection(section);
                }
                sectionAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }

    private void openAreaCreateDialog() {
        NewAreaDialog dlg = new NewAreaDialog();
        dlg.setTargetFragment(AreasDomainsTabFragment.this, 1);
        dlg.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "AreasDomainsTabFragment");
    }

    private boolean deployHeaderLongClickMenu(@NonNull KnowledgeArea area, @NonNull AreasViewHolder viewHolder) {
        PopupMenu menu = new PopupMenu(getContext(), viewHolder.headerView);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.area_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.domain_add:
                        Log.v(TAG, "******************* menu add domain selected");
                        openDomainCreateDialog();
                        return true;
                    case R.id.area_edit:
                        Log.v(TAG, "******************* menu edit area selected");
                        openAreaEditDialog();
                        return true;
                    case R.id.area_delete:
                        Log.v(TAG, "******************* menu area delete selected");
                        return true;
                }
                return false;
            }
        });
        menu.show();
        return true;
    }

    private void openDomainCreateDialog() {
        NewDomainDialog dlg = new NewDomainDialog();
        dlg.setTargetFragment(AreasDomainsTabFragment.this, 1);
        dlg.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "AreasDomainsTabFragment");
    }

    private void openAreaEditDialog() {
        EditAreaDialog dlg = new EditAreaDialog();
        dlg.setTargetFragment(AreasDomainsTabFragment.this, 1);
        dlg.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "AreasDomainsTabFragment");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            domainListener = (OnDomainSelectedListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("You must implement the OnDomainSelectedListener interface...");
        }
    }
}















