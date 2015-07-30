package net.game;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.hardware.SensorEventListener;
import android.view.SurfaceHolder;


@SuppressLint("WrongCall")
public class GameThread extends Thread {
	private static final String TAG = GameThread.class.getSimpleName();

	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private GameScreen gamePanel;


	// flag to hold game state 
	private boolean running=true;
	Canvas canvas;
	public GameThread(SurfaceHolder surfaceHolder, GameScreen gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}


	public boolean stopThread(){
		running = false;
		return true;

	}
	@Override
	public void run() throws NullPointerException{
		
		// TODO Auto-generated method stub
		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					// update game state 
					// render state to the screen
					// draws the canvas on the panel

//					try {
						this.gamePanel.onDraw(canvas);
//					}catch (Exception e){
//						e.printStackTrace();
//					}


					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					GameScreen.update();
				}
			}catch (NullPointerException e){

			}

			finally {
				// in case of an exception the surface is not left in 
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}	// end finally
		}
	}

}
