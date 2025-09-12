package com.mess.qrunch.screen;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mess.qrunch.R;
import com.mess.qrunch.helper.ImageFetcher;

public class ImagePopupDialog extends DialogFragment {
    private static final String ARG_VENDOR_ID = "vendorId";
    private ImageFetcher imageFetcher;
    private Long vendorId;
    private ImageView popupImage; // keep reference

    public static ImagePopupDialog newInstance(Long vendorId) {
        ImagePopupDialog fragment = new ImagePopupDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_VENDOR_ID, vendorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (getArguments() != null) {
            vendorId = getArguments().getLong(ARG_VENDOR_ID);
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
        imageFetcher = new ImageFetcher();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_popup_dialog, container, false);
        TextView placeholder = view.findViewById(R.id.textViewPlaceholder);
        popupImage = view.findViewById(R.id.popupImageView);
        ImageView closeButton = view.findViewById(R.id.closeButton);

        // start async fetch
        imageFetcher.fetchImage(requireActivity(), vendorId, popupImage, placeholder);

        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageFetcher.cancel(); // cancel if fragment is destroyed
        popupImage = null; // avoid memory leaks
    }
}