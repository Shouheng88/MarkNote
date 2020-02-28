package me.shouheng.notepal.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.activity.ContainerActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.fragment.WebviewFragment;
import me.shouheng.commons.theme.ThemeStyle;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.notepal.BuildConfig;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityAboutBinding;

import static me.shouheng.commons.event.UMEvent.*;
import static me.shouheng.notepal.Constants.EMAIL_DEVELOPER;

/**
 * @author shouh
 * @version $Id: AboutActivity, v 0.1 2018/9/24 18:16 shouh Exp$
 */
@PageName(name = PAGE_ABOUT)
public class AboutActivity extends CommonActivity<ActivityAboutBinding> {

    public final static String APP_ABOUT_ARG_OPEN_SOURCE_ONLY = "__extra_app_about_open_source_only";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        /* Config base theme. */
        ThemeStyle themeStyle = ThemeUtils.getInstance().getThemeStyle();
        getBinding().setIsDarkTheme(themeStyle.isDarkTheme);
        getBinding().setVersionName(BuildConfig.VERSION_NAME);
        ThemeUtils.setStatusBarColor(this, themeStyle.isDarkTheme ? Color.BLACK :
                PalmUtils.isMarshmallow() ? Color.WHITE : PalmUtils.getColorCompact(R.color.light_theme_background_dark));

