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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import es.babel.cdm.navigation.interfaces.NavigationFragment;


public class BlankFragment extends Fragment implements NavigationFragment {
    private static final String ARG_INDEX = "index_arg";

    private TextView indexTextView;
    private Button nextButton;

    private int mIndex;

    private OnFragmentInteractionListener mListener;

    public BlankFragment() {
    }

    public static BlankFragment newInstance(int index) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt(ARG_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        indexTextView = (TextView) view.findViewById(R.id.tv_index);
        nextButton = (Button) view.findViewById(R.id.btn_next);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        indexTextView.setText("Index: " + mIndex);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNextClick(mIndex + 1);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean customizedOnBackPressed() {
        return false;
    }

    @Override
    public String getFragmentTag() {
        return "BlankFragment_" + mIndex;
    }

    @Override
    public boolean isEntryFragment() {
        return false;
    }

    @Override
    public boolean isSingleInstance() {
        return false;
    }

    @Override
    public void onFragmentVisible() {

    }

    @Override
    public void onFragmentNotVisible() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public String onBackPressedTarget() {
        return null;
    }

    public interface OnFragmentInteractionListener {
        void onNextClick(int newIndex);
    }
}
