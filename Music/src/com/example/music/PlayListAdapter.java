package com.example.music;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlayListAdapter extends ArrayAdapter<String>
{
	private ArrayList<String> paths=null;
	Activity context =null;
	LayoutInflater inflater;
	MusicPlay musicplay;
	MediaPlayer mediaplayer;
	int layoutID;
	public PlayListAdapter(Activity context, int layoutID,ArrayList<String> paths)
	{
		super(context, layoutID,paths);
		this.paths =paths;
		this.context=context;
		this.layoutID=layoutID;
		
	}
	
	@SuppressLint("NewApi") @Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(arg1 ==null)
		{	
			inflater=context.getLayoutInflater();
			arg1=inflater.inflate(layoutID, null);
			// set up the ViewHolder
			holder = new ViewHolder();
			holder.tvTitle =(TextView) arg1.findViewById(R.id.tv_title);
			holder.tvArtist=(TextView) arg1.findViewById(R.id.tv_artist);
			arg1.setTag(holder); // lưu trữ View

		}
		else
		{
			holder =(ViewHolder) arg1.getTag();
			//Lấy view
		}
		MediaMetadataRetriever mmr =new MediaMetadataRetriever();
		mmr.setDataSource(paths.get(arg0));
		String artist =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		String title =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		holder.tvArtist.setText(artist);
		holder.tvTitle.setText(title);
		if(artist ==null && title ==null)
		{
			holder.tvArtist.setText("Unknown artist");
			holder.tvTitle.setText("Unknown song");
		}
		if(artist ==null)
		{
			holder.tvArtist.setText("Unknown artist");
			holder.tvTitle.setText(title);
			if(title ==null)
			{
				holder.tvTitle.setText("Unknown song");
			}
		}
		if(title ==null)
		{
			holder.tvArtist.setText(artist);
			holder.tvTitle.setText("Unknown song");
			if(artist ==null)
			{
				holder.tvArtist.setText("Unknow artist");
			}
		}

		return arg1;
	}
	// ViewHolder cho phép truy cập các thành phần của danh sách.
	//Đặc biệt,  giúp  tránh việc phải thực hiện findViewById() 
	//và làm cho ứng dụng mượt hơn.
	private class ViewHolder { 
			TextView tvTitle ;
			TextView tvArtist;
	}
}