        /* Handle intent. */
        boolean openSourceOnly = false;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(APP_ABOUT_ARG_OPEN_SOURCE_ONLY)) {
            openSourceOnly = intent.getBooleanExtra(APP_ABOUT_ARG_OPEN_SOURCE_ONLY, false);
        }

        /* Config toolbar. */
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(
                    PalmUtils.getDrawableCompact(me.shouheng.commons.R.drawable.ic_arrow_back_black_24dp),
                    getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
        }
        getBinding().toolbar.setTitleTextColor(isDarkTheme() ? Color.WHITE : Color.BLACK);

        /* About entities. */
        List<AboutEntity> aboutEntities = new LinkedList<>();
        if (!openSourceOnly) {
            aboutEntities.add(AboutEntity.getSectionTitle(PalmUtils.getStringCompact(R.string.about_section_description)));
            aboutEntities.add(AboutEntity.getNormalText(Html.fromHtml(PalmUtils.getStringCompact(R.string.about_section_description_details))));
            aboutEntities.add(AboutEntity.getSectionTitle(PalmUtils.getStringCompact(R.string.about_section_developer)));
            aboutEntities.add(AboutEntity.getUser("WngShhng (" + EMAIL_DEVELOPER + ")", Constants.IMAGE_AVATAR_DEVELOPER,
                    PalmUtils.getStringCompact(R.string.about_section_developer_desc), Constants.PAGE_GITHUB_DEVELOPER));
            aboutEntities.add(AboutEntity.getSectionTitle(PalmUtils.getStringCompact(R.string.about_section_open_links)));
            String html = String.format(PalmUtils.getStringCompact(R.string.about_section_open_links_details),
                    Constants.PAGE_GITHUB_REPOSITORY, Constants.PAGE_CHANGE_LOGS, Constants.PAGE_UPDATE_PLAN, Constants.PAGE_ABOUT);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                aboutEntities.add(AboutEntity.getNormalText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)));
            } else {
                aboutEntities.add(AboutEntity.getNormalText(Html.fromHtml(html)));
            }
        }
        aboutEntities.add(AboutEntity.getSectionTitle(PalmUtils.getStringCompact(R.string.about_section_open_sources)));
        aboutEntities.add(AboutEntity.getLicense("EasyMark", "WngShhng",
                AboutEntity.License.APACHE_2, "https://github.com/Shouheng88/EasyMark"));
        aboutEntities.add(AboutEntity.getLicense("PhotoView", "Chris Banes",
                AboutEntity.License.APACHE_2, "https://github.com/chrisbanes/PhotoView"));
        aboutEntities.add(AboutEntity.getLicense("Material Dialog", "Aidan Michael Follestad",
                AboutEntity.License.MIT, "https://github.com/afollestad/material-dialogs"));
        aboutEntities.add(AboutEntity.getLicense("Glide", "Square",
                AboutEntity.License.APACHE_2, "https://github.com/bumptech/glide"));
        aboutEntities.add(AboutEntity.getLicense("Stetho", "Facebook",
                AboutEntity.License.BSD_3, "https://github.com/facebook/stetho"));
        aboutEntities.add(AboutEntity.getLicense("RxAndroid", "The RxAndroid authors",
                AboutEntity.License.APACHE_2, "https://github.com/ReactiveX/RxAndroid"));
        aboutEntities.add(AboutEntity.getLicense("RxJava", "RxJava Contributors",
                AboutEntity.License.APACHE_2, "https://github.com/ReactiveX/RxJava"));
        aboutEntities.add(AboutEntity.getLicense("RxBinding", "Jake Wharton",
                AboutEntity.License.APACHE_2, "https://github.com/JakeWharton/RxBinding"));
        aboutEntities.add(AboutEntity.getLicense("Joda-time", "January 2004",
                AboutEntity.License.APACHE_2, "https://github.com/JodaOrg/joda-time"));
        aboutEntities.add(AboutEntity.getLicense("Hello Charts", "Leszek Wach",
                AboutEntity.License.APACHE_2, "https://github.com/lecho/hellocharts-android"));
        aboutEntities.add(AboutEntity.getLicense("Floating Action Button", "Dmytro Tarianyk",
                AboutEntity.License.APACHE_2, "https://github.com/Clans/FloatingActionButton"));
        aboutEntities.add(AboutEntity.getLicense("BaseRecyclerViewAdapterHelper", "CymChad",
                AboutEntity.License.APACHE_2, "https://github.com/CymChad/BaseRecyclerViewAdapterHelper"));
        aboutEntities.add(AboutEntity.getLicense("CircleImageView", "Henning Dodenhof",
                AboutEntity.License.APACHE_2, "https://github.com/hdodenhof/CircleImageView"));
        aboutEntities.add(AboutEntity.getLicense("PinLockView", "aritraroy",
                AboutEntity.License.APACHE_2, "https://github.com/aritraroy/PinLockView"));

        /* Config adapter event. */
        AboutAdapter aboutAdapter = new AboutAdapter(getContext(), aboutEntities);
        aboutAdapter.setOnItemClickListener((adapter, view, position) -> {
            AboutEntity aboutEntity = aboutAdapter.getItem(position);
            assert aboutEntity != null;
            switch (aboutEntity.type) {
                case AboutEntity.typeUser:
                    ContainerActivity.open(WebviewFragment.class)
                            .put(WebviewFragment.ARGUMENT_KEY_URL, aboutEntity.user.website)
                            .put(WebviewFragment.ARGUMENT_KEY_USE_PAGE_TITLE, true)
                            .launch(this);
                    break;
                case AboutEntity.typeLicense:
                    ContainerActivity.open(WebviewFragment.class)
                            .put(WebviewFragment.ARGUMENT_KEY_URL, aboutEntity.license.url)
                            .put(WebviewFragment.ARGUMENT_KEY_USE_PAGE_TITLE, true)
                            .launch(this);
                    break;
            }
        });
        getBinding().list.setAdapter(aboutAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_rate:
                IntentUtils.openInMarket(getContext(), BuildConfig.APPLICATION_ID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean autoCustomMenu() {
        return false;
    }

    public static class AboutAdapter extends BaseMultiItemQuickAdapter<AboutEntity, BaseViewHolder> {

        private Context context;
        private ThemeStyle themeStyle;

        AboutAdapter(Context context, List<AboutEntity> data) {
            super(data);
            addItemType(AboutEntity.typeText, R.layout.item_about_text);
            addItemType(AboutEntity.typeSection, R.layout.item_about_section);
            addItemType(AboutEntity.typeLicense, R.layout.item_about_license);
            addItemType(AboutEntity.typeUser, R.layout.item_about_user);
            this.context = context;
            themeStyle = ThemeUtils.getInstance().getThemeStyle();
        }

        @Override
        protected void convert(BaseViewHolder helper, AboutEntity item) {
            switch (item.type) {
                case AboutEntity.typeSection:
                    convertSection(helper, item);
                    break;
                case AboutEntity.typeText:
                    convertContent(helper, item);
                    break;
                case AboutEntity.typeLicense:
                    convertLicense(helper, item);
                    break;
                case AboutEntity.typeUser:
                    convertUser(helper, item);
                    break;
            }
        }

        private void convertContent(BaseViewHolder helper, AboutEntity item) {
            helper.setText(R.id.content, item.text);
            ((TextView) helper.getView(R.id.content)).setMovementMethod(LinkMovementMethod.getInstance());
            if (themeStyle.isDarkTheme) {
                helper.getView(R.id.bg).setBackgroundResource(R.color.colorDarkPrimary);
            }
        }

        private void convertSection(BaseViewHolder helper, AboutEntity item) {
            helper.setText(R.id.category, item.sectionTitle);
        }

        private void convertLicense(BaseViewHolder helper, AboutEntity item) {
            helper.setText(R.id.content, item.license.name + " - " + item.license.author);
            helper.setText(R.id.hint, item.license.url + "\n" + item.license.type);
            if (themeStyle.isDarkTheme) {
                helper.getView(R.id.bg).setBackgroundResource(R.color.colorDarkPrimary);
            }
        }

        private void convertUser(BaseViewHolder helper, AboutEntity item) {
            Glide.with(context).load(item.user.avatarUrl).into((ImageView) helper.getView(R.id.avatar));
            helper.setText(R.id.name, item.user.name);
            helper.setText(R.id.desc, item.user.description);
            if (themeStyle.isDarkTheme) {
                helper.getView(R.id.bg).setBackgroundResource(R.color.colorDarkPrimary);
            }
        }
    }

    public static class AboutEntity implements MultiItemEntity {
        public final static int typeLicense = 0;
        public final static int typeUser = 1;
        public final static int typeSection = 2;
        public final static int typeText = 3;

        public final int type;

        public CharSequence sectionTitle;
        public CharSequence text;
        public License license;
        public User user;

        static AboutEntity getSectionTitle(CharSequence sectionTitle) {
            AboutEntity aboutEntity = new AboutEntity(typeSection);
            aboutEntity.sectionTitle = sectionTitle;
            return aboutEntity;
        }

        static AboutEntity getNormalText(CharSequence text) {
            AboutEntity aboutEntity = new AboutEntity(typeText);
            aboutEntity.text = text;
            return aboutEntity;
        }

        static AboutEntity getLicense(@NonNull String name,
                                      @NonNull String author,
                                      @NonNull String type,
                                      @NonNull String url) {
            License license = new License(name, author, type, url);
            AboutEntity aboutEntity = new AboutEntity(typeLicense);
            aboutEntity.license = license;
            return aboutEntity;
        }

        static AboutEntity getUser(@NonNull String name,
                                   @NonNull String avatarUrl,
                                   @NonNull String description,
                                   @NonNull String website) {
            AboutEntity aboutEntity = new AboutEntity(typeUser);
            aboutEntity.user = new User(name, avatarUrl, description, website);
            return aboutEntity;
        }

        private AboutEntity(int type) {
            this.type = type;
        }

        @Override
        public int getItemType() {
            return type;
        }

        public static class License {
            static final String MIT = "MIT License";
            static final String APACHE_2 = "Apache Software License 2.0";
            static final String GPL_V3 = "GNU general public license Version 3";
            static final String BSD_3 = "BSD 3-Clause License";

            public final String name;
            public final String author;
            public final String type;
            public final String url;

            License(@NonNull String name, @NonNull String author, @NonNull String type, @NonNull String url) {
                this.name = name;
                this.author = author;
                this.type = type;
                this.url = url;
            }
        }

        public static class User {
            public final String name;
            public final String avatarUrl;
            public final String description;
            public final String website;

            User(@NonNull String name, @NonNull String avatarUrl, @NonNull String description, @NonNull String website) {
                this.name = name;
                this.avatarUrl = avatarUrl;
                this.description = description;
                this.website =website;
            }
        }
    }
}
