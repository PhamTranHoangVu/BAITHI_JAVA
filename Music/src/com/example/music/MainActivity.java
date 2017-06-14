package com.example.music;

import java.io.File;

import java.util.ArrayList;

import com.example.music.MusicPlay.OnCompletionListener;

import java.util.Random;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.SeekBar.OnSeekBarChangeListener;
@SuppressLint("NewApi") public class MainActivity extends Activity {
	ListView lvMusic;
	TextView tvTitle, tvArtist, tvTimeProcess,tvTimeTotal;
	SeekBar spProcess;
	ImageView ivShuffle, ivPrevious, ivPlay,ivNext,ivRepeat;
	ArrayList<String> paths ; // lấy tất cả đường dẫn của bài hát
	int timeProces;
	int timeTotal;
	PlayListAdapter adapter;
	MusicPlay musicplay;
	Boolean isRunning;
	private int UPDATE_TIME=1;
	int timeCurrent;
	private int position;
	int KT=0;
	int result=0;
	int KT_Next =0;
	int KT_Next_Repeat =0;
	int result_Repeat=-1;
	int length;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ánh xạ
        initView();
        //xét sự kiện cho các button
        initListeners();
        // thêm nội dung cho chương trình
        initComponets();
    }

	private void initComponets() {
		// TODO Auto-generated method stub
		//MediaStore.Audio.Media : đọc tất cả
		initList();
		adapter = new PlayListAdapter(this,R.layout.item_list, paths); // lấy đường dẫn gán vào adapter
		lvMusic.setAdapter(adapter);
		musicplay =new MusicPlay();
		musicplay.getOnCompletionListener(new OnCompletionListener() {
			@Override
			public void OnEndMusic() {
				// TODO Auto-generated method stub
				if(result==1)
				{
					KT=1;
					Random();	
				}
				if(result==0)
				{
					NextMusic();
					KT_Next =1;
					ivShuffle.setBackgroundResource(R.drawable.shuffle);
				}
				//
				if(result_Repeat==1)
				{
					String path = paths.get(position);
					MusicPlay(path);
					KT_Next_Repeat=1;
				}
			}
		});

	}
	void Random()
	{	
		Random rd = new Random();
		int num;
		num = rd.nextInt(length);
		if(num != length+1)
		{
			position =num;
			String path = paths.get(position);
			MusicPlay(path);
		}	
	}
	void NextMusic()
	{		
		position++;
        if (position >= paths.size()) 
        {
            position = 0;
        }
        String path = paths.get(position);
        MusicPlay(path);
	}
	void PreviousMusic()
	{
		position --;
		if(position <0)
		{
			position =paths.size() -1;
		}
		String path = paths.get(position);
		MusicPlay(path);
	}
	private void initList() {
		// TODO Auto-generated method stub
		paths =new ArrayList<String>(); // lưu tất cả đường dẫn vào arraylist
		String path =Environment.getExternalStorageDirectory().getAbsolutePath() +"/Download"; // lấy đường đẫn
		File file =new File(path);
		File[] files =file.listFiles(); //lấy tất cả các file trong thư mục
		length =files.length;
		for (int i=0; i< files.length; i++)
		{
			// đọc tất cả các file có trong thư mục thêm vào list nhạc
			String s =files[i].getName();
			if(s.endsWith(".mp3") || s.endsWith(".wav") || s.endsWith(".flac")) // lấy đuôi mp3
			{
				paths.add(files[i].getAbsolutePath());
			}
		}
	
	}

	///////
	@SuppressLint("HandlerLeak") private  Handler handler =new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what==UPDATE_TIME)
			{
				tvTimeProcess.setText(getTimeFormat(musicplay.getTimeCurrent()));
				spProcess.setProgress(musicplay.getTimeCurrent()); //tiến độ hiện tại của thanh SeekBar -- Progress
			}
		}
	};
	/////////

	 private void initListeners() {
		// TODO Auto-generated method stub
		// musicplay =new MusicPlay();// hàm tạo mới
		lvMusic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position =arg2; // vị trí;
				String path =paths.get(arg2);  // lấy vị trí đường dẫn
				// hàm play nhạc
				 MusicPlay(path);
			}
			
		});
		ivShuffle.setOnClickListener(new Click());
		ivPrevious.setOnClickListener(new Click());
		ivPlay.setOnClickListener(new Click());
		ivNext.setOnClickListener(new Click());
		ivRepeat.setOnClickListener(new Click());
		spProcess.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			// tua trên thanh SeekBar
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				// arg1 = progess: tiến độ của thanh SeekBar 
				if(musicplay.getTimeCurrent()!= arg1 && musicplay.getTimeCurrent() !=0)
				{
					
					musicplay.seek(spProcess.getProgress()*1000);
				}
			}
		});
	}
	 @SuppressLint({ "InlinedApi", "NewApi" })private void MusicPlay (String path)
	{
		if(musicplay.getState() == com.example.music.MusicPlay.PLAYER_PLAY) // kiểm tra nếu nhạc đã có khi click sẽ stop
		{
			musicplay.stop();
		}
		musicplay.setup(path); // thiết lập (khởi tạo)
		musicplay.play();
		ivPlay.setImageResource(R.drawable.pause);
		//
		// tên bài hát + ca sĩ
		MediaMetadataRetriever mmr =new MediaMetadataRetriever();
		mmr.setDataSource(paths.get(position));
		String artist =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		String title =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		tvArtist.setText(artist);
		tvTitle.setText(title);
		isRunning = true;
		if(artist ==null && title ==null)
		{
			tvArtist.setText("Unknown artist");
			tvTitle.setText("Unknown song");
		}
		if(artist ==null)
		{
			tvArtist.setText("Unknown artist");
			if(title ==null)
			{
				tvTitle.setText("Unknown song");
			}
			else
			{
				tvTitle.setText(title);
			}
		}
		if(title ==null)
		{
			tvTitle.setText("Unknown song");
			if(	artist ==null)
			{
				tvArtist.setText("Unknown artist");
			}
			else
			{
				tvArtist.setText(artist);
			}
			
		}

	// total time
		tvTimeTotal.setText(getTimeFormat(musicplay.getTimeTotal()));
	// process time
		spProcess.setMax(musicplay.getTimeTotal());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(isRunning)
				{
					Message message = new Message();
					message.what = UPDATE_TIME;
					handler.sendMessage(message);
						try {
							//tạm dừng 100 miliseconds
							Thread.sleep(100);
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
					
				}
				
			}
		}).start();
	}
	private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }
	private void initView()
	{
		lvMusic=(ListView)findViewById(R.id.lv_listMusic);
		tvTitle =(TextView)findViewById(R.id.tv_song);
		tvArtist=(TextView)findViewById(R.id.tv_artist);
		tvTimeProcess =(TextView)findViewById(R.id.tv_process);
		tvTimeTotal =(TextView)findViewById(R.id.tv_total);
		spProcess =(SeekBar)findViewById(R.id.sb_process);
		ivShuffle =(ImageView)findViewById(R.id.iv_shuffle);
		ivPrevious =(ImageView)findViewById(R.id.iv_previous);
		ivPlay=(ImageView)findViewById(R.id.iv_play);
		ivNext =(ImageView)findViewById(R.id.iv_next);
		ivRepeat =(ImageView)findViewById(R.id.iv_repeat);
	}
	private class Click implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
			case R.id.iv_shuffle:
				result=1;
				result_Repeat=-1;
				ivShuffle.setBackgroundResource(R.drawable.shuffle1);
				if(KT==1 && musicplay.getState() ==com.example.music.MusicPlay.PLAYER_PLAY)
				{
						//musicplay.repeat();
						//if(musicplay.getState() == com.example.music.MusicPlay.PLAYER_IDLE)
						//{
						//	String path = paths.get(position);
						//	MusicPlay(path);
						//	ivShuffle.setBackgroundResource(R.drawable.shuffle);
						//}
					ivShuffle.setBackgroundResource(R.drawable.shuffle);
					result=0;
					KT=0;
					
				}
				else if(KT_Next==1 && musicplay.getState() == com.example.music.MusicPlay.PLAYER_PLAY )
				{
					result=1;
					KT=0;
					ivShuffle.setBackgroundResource(R.drawable.shuffle1);
				}
				break;
			case R.id.iv_next:
				NextMusic();
				break;
			case R.id.iv_play:
				if(musicplay.getState() ==com.example.music.MusicPlay.PLAYER_PLAY)
				{
					ivPlay.setImageResource(R.drawable.playbut);
					musicplay.pause();
				}
				else
				{
					ivPlay.setImageResource(R.drawable.pause);
					musicplay.play();
				}
				break;
			case R.id.iv_previous:
				PreviousMusic();
				break;
			case R.id.iv_repeat:
				if(musicplay.getState() == com.example.music.MusicPlay.PLAYER_PLAY)
				{;
					ivRepeat.setBackgroundResource(R.drawable.repeat1);
					result=2;
					KT=0;
					KT_Next=0;
					result_Repeat =1;
				}
				if(KT_Next_Repeat == 1 && musicplay.getState() == com.example.music.MusicPlay.PLAYER_PLAY )
				{
					ivRepeat.setBackgroundResource(R.drawable.repeat);
					result_Repeat =0;
					KT_Next_Repeat =0;
					result=0;
				}
				break;
			default:
				break;
			}
		}
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
