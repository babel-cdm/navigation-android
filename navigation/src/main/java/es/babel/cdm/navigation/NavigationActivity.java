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

package es.babel.cdm.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import es.babel.cdm.navigation.interfaces.Exceptions;
import es.babel.cdm.navigation.interfaces.NavigationController;
import es.babel.cdm.navigation.interfaces.NavigationFragment;
import es.babel.cdm.navigation.interfaces.OnActionNavigation;

public class NavigationActivity extends AppCompatActivity implements NavigationController, Exceptions {

    protected Integer sContainer = null;
    protected NavigationManager sNavigationManager;
    protected OnActionNavigation sOnActionNavigation;

    protected FragmentAnimation sAnimation = new FragmentAnimation(
            R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sNavigationManager = new NavigationManager();
        this.sNavigationManager.initialize(getSupportFragmentManager());
        sNavigationManager.setAnimation(sAnimation);
    }

    public NavigationActivity setContainer(int id) {
        this.sContainer = id;
        return this;
    }

    public NavigationActivity config() {
        return this;
    }

    public NavigationActivity setAnimation(FragmentAnimation anim) {
        this.sAnimation = anim;
        sNavigationManager.setAnimation(sAnimation);
        return this;
    }

    public NavigationActivity setOnActionNavigation(OnActionNavigation listener) {
        this.sOnActionNavigation = listener;
        return this;
    }

    /*@Override
    public void navigateToSection(Fragment fragment, boolean addToBackStack) throws Exception {

        int flags = (addToBackStack ? NavigationManager.ADD_TO_BACKSTACK : NavigationManager.DO_NOT_ADD_TO_BACKSTACK) &
                NavigationManager.CLEAR_BACKSTACK;

        setFragment(fragment, flags);
    }*/

    @Override
    public void navigateToSection(Fragment fragment) throws Exception {
        int flags = NavigationManager.ADD_TO_BACKSTACK & NavigationManager.CLEAR_BACKSTACK;

        setFragment(fragment, flags);
    }

    @Override
    public void navigateToSectionInverse(Fragment fragment) throws Exception {
        //Save previous animation
        FragmentAnimation previousAnimation = this.sAnimation;

        int flags = NavigationManager.ADD_TO_BACKSTACK & NavigationManager.CLEAR_BACKSTACK;

        //Change animation
        setGoBackAnimation();

        setFragment(fragment, flags);

        //Set new animation
        this.config().setAnimation(previousAnimation);
    }

    @Override
    public void navigateDown(Fragment fragment, boolean addToBackStack) throws Exception {
        int flags = (addToBackStack ? NavigationManager.ADD_TO_BACKSTACK : NavigationManager.DO_NOT_ADD_TO_BACKSTACK);

        setFragment(fragment, flags);
    }

    @Override
    public void navigateDownInverse(Fragment fragment, boolean addToBackStack) throws Exception {
        int flags = (addToBackStack ? NavigationManager.ADD_TO_BACKSTACK : NavigationManager.DO_NOT_ADD_TO_BACKSTACK);

        //Save previous animation
        FragmentAnimation previousAnimation = this.sAnimation;

        //Change animation
        setGoBackAnimation();

        setFragment(fragment, flags);

        //Set new animation
        this.config().setAnimation(previousAnimation);
    }

    private void setGoBackAnimation() {
        this.config().setAnimation(new FragmentAnimation(
                R.anim.continuous_slide_in_right,
                R.anim.continuous_slide_out_right,
                R.anim.continuous_slide_in_right,
                R.anim.continuous_slide_out_right));
    }

    @Override
    public void navigateUp() throws Exception {
        if (sContainer == null) {
            throw new Exception(CONTAINER_EXCEPTION);
        }

        sNavigationManager.popBackStack(sContainer);
    }

    @Override
    public void navigateUp(int levels) throws Exception {
        if (sContainer == null) {
            throw new Exception(CONTAINER_EXCEPTION);
        }

        if (levels > sNavigationManager.getBackStackEntryCount()) {
            throw new Exception(SIZE_STACK_EXCEPTION);
        }

        for (int i = 0; i < levels; i++) {
            sNavigationManager.popBackStack(sContainer);
        }
    }

    private void setFragment(Fragment fragment, int flags) throws Exception {
        if (sContainer == null) {
            throw new Exception(CONTAINER_EXCEPTION);
        }

        sNavigationManager.addFragment(fragment, ((NavigationFragment) fragment).getFragmentTag(),
                sAnimation, flags, sContainer);
    }

    public boolean canActivityFinish() {
        return sNavigationManager.canActivityFinish();
    }

    @Override
    public void onBackPressed() {
        if (sOnActionNavigation != null) {
            sOnActionNavigation.onBackPressedNavigation();
        }
    }

    public int getCountBackNavigation() {
        return sNavigationManager.getBackStackEntryCount();
    }

    public NavigationFragment getCurrentFragment() {
        return sNavigationManager.getLastFragmentOfStack();
    }
}
