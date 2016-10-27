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

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import es.babel.cdm.navigation.interfaces.NavigationFragment;

/**
 * NavigationManager wraps some common operations over Android's FragmentManager concerning the
 * addition, query and removal of Fragments.
 */
public class NavigationManager {

    /**
     * Flag for normal backstack operation.
     * <p/>
     * This means the backstack will not be cleared and the new fragment will be added atop of the
     * current one.
     */
    public static final int ADD_TO_BACKSTACK = 0;

    /**
     * Flags addFragment method it has to add the fragment to the backstack.
     */
    public static final int DO_NOT_ADD_TO_BACKSTACK = 1;

    /**
     * Flags addFragment method it has to clear the backstack.
     * <p/>
     * All the previous fragments will be discarded.
     */
    public static final int CLEAR_BACKSTACK = (1 << 1);

    /**
     * Flag indicating to replace or not the fragment.
     * <p/>
     * If the Fragment needs to be replaced, will NavigationManager will substitue the current
     * Fragment with the new one. Otherwise the new will appear on top of the current one and call
     * {@link NavigationFragment#onFragmentNotVisible() onFragmentNotVisible}
     * instead
     */
    public static final int DO_NOT_REPLACE_FRAGMENT = (1 << 2);

    /**
     * Injected Fragment Manager
     */
    protected FragmentManager fm;

    protected FragmentAnimation animation;

    /**
     * Creates a new instance of Navigation Manager
     */
    public NavigationManager() {
    }

    /**
     * Initializes the Navigation Manager by using a FragmentManager.
     *
     * @param fm FragmentManager to wrap around the NavigationManager.
     */
    public void initialize(FragmentManager fm) {
        this.fm = fm;
    }

    /**
     * Calculates the correct mode of adding a Fragment.
     * <p/>
     * If there are no available Fragments the NavigationManager will not try to call a previous
     * Fragment lifecycle.
     *
     * @return No flags if there are no fragments available or DO_NOT_REPLACE otherwise.
     */
    protected int calculateCorrectMode() {
        return fm.getBackStackEntryCount() == 0 ? 0 : DO_NOT_REPLACE_FRAGMENT;
    }

    /**
     * Adds a fragment to the activity content viewgroup. This will typically pass by a several
     * stages, in this order:
     * <ul>
     * <li>Considering if the necessity of {@link NavigationFragment#isSingleInstance()
     * adding another instance of such fragment class}</li>
     * <li>{@link #processClearBackstack(int) Processing clearing backstack flags conditions}</li>
     * <li>{@link #processAddToBackstackFlag(String, int,
     * android.support.v4.app.FragmentTransaction) Process adding to backstack flags
     * conditions}</li>
     * <li>{@link #processAnimations(FragmentAnimation, android.support.v4.app.FragmentTransaction)
     * Process the state of the deserved animations if any}</li>
     * <li>{@link #performTransaction(android.support.v4.app.Fragment, int,
     * android.support.v4.app.FragmentTransaction, int) Perform the actual transaction}</li>
     * </ul>
     * <p/>
     * If the fragment is not required to be readded (as in a up navigation) the fragment manager
     * will pop all the backstack until the desired fragment and the
     * {@link NavigationFragment#onFragmentVisible() onFragmentVisible()}
     * method will be called instead to bring up the dormant fragment.
     *
     * @param frag        Fragment to add
     * @param tag         Fragment tag
     * @param flags       Adds flags to manipulate the state of the backstack
     * @param containerId Container ID where to insert the fragment
     */
    public void addFragment(Fragment frag, String tag, FragmentAnimation animation, int flags,
                            int containerId) {

        if (frag != null) {
            if (((NavigationFragment) frag).isSingleInstance()) {
                if (fm.findFragmentByTag(tag) != null) { //El fragment está en la pila
                    if (flags != (NavigationManager.ADD_TO_BACKSTACK
                            & NavigationManager.CLEAR_BACKSTACK)) { //No viene de gotosection
                        Log.e("NAVIGATION FRAGMENT", "The fragment with tag --'" + tag
                                + "'-- is SingleInstance and it is already in the backstack");
                        return;
                    }
                }
            }
            FragmentTransaction ft = fm.beginTransaction();
            processClearBackstack(flags);
            processAddToBackstackFlag(tag, flags, ft);
            processAnimations(animation, ft);
            performTransaction(frag, flags, ft, containerId);
        }
    }
    /*public void addFragment(Fragment frag, String tag, FragmentAnimation animation,
    int flags, int containerId) {
        if (frag != null) {
            if (!((NavigationFragment) frag).isSingleInstance()
            || fm.getBackStackEntryCount() == 0) {
                FragmentTransaction ft = fm.beginTransaction();
                processClearBackstack(flags);
                processAddToBackstackFlag(tag, flags, ft);
                processAnimations(animation, ft);
                performTransaction(frag, flags, ft, containerId);
            } else {
                fm.popBackStack(((NavigationFragment) frag).getFragmentTag(), 0);
                peek(tag).onFragmentVisible();
            }
        }
    }*/

