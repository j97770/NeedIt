package com.miru.needit;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.loopj.android.image.SmartImageView;

public class ImageAdapter extends ArrayAdapter<ImageResult> {

	public ImageAdapter(Context context, ArrayList<ImageResult> imageResults) {
		super(context, R.layout.item_image_result, imageResults);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageResult imageInfo = getItem(position);
		SmartImageView ivImage;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			ivImage = (SmartImageView) inflater.inflate(
					R.layout.item_image_result, parent, false);
		} else {
			ivImage = (SmartImageView) convertView;
			ivImage.setImageResource(android.R.color.transparent);
		}

		ivImage.setImageUrl(imageInfo.getThumbUrl());
		return ivImage;
	}

}
