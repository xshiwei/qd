package com.qvd.smartswitch.activity.login;

import android.os.Bundle;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.yanzhenjie.permission.Permission;

public class WelcomeActivity extends com.stephentuso.welcome.WelcomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.requestPermission(this, Permission.Group.LOCATION);
        PermissionUtils.requestPermission(this, Permission.Group.STORAGE);
    }

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")

                .page(new BasicPage(R.drawable.ic_front_desk_white,
                        "Welcome",
                        "An Android library for onboarding, instructional screens, and more")
                        .background(R.color.orange_background)
                )

                .page(new BasicPage(R.drawable.ic_thumb_up_white,
                        "Simple to use",
                        "Add a welcome screen to your app with only a few lines of code.")
                        .background(R.color.red_background)
                )

                .page(new ParallaxPage(R.layout.activity_welcome,
                        "Easy parallax",
                        "Supply a layout and parallax effects will automatically be applied")
                        .lastParallaxFactor(2f)
                        .background(R.color.purple_background)
                )

                .page(new BasicPage(R.drawable.ic_edit_white,
                        "Customizable",
                        "All elements of the welcome screen can be customized easily.")
                        .background(R.color.blue_background)
                )
                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }
}
