package com.kenshi.Core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.kenshi.NetworkManager.Target;
import com.mitdroid.kenshi.Main.R;

public class Plugin extends Activity {
    public static final int NO_LAYOUT = -1;

    private String name = null;
    private String description = null;
    private Target.Type[]allowedTargetType = null;
    private int layoutId = 0;
    private int iconId = 0;

    public Plugin(String name, String description, Target.Type[]allowedTargetType,
                  int layoutId, int iconId) {
        this.name = name;
        this.description = description;
        this.allowedTargetType = allowedTargetType;
        this.layoutId = layoutId;
        this.iconId = iconId;
    }

    public Plugin(String name, String description, Target.Type[]allowedTargetType, int layoutId) {
        this(name, description, allowedTargetType, layoutId, R.drawable.action_plugin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(System.getCurrentTarget() + " > " + name);
        setContentView(layoutId);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public String getDescription() { return description; }

    public String getName() { return name; }

    public Target.Type[] getAllowedTargetType() { return allowedTargetType; }

    public int getIconId() { return iconId; }

    public boolean isAllowedTarget(Target target) {
        for(Target.Type type : allowedTargetType)
            if(type == target.getType())
                return true;
        return false;
    }

    public boolean hasLayoutToShow() { return layoutId != -1; }

    public void onActionClick(Context context) {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onTargetNewOpenPort(Target target, Target.Port port) {}

    public void onTargetNewVulnerbility(Target target, Target.Port port,
                                        Target.Vulnerability vulnerability) {}
}
