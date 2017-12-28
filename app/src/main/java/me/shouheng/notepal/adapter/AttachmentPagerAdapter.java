package me.shouheng.notepal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import me.shouheng.notepal.fragment.ImageFragment;
import me.shouheng.notepal.model.Attachment;


public class AttachmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Attachment> attachments;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public AttachmentPagerAdapter(FragmentManager fragmentManager, List<Attachment> attachments) {
        super(fragmentManager);
        this.attachments = attachments;
    }

    @Override
    public Fragment getItem(int pos) {
        Attachment attachment = this.attachments.get(pos);
        return ImageFragment.newInstance(attachment);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return attachments.size();
    }
}