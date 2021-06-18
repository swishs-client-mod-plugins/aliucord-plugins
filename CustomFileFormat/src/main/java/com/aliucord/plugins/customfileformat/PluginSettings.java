/*
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 */

package com.aliucord.plugins.customfileformat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import com.aliucord.Constants;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Divider;
import com.aliucord.views.SaveButton;
import com.aliucord.views.TextInput;

import com.discord.utilities.color.ColorCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lytefast.flexinput.R$h;

@SuppressLint("SetTextI18n")
public final class PluginSettings extends SettingsPage {
    private static final String plugin = "CustomFileFormat";

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setActionBarTitle(plugin);
    }

    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);
        int padding = Utils.dpToPx(12);

        SettingsAPI sets = PluginManager.plugins.get(plugin).sets;
        Context context = view.getContext();
        LinearLayout layout = (LinearLayout) ((NestedScrollView) ((CoordinatorLayout) view).getChildAt(1)).getChildAt(0);
        layout.setPadding(padding, padding, padding, padding);

        SaveButton saveButtonLayout = new SaveButton(context);
        FloatingActionButton saveButton = (FloatingActionButton) saveButtonLayout.getChildAt(0);
        saveButton.hide();

        TextInput uploadFormat = new TextInput(context);
        uploadFormat.setHint("Upload Format");
        uploadFormat.setSuffixText(".EXT");
        uploadFormat.setSuffixTextColor(uploadFormat.getHintTextColor());
        uploadFormat.setBackgroundColor(ColorCompat.getThemedColor(view, 0));
        EditText uploadEditText = uploadFormat.getEditText();
        if (uploadEditText != null) uploadEditText.setMaxLines(1);
        uploadEditText.setText(sets.getString("uploadFormat", "{original}"));
        layout.addView(uploadFormat);

        uploadEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(sets.getString("uploadFormat", "{original}")))
                    saveButton.hide();
                else
                    saveButton.show();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        layout.addView(new Divider(context));
        TextInput downloadFormat = new TextInput(context);
        downloadFormat.setHint("Download Format");
        downloadFormat.setSuffixText(".EXT");
        downloadFormat.setSuffixTextColor(downloadFormat.getHintTextColor());
        downloadFormat.setBackgroundColor(ColorCompat.getThemedColor(view, 0));
        EditText downloadEditText = downloadFormat.getEditText();
        if (downloadEditText != null) downloadEditText.setMaxLines(1);
        downloadEditText.setText(sets.getString("downloadFormat", "{original}"));
        layout.addView(downloadFormat);

        downloadEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(sets.getString("downloadFormat", "{original}")))
                    saveButton.hide();
                else
                    saveButton.show();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        layout.addView(new Divider(context));
        TextView itemSubtext = new TextView(context, null, 0, R$h.UiKit_Settings_Item_SubText);
        itemSubtext.setTextSize(13.0f);
        itemSubtext.setPadding(padding, padding, padding, padding);
        itemSubtext.setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.whitney_medium));
        itemSubtext.setText("Variables: {original} {timestamp} {random}");
        layout.addView(itemSubtext);

        saveButton.setOnClickListener((View _view) -> {
            String downloadText = downloadEditText.getText().toString();
            if (downloadText.isEmpty()) sets.setString("downloadFormat", "{original}");
            else sets.setString("downloadFormat", downloadText);

            String uploadText = uploadEditText.getText().toString();
            if (uploadText.isEmpty()) sets.setString("uploadFormat", "{original}");
            else sets.setString("uploadFormat", uploadText);

            saveButton.hide();
            reloadPlugin();
        });
        layout.addView(saveButtonLayout);
    }

    public void reloadPlugin() {
        PluginManager.stopPlugin(plugin);
        PluginManager.startPlugin(plugin);
    }
}