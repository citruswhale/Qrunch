package com.example.test.screen;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.helper.ImageFetcher;

public class ImagePopupDialog extends DialogFragment {
    private static final String ARG_IMAGE_URL = "image_url";
    private String imageUrl;

    public static ImagePopupDialog newInstance(String imageUrl) {
        ImagePopupDialog dialog = new ImagePopupDialog();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_popup_dialog, container, false);

        ImageView imageView = view.findViewById(R.id.popupImageView);
        TextView placeholder = view.findViewById(R.id.textViewPlaceholder);
        ImageButton closeButton = view.findViewById(R.id.closeButton);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            placeholder.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUrl).into(imageView);
        }

        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }
}

