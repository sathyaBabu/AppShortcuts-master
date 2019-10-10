package com.kostovtd.appshortcuts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends Activity {
// https://spotandroid.com/2017/03/12/app-shortcuts-in-details/

    private ShortcutManager shortcutManager;

    private Button bCallJack;
    private Button bCallJill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // INIT SHORTCUT MANAGER
        shortcutManager = getSystemService(ShortcutManager.class);
        boolean noDynamicShortcuts = shortcutManager.getDynamicShortcuts().size() == 0;
        if(noDynamicShortcuts) {
            createDynamicShortcut();
        }


        bCallJack = (Button) findViewById(R.id.button_call_jack);
        bCallJill = (Button) findViewById(R.id.button_call_jill);


        // CLICK JACK
        bCallJack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Jack called", Toast.LENGTH_LONG).show();
                shortcutManager.reportShortcutUsed("jack");
                trackShortcutUsage("jack");
            }
        });


        // CLICK JILL
        bCallJill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Jill called", Toast.LENGTH_LONG).show();
                shortcutManager.reportShortcutUsed("jill");
                trackShortcutUsage("jill");
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        createDynamicShortcut();
    }


    /**
     * Create a dynamic App Shortcut with
     * the most used feature
     */
    private void createDynamicShortcut() {
        ShortcutInfo dynamicShortcut;

        int jackUsage = getShortcutUsage("jack");
        int jillUsage = getShortcutUsage("jill");

        // compare the usage of the two dynamic shortcuts
        if(jackUsage > jillUsage) {
            dynamicShortcut = createJackShortcut();
        } else if(jillUsage > jackUsage) {
            dynamicShortcut = createJillShortcut();
        } else {
            return;
        }

        shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut));
    }


    /**
     * Create a dummy {@link ShortcutInfo} object
     * @return A dummy {@link ShortcutInfo} object
     */
    private ShortcutInfo createJackShortcut() {
        // dial this number when the shortcut was selected
        Uri jackPhoneUri = Uri.parse("tel:1111111");

        ShortcutInfo jackShortcut = new ShortcutInfo.Builder(this, "jack")
                .setShortLabel(getString(R.string.main_screen_shortcut_call_jack_short))
                .setLongLabel(getString(R.string.main_screen_shortcut_call_jack_long))
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(new Intent(Intent.ACTION_DIAL, jackPhoneUri))
                .build();

        return jackShortcut;
    }


    /**
     * Create a dummy {@link ShortcutInfo} object
     * @return A dummy {@link ShortcutInfo} object
     */
    private ShortcutInfo createJillShortcut() {
        // dial this number when the shortcut was selected
        Uri jillPhoneUri = Uri.parse("tel:2222222");

        ShortcutInfo jillShortcut = new ShortcutInfo.Builder(this, "jill")
                .setShortLabel(getString(R.string.main_screen_shortcut_call_jill_short))
                .setLongLabel(getString(R.string.main_screen_shortcut_call_jill_long))
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(new Intent(Intent.ACTION_DIAL, jillPhoneUri))
                .build();

        return jillShortcut;
    }


    /**
     * Increase the current usage of a given {@link ShortcutInfo} by 1
     * and save it in {@link SharedPreferences}
     * @param shortcutId An ID of a {@link ShortcutInfo}
     */
    private void trackShortcutUsage(String shortcutId) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_shortcuts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int currentUsage = sharedPreferences.getInt(shortcutId, 0);

        // increase the current usage by 1
        editor.putInt(shortcutId, ++currentUsage);
        editor.apply();
    }


    /**
     * Get the current usage for a given {@link ShortcutInfo}
     * @param shortcutId An ID of a {@link ShortcutInfo}
     * @return The current usage, or 0 if something went wrong
     */
    private int getShortcutUsage(String shortcutId) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_shortcuts", MODE_PRIVATE);

        return sharedPreferences.getInt(shortcutId, 0);
    }
}