    /**
     * Returns the first fragment in the stack with the tag "tag".
     *
     * @param tag Tag to look for in the Fragment stack
     * @return First fragment in the stack with the name Tag
     */
    protected NavigationFragment peek(String tag) {
        return (NavigationFragment) fm.findFragmentByTag(tag);
    }

    /**
     * Process Clear backstack flag.
     * <p/>
     * NavigationManager will clear the back stack before trying to add the next Fragment if
     * {@link #CLEAR_BACKSTACK CLEAR_BACKSTACK} flag is found
     *
     * @param flags Added flags to the Fragment configuration
     */
    protected void processClearBackstack(int flags) {
        if ((flags & CLEAR_BACKSTACK) == CLEAR_BACKSTACK) {
            try {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Processes Add to Backstack flag.
     * <p/>
     * Will not add the Fragment to the backstack if the
     * {@link #DO_NOT_ADD_TO_BACKSTACK DO_NOT_ADD_TO_BACKSTACK} flag is found.
     *
     * @param title Title of the fragment
     * @param flags Added flags to the Fragment configuration
     * @param ft    Transaction to add to backstack from
     */
    protected void processAddToBackstackFlag(String title, int flags, FragmentTransaction ft) {
        if ((flags & DO_NOT_ADD_TO_BACKSTACK) != DO_NOT_ADD_TO_BACKSTACK) {
            ft.addToBackStack(title);
        }
    }

    /**
     * Processes the custom animations element, adding them as required
     *
     * @param animation Animation object to process
     * @param ft        Fragment transaction to add to the transition
     */
    protected void processAnimations(FragmentAnimation animation, FragmentTransaction ft) {
        if (animation != null) {
            if (animation.isCompletedAnimation()) {
                ft.setCustomAnimations(animation.getEnterAnim(), animation.getExitAnim(),
                        animation.getPushInAnim(), animation.getPopOutAnim());
            } else {
                ft.setCustomAnimations(animation.getEnterAnim(), animation.getExitAnim());
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                for (LollipopAnim sharedElement : animation.getSharedViews()) {
                    ft.addSharedElement(sharedElement.view, sharedElement.name);
                }
            }
        }
    }

    /**
     * Configures the way to add the Fragment into the transaction. It can vary from
     * adding a new fragment, to using a previous instance and refresh it, or replacing
     * the last one.
     *
     * @param frag        Fragment to add
     * @param flags       Added flags to the Fragment configuration
     * @param ft          Transaction to add the fragment
     * @param containerId Target container ID
     */
    protected void configureAdditionMode(Fragment frag, int flags, FragmentTransaction ft,
                                         int containerId) {
        if ((flags & DO_NOT_REPLACE_FRAGMENT) != DO_NOT_REPLACE_FRAGMENT) {
            ft.replace(containerId, frag, ((NavigationFragment) frag).getFragmentTag());
        } else {
            ft.add(containerId, frag, ((NavigationFragment) frag).getFragmentTag());
            peek().onFragmentNotVisible();
        }
    }

    /**
     * Commits the transaction to the Fragment Manager.
     *
     * @param frag        Fragment to add
     * @param flags       Added flags to the Fragment configuration
     * @param ft          Transaction to add the fragment
     * @param containerId Target containerID
     */
    protected void performTransaction(Fragment frag, int flags, FragmentTransaction ft,
                                      int containerId) {
        configureAdditionMode(frag, flags, ft, containerId);
        ft.commitAllowingStateLoss();
    }

    /**
     * Peeks the last fragment in the Fragment stack.
     *
     * @return Last Fragment in the fragment stack
     * @throws java.lang.NullPointerException if there is no Fragment Added
     */
    protected NavigationFragment peek() {
        if (fm.getBackStackEntryCount() > 0) {
            return ((NavigationFragment) fm.findFragmentByTag(
                    fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName()));
        } else {
            return null;
        }
    }

    protected NavigationFragment getLastFragmentOfStack() {
        if (fm.getFragments() != null) {
            for (int i = 0; i < fm.getFragments().size(); i++) {
                Fragment frag = fm.getFragments().get(i);
                if (frag instanceof NavigationFragment && frag.isVisible()) {
                    return (NavigationFragment) frag;
                }
            }
        }
        return null;
    }

    /**
     * Decides what to do with the backstack.
     * <p/>
     * The default behavior is as follows:
     * <p/>
     * NavigationManager will determine if the Fragment has a
     * {@link NavigationFragment#customizedOnBackPressed() customized action(s) for backpressing}
     * If so, the Navigation Manager will execute its {@link NavigationFragment#onBackPressed()
     * onBackPressed()} method.
     * <p/>
     * If the Fragment does not have any kind of custom action, then the NavigationManager will try
     * to determine if there is a {@link NavigationFragment#onBackPressedTarget()}.
     * <p/>
     * If positive, the NavigationManager will pop until it finds the Fragment.
     * <p/>
     * Otherwise will pop the inmediate Fragment and execute its
     * {@link NavigationFragment#onFragmentVisible()}
     *
     * @param containerId Target container ID
     */
    public void popBackStack(int containerId) {
        NavigationFragment currentFragment = (NavigationFragment) fm.findFragmentById(containerId);

        if (fm.getBackStackEntryCount() <= 0) {
            currentFragment.onBackPressed();
            return;
        }

        if (currentFragment != null) {
            if (!currentFragment.customizedOnBackPressed()) {
                FragmentManager.BackStackEntry backEntry =
                        fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
                NavigationFragment lastFragment = (NavigationFragment)
                        fm.findFragmentByTag(backEntry.getName());
                if (currentFragment.getFragmentTag().equals(lastFragment.getFragmentTag())) {
                    if (currentFragment.onBackPressedTarget() == null
                            || currentFragment.onBackPressedTarget().isEmpty()) {
                        fm.popBackStackImmediate();
                        if (fm.getBackStackEntryCount() >= 1 && peek() != null) {
                            peek().onFragmentVisible();
                        }
                    } else {
                        //Clean all until containerId
                        popBackStack(currentFragment.onBackPressedTarget(), 0);
                    }
                } else {
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.setCustomAnimations(animation.getPushInAnim(),
                            animation.getPopOutAnim());
                    fragmentTransaction.remove((Fragment) currentFragment)
                            .add((Fragment) lastFragment, lastFragment.getFragmentTag())
                            .commit();
                    lastFragment.onFragmentVisible();
                }
            } else {
                currentFragment.onBackPressed();
            }
        }
    }


    /**
     * Pops the Fragment with the tag, applying the necessary flags
     *
     * @param tag   Tag to look for in the Fragment stack
     * @param flags Flags to apply for the
     */
    public void popBackStack(String tag, int flags) {
        fm.popBackStack(tag, flags);
    }

    /**
     * Checks Backstack Entry Count.
     *
     * @return Backstack Entry Count.
     */
    public int getBackStackEntryCount() {
        return fm.getBackStackEntryCount();
    }

    /**
     * Returns if NavigationManager signals the Activity to finish.
     * Returns if NavigationManager signals the Activity to finish.
     * <p/>
     * This method is here instead of the
     * NavigationManager because I did not want to enforce any termination conditions itself.
     * However this is a good start to extend your own Activity Finishes.
     *
     * @return TRUE if the activity is finishable, FALSE otherwise
     */
    public boolean canActivityFinish() {
        return getBackStackEntryCount() <= 1 || peek() == null || peek().isEntryFragment();
    }

    public void setAnimation(FragmentAnimation animation) {
        this.animation = animation;
    }
}