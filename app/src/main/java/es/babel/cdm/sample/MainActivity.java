/*
 * Copyright (c) 2016. Babel sistemas de información.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.babel.cdm.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import es.babel.cdm.navigation.FragmentAnimation;
import es.babel.cdm.navigation.NavigationActivity;
import es.babel.cdm.navigation.interfaces.OnActionNavigation;

public class MainActivity extends NavigationActivity implements OnActionNavigation,
        BlankFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFragmentContainer();
        goDown(BlankFragment.newInstance(1), true);
    }

    private void initializeFragmentContainer() {
        this.config().setContainer(R.id.frame_container).setOnActionNavigation(this);
        this.config().setContainer(R.id.frame_container)
                .setOnActionNavigation(this)
                .setAnimation(new FragmentAnimation(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out));
    }

    @Override
    public void onNextClick(int newIndex) {
        goDown(BlankFragment.newInstance(newIndex), true);
    }

    @Override
    public void onBackPressedNavigation() {
        try {
            navigateUp();
        } catch (Exception e) {
            Log.e(TAG, "onBackPressedNavigation: ", e);
        }
    }

    @Override
    public void goDown(Fragment fragment, boolean stack) {
        try {
            navigateDown(fragment, stack);
        } catch (Exception e) {
            Log.e(TAG, "goDown: ", e);
        }
    }

    @Override
    public void goDownInverse(Fragment fragment, boolean stack) {
        try {
            navigateDownInverse(fragment, stack);
        } catch (Exception e) {
            Log.e(TAG, "goDownInverse: ", e);
        }
    }

    @Override
    public void goUp() {
        try {
            navigateUp();
        } catch (Exception e) {
            Log.e(TAG, "goUp: ", e);
        }
    }

    @Override
    public void goToSection(Fragment fragment) {
        try {
            navigateToSection(fragment);
        } catch (Exception e) {
            Log.e(TAG, "goToSection: ", e);
        }
    }

    @Override
    public void goToSectionInverse(Fragment fragment) {
        try {
            navigateToSectionInverse(fragment);
        } catch (Exception e) {
            Log.e(TAG, "goToSectionInverse: ", e);
        }
    }
}
