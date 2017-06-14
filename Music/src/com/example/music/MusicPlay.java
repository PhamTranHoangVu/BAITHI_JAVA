package com.example.music;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
public class MusicPlay extends Service implements android.media.MediaPlayer.OnCompletionListener   {
	MediaPlayer mediaplayer;
	static final int PLAYER_IDLE =-1, PLAYER_PLAY=1,PLAYER_PAUSE= 2; 
	static boolean isEnd;
	public int state;
	private OnCompletionListener onCompletionListener;
	public MusicPlay ()
	{
		
	}
	
	public int getState()
	{
		return state;
	}
	public void setup (String path)
	{
		try
		{
			state =PLAYER_IDLE;
			mediaplayer =new MediaPlayer();
			mediaplayer.setDataSource(path); // nguồn dữ liệu
			mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			isEnd=true;
			//
			mediaplayer.prepare(); // trạng thái chuẩn bị
			// phản ứng khi kết thúc file
			mediaplayer.setOnCompletionListener(this);

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		//khi ket thuc bai hat no se vào hàm này, ta viết 1 interface để cho activity
		//biết khi nào kết thúc bài hát để chuyển bài
		//gọi interface
		
	         onCompletionListener.OnEndMusic();    
	}
	public void getOnCompletionListener(OnCompletionListener onCompletionListener) {
		 this.onCompletionListener =onCompletionListener;
	}
	public interface OnCompletionListener{
        void OnEndMusic();
    }
	public int getTimeTotal()
	{
		return mediaplayer.getDuration()/1000; 
		//getDuration() Phương pháp này trả về tổng thời gian của bài hát trong mili giây
	}
	public void play()
	{
		if(state == PLAYER_IDLE || state ==PLAYER_PAUSE )
		{

			state =PLAYER_PLAY;
			mediaplayer.start();
		}
	}
	public void pause()
	{
		if(state == PLAYER_PLAY)
		{
			mediaplayer.pause();
			state =PLAYER_PAUSE;
		}
	}
	public void repeat()
	{
		mediaplayer.stop();
		state = PLAYER_IDLE ;
	}
	public void stop()
	{
		if(state ==PLAYER_PLAY || state ==PLAYER_PAUSE)
		{
			state =PLAYER_IDLE;
			mediaplayer.stop();
			// giải phóng tài nguyên
			mediaplayer.release();
			mediaplayer=null;
			
		}
	}
	public int getTimeCurrent() // thời gian hiện tại
	{
		if(state !=PLAYER_IDLE)
		{
			return mediaplayer.getCurrentPosition()/1000; 
			//getCurrentDuration() Phương pháp này trả về vị trí hiện tại của bài hát trong mili giây
		}
		else
		return 0;
	}
	public void seek(int time)
	{
		mediaplayer.seekTo(time);
		// seekTo (position) Phương pháp này có một số nguyên, và di chuyển bài hát đến giây phút đặc biệt
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
