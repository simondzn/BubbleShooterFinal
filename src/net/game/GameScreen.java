package net.game;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Delayed;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import au.com.bytecode.opencsv.CSVWriter;
import net.game.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;

import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameScreen extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = GameScreen.class.getSimpleName();
	// static Point originalPoint;
	static private GameThread thread;
	static private Bubble[][] bubbles;
	static private Bitmap[] bubbles_normal = new Bitmap[8];
	static private Bitmap backgroundBMP;
	// variables for destruction
	static Queue<Point> bubblePositionsToDestroy;
	static int colorIndexDest = 0;

	static int width;
	static int height;
	static int bubbleHight;
	static int bubblewidth;
	final static int bubbleSize = 30;
	static int numOfBubble = 0;
	static Bubble[] waitingBubbles;
	static final int numOfWaitingBubbles = 4;
	static Bubble movingBubble;
	static float slope;
	static float deltaX = 0;
	static boolean moving = false;
	static final int noOfSteps = 10;
	static boolean changingWaiting = false;
	static int noOfShiftedRows = 0;
	static int t1 = 0;
	static private SoundPool sounds;
	static private int collide;
	static private int destroyGroup;
	static private int loseSound;
	static private int winSound;
	static private int rebound;

	static private int initialYDis = 60;
	static private int initialXdis = 50;
	static int noOfSameColor = 0;
	static Random random = new Random();
	static Paint paint;
	static int score;
	static boolean lose = false;

	static Bitmap losePanel;
	static Bitmap winPanel;
	static Bitmap compressor;
	static Bitmap congrats;
	static int levelN = 1;
	static int compressorPeriod;
	static int[] colorsPresented;
//	My deceleration
long timestamp, timestampUp;
	float x, y;
	private ArrayList touchData;
	int count=0, touchLim = 250;
	public String Id = BubbleShooterActivity.Id;

	private void initialize() {
		// num of bubbles should be initialized according to level no

		// generating pics of bubbles
		touchData = new ArrayList();
		bubbles = new Bubble[20][8];
		waitingBubbles = new Bubble[4];
		bubblePositionsToDestroy = new LinkedList<Point>();
		bubbleHight = bubbles.length;
		bubblewidth = bubbles[0].length;
		colorsPresented = new int[8];
		

		BitmapFactory.Options options = new BitmapFactory.Options();

		bubbles_normal[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_1, options);
		bubbles_normal[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_2, options);
		bubbles_normal[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_3, options);
		bubbles_normal[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_4, options);
		bubbles_normal[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_5, options);
		bubbles_normal[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_6, options);
		bubbles_normal[6] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_7, options);
		bubbles_normal[7] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bubble_8, options);

		backgroundBMP = BitmapFactory.decodeResource(getResources(),
				R.drawable.background, options);
		losePanel = BitmapFactory.decodeResource(getResources(),
				R.drawable.lose_panel, options);
		winPanel = BitmapFactory.decodeResource(getResources(),
				R.drawable.win_panel, options);
		congrats = BitmapFactory.decodeResource(getResources(),
				R.drawable.congratulations013, options);
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setTextSize(20);

		movingBubble = new Bubble();
		compressor = BitmapFactory.decodeResource(getResources(),
				R.drawable.compressor, options);

	}

	public static void reInit() {

		noOfShiftedRows = 0;
		score = 0;
		numOfBubble = 0;
		noOfSameColor = 3 + levelN;
		compressorPeriod = 1000 - levelN * 100;
		int numOfBubblesGen = 0;

		int index = 0;
		int[] currMap = Map.Maps[levelN - 1];

		for (int i = 0; i < bubbleHight; i++) {

			for (int j = 0; j < bubblewidth; j++) {

				if (numOfBubblesGen < currMap.length) {
					index = currMap[numOfBubblesGen];
					if (index != -1) {
						colorsPresented[index]++;
						if (i % 2 == 0) {
							// just creating the bubble on the start
							if (bubbles[i][j] == null) {
								bubbles[i][j] = new Bubble(
										bubbles_normal[index], initialXdis
												+ (int) j * 30, initialYDis + i
												* 30);
							} else {
								bubbles[i][j].x = initialXdis + (int) j * 30;
								bubbles[i][j].y = initialYDis + i * 30;
								bubbles[i][j].bitmap = bubbles_normal[index];
								bubbles[i][j].destroy = false;
								bubbles[i][j].noOfShiftedRows = 0;
								bubbles[i][j].markedCheck = false;
							}
							// positions[i] = new Point(30 + (int) j * 30, i*
							// 30);

						} else {
							if(bubbles[i][j] ==null){
							bubbles[i][j] = new Bubble(bubbles_normal[index],
									initialXdis + 15 + (int) j * 30,
									initialYDis + i * 30);
							// positions[i] = new Point(45 + (int) j * 30, i *
							// 30);
						}else{
							bubbles[i][j].x = initialXdis + 15 + (int) j * 30;
							bubbles[i][j].y = initialYDis + i * 30;
							bubbles[i][j].bitmap = bubbles_normal[index];
							bubbles[i][j].destroy = false;
							bubbles[i][j].noOfShiftedRows = 0;
							bubbles[i][j].markedCheck = false;	
							}
						}
						bubbles[i][j].colorIndex = index;
						numOfBubble++;
					}
				} else {

					bubbles[i][j] = new Bubble();

				}
				numOfBubblesGen++;
			}

		}

	}

	public GameScreen(Context context) {

		super(context);
		initialize();
		width = 300;
		height = 590;

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		
		reInit();
		int index = 0;

		for (int i = 0; i < numOfWaitingBubbles; i++) {
			index = random.nextInt(8);
			while (colorsPresented[index] <= 0) {
				index = random.nextInt(8);
			}
			Bubble curr = new Bubble(bubbles_normal[index], 2 * initialXdis + i
					* 30, height - initialYDis);
			curr.colorIndex = index;
			waitingBubbles[i] = curr;
		}
	
		// create the game loop thread
		thread = new GameThread(getHolder(), this);
		// sounds
		sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		collide = sounds.load(context, R.raw.stick, 1);
		destroyGroup = sounds.load(context, R.raw.destroy_group, 1);
		loseSound = sounds.load(context, R.raw.lose, 1);
		winSound = sounds.load(context, R.raw.clappings, 1);
		rebound = sounds.load(context, R.raw.rebound, 1);
		// make the GamePanel focusable so it can handle events
		setFocusable(true);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		// thread.setRunning(true);
		thread.start();
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		//		Write the touch data
		String csv1 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +  "/User-"+ Id + "/Touch_Bubble.csv";
		CSVWriter writer_touch = null;
		try {
			writer_touch = new CSVWriter(new FileWriter(csv1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int j=0;j<touchData.size(); j += 1) {
			writer_touch.writeNext(new String[]{touchData.get(j).toString()});
		}
		try {
			writer_touch.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		End of writing


		while (retry) {
			try {
				thread.stopThread();
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}

		Log.d(TAG, "Thread was shut down cleanly");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		My code!!
		float xp,yp;
		int action = event.getAction();
		switch (action){
			case MotionEvent.ACTION_DOWN:{
				timestamp = System.nanoTime();
				xp = event.getXPrecision();
				yp = event.getYPrecision();
				x = event.getX()/xp;
				y = event.getY()/yp;
				break;
			}
			case MotionEvent.ACTION_UP:{
				timestampUp = System.nanoTime();
				TouchVals touchVals = new TouchVals(x, y, timestamp, timestampUp);
				touchData.add(touchVals);
				count++;
				break;
			}
		}
		if(count == touchLim){
			BubbleShooterActivity fin = (BubbleShooterActivity)getContext();
			fin.onDestroy();
		}


//		End pf my code

		if (lose) {
			lose = false;
			reInit();
		} else {
			if (numOfBubble == 0) {
				if (levelN < 13) {
					levelN++;
					Log.d("Level no", levelN + "");
					reInit();
				}
			} else {
				if (!moving) {
					float desiredY = event.getY();
					if (desiredY - waitingBubbles[numOfWaitingBubbles - 1].y > -5) {

					} else {
						Bubble removed = waitingBubbles[numOfWaitingBubbles - 1];
						// changing bubble position
						movingBubble.bitmap = bubbles_normal[removed.colorIndex];
						movingBubble.x = removed.x;
						movingBubble.y = removed.y;
						
						movingBubble.colorIndex = removed.colorIndex;
						movingBubble.destroy = false;
						

						synchronized (waitingBubbles) {
							for (int i = numOfWaitingBubbles - 2; i >= 0; i--) {
								waitingBubbles[i + 1].colorIndex = waitingBubbles[i].colorIndex;
								waitingBubbles[i + 1].bitmap = bubbles_normal[waitingBubbles[i + 1].colorIndex];

							}
						}

						int index = random.nextInt(8);
						while (colorsPresented[index] == 0) {
							index = random.nextInt(8);
						}
						waitingBubbles[0].colorIndex = index;
						waitingBubbles[0].bitmap = bubbles_normal[index];
						// add new Bubble

						float desiredX = event.getX();
						deltaX = ((desiredX - movingBubble.x) / noOfSteps);
						
						slope = (desiredY - movingBubble.y)
								/ (desiredX - movingBubble.x);
						moving = true;
					}
				}
			}
		}
		try {
			GameThread.sleep(16);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.BLACK);


		// fills the canvas with black

		canvas.drawBitmap(backgroundBMP, 165 - (backgroundBMP.getWidth() / 2),
				80, null);
		canvas.drawText("score", 20, height - 70, paint);
		canvas.drawText(score + "", 30, height - 50, paint);
		if (numOfBubble == 0) {

			for (int i = 0; i < colorsPresented.length; i++) {
				colorsPresented[i] = 0;
			}
			if (levelN >= 3) {
				canvas.drawBitmap(congrats, 0, 100, paint);
			} else {
				canvas.drawBitmap(winPanel, 0, 100, paint);
			}
		} else if (!lose) {
			drawBubbles(canvas);

			if (!movingBubble.destroy)
				movingBubble.draw(canvas);
			update();
			// draw compressor
			canvas.drawBitmap(compressor, 5, -30 + (30 * noOfShiftedRows),
					paint);

		} else {
			canvas.drawBitmap(losePanel, 0, 100, paint);
			for (int i = 0; i < colorsPresented.length; i++) {
				colorsPresented[i] = 0;
			}
		}

	}

	/**
	 * while queue is not empty poll a bubble from the queue
	 */

	static private void checkBubbleToDestroy() {
		int numOfBubblesChecked = 1;
		while (bubblePositionsToDestroy.size() > 0) {
			Log.d("Color", colorIndexDest + "");
			Point curr = bubblePositionsToDestroy.poll();
			int posX = curr.x;
			int posY = curr.y;
			Log.d("Points ", curr.x + " " + curr.y);
			// check its neighbor
			// check the right position
			if (posX < bubblewidth - 1) {
				if (!bubbles[posY][posX + 1].destroy
						&& !bubbles[posY][posX + 1].markedCheck) {
					if (bubbles[posY][posX + 1].colorIndex == colorIndexDest) {
						bubbles[posY][posX + 1].markedCheck = true;
						bubblePositionsToDestroy.add(new Point(posX + 1, posY));
						numOfBubblesChecked++;
					}
				}
			}
			// check the left position
			if (posX > 0) {
				if (!bubbles[posY][posX - 1].destroy
						&& !bubbles[posY][posX - 1].markedCheck) {
					if (bubbles[posY][posX - 1].colorIndex == colorIndexDest) {
						bubbles[posY][posX - 1].markedCheck = true;
						bubblePositionsToDestroy.add(new Point(posX - 1, posY));
						numOfBubblesChecked++;
					}
				}
			}
			// check the top and down
			if (posY % 2 == 0) {
				// even row

				if (posY < bubbleHight - 1) {
					// check bottom right
					if (!bubbles[posY + 1][posX].destroy
							&& !bubbles[posY + 1][posX].markedCheck) {
						if (bubbles[posY + 1][posX].colorIndex == colorIndexDest) {
							bubbles[posY + 1][posX].markedCheck = true;
							bubblePositionsToDestroy.add(new Point(posX,
									posY + 1));
							numOfBubblesChecked++;
						}
					}
					// check bottom left
					if (posX > 0) {
						if (!bubbles[posY + 1][posX - 1].destroy
								&& !bubbles[posY + 1][posX - 1].markedCheck) {
							if (bubbles[posY + 1][posX - 1].colorIndex == colorIndexDest) {
								bubbles[posY + 1][posX - 1].markedCheck = true;
								bubblePositionsToDestroy.add(new Point(
										posX - 1, posY + 1));
								numOfBubblesChecked++;
							}
						}
					}
				}

				if (posY > 0) {
					// check top right
					if (!bubbles[posY - 1][posX].destroy
							&& !bubbles[posY - 1][posX].markedCheck) {
						if (bubbles[posY - 1][posX].colorIndex == colorIndexDest) {
							bubbles[posY - 1][posX].markedCheck = true;
							bubblePositionsToDestroy.add(new Point(posX,
									posY - 1));
							numOfBubblesChecked++;
						}
					}
					// check top left
					if (posX > 0) {
						if (!bubbles[posY - 1][posX - 1].destroy
								&& !bubbles[posY - 1][posX - 1].markedCheck) {
							if (bubbles[posY - 1][posX - 1].colorIndex == colorIndexDest) {
								bubbles[posY - 1][posX - 1].markedCheck = true;
								bubblePositionsToDestroy.add(new Point(
										posX - 1, posY - 1));
								numOfBubblesChecked++;
							}
						}
					}
				}

			} else {
				// odd row
				if (posY < bubbleHight - 1) {
					// check bottom right
					if (posX < bubblewidth - 1) {
						if (!bubbles[posY + 1][posX + 1].destroy
								&& !bubbles[posY + 1][posX + 1].markedCheck) {
							if (bubbles[posY + 1][posX + 1].colorIndex == colorIndexDest) {
								bubbles[posY + 1][posX + 1].markedCheck = true;
								bubblePositionsToDestroy.add(new Point(
										posX + 1, posY + 1));
								numOfBubblesChecked++;
							}
						}
					}
					// check bottom left
					if (!bubbles[posY + 1][posX].destroy
							&& !bubbles[posY + 1][posX].markedCheck) {
						if (bubbles[posY + 1][posX].colorIndex == colorIndexDest) {
							bubbles[posY + 1][posX].markedCheck = true;
							bubblePositionsToDestroy.add(new Point(posX,
									posY + 1));
							numOfBubblesChecked++;
						}
					}
				}
				if (posY > 0) {
					// check top right
					if (posX < bubblewidth - 1) {
						if (!bubbles[posY - 1][posX + 1].destroy
								&& !bubbles[posY - 1][posX + 1].markedCheck) {
							if (bubbles[posY - 1][posX + 1].colorIndex == colorIndexDest) {
								bubbles[posY - 1][posX + 1].markedCheck = true;
								bubblePositionsToDestroy.add(new Point(
										posX + 1, posY - 1));
								numOfBubblesChecked++;
							}
						}
					}
					// check top left
					if (!bubbles[posY - 1][posX].destroy
							&& !bubbles[posY - 1][posX].markedCheck) {
						if (bubbles[posY - 1][posX].colorIndex == colorIndexDest) {
							bubbles[posY - 1][posX].markedCheck = true;
							bubblePositionsToDestroy.add(new Point(posX,
									posY - 1));
							numOfBubblesChecked++;
						}
					}
				}

			}

		}

		// check to destroy
		if (numOfBubblesChecked > 2) {
			score = score + numOfBubblesChecked;
			sounds.play(destroyGroup, 5.0f, 5.0f, 1, 0, 1.5f);
			for (int i = 0; i < bubbleHight; i++) {
				for (int j = 0; j < bubblewidth; j++) {
					if (!bubbles[i][j].destroy && bubbles[i][j].markedCheck) {

						bubbles[i][j].destroy = true;
						numOfBubble--;
						colorsPresented[bubbles[i][j].colorIndex]--;
					}
				}
			}
			// check if the ball should fall (no bubbles hold it)
			for (int i = 0; i < bubbleHight; i++) {
				for (int j = 0; j < bubblewidth; j++) {
					if (!bubbles[i][j].destroy && i > 0) {
						if (i % 2 == 0) {
							if (j > 0) {
								if (bubbles[i - 1][j - 1].destroy
										&& bubbles[i - 1][j].destroy) {
									bubbles[i][j].destroy = true;
									bubbles[i][j].markedCheck = true;
									colorsPresented[bubbles[i][j].colorIndex]--;
									numOfBubble--;
								}

							} else {
								if (bubbles[i - 1][j].destroy) {
									bubbles[i][j].destroy = true;
									bubbles[i][j].markedCheck = true;
									colorsPresented[bubbles[i][j].colorIndex]--;
									numOfBubble--;
								}
							}
						} else {
							if (j < bubblewidth - 1) {
								if (bubbles[i - 1][j + 1].destroy
										&& bubbles[i - 1][j].destroy) {
									bubbles[i][j].destroy = true;
									bubbles[i][j].markedCheck = true;
									colorsPresented[bubbles[i][j].colorIndex]--;
									numOfBubble--;
								}
							} else {
								if (bubbles[i - 1][j].destroy) {
									bubbles[i][j].destroy = true;
									bubbles[i][j].markedCheck = true;
									colorsPresented[bubbles[i][j].colorIndex]--;
									numOfBubble--;
								}
							}
						}

					}
				}

			}
		} else {
			for (int i = 0; i < bubbleHight; i++) {
				for (int j = 0; j < bubblewidth; j++) {
					if (!bubbles[i][j].destroy) {
						bubbles[i][j].markedCheck = false;
					}
				}
			}
		}
		if (numOfBubble == 0) {
			sounds.play(winSound, 5.0f, 5.0f, 1, 0, 1.5f);
		}

	}

	static  void update() {
		if (!movingBubble.destroy) {
			if (moving) {
				if (movingBubble.x >= initialXdis + 30 * bubblewidth) {
					deltaX = -deltaX;
					sounds.play(rebound, 5.0f, 5.0f, 1, 0, 1.5f);
				}
				if (movingBubble.x <= initialXdis + 5) {
					deltaX = -1 * deltaX;
					sounds.play(rebound, 5.0f, 5.0f, 1, 0, 1.5f);
				}

				movingBubble.x = (int) (movingBubble.x + deltaX);
				int deltaY = Math.abs((int) (slope * deltaX));

				movingBubble.y = movingBubble.y - deltaY;

				checkCollision();

				checkBubbleToDestroy();
			}
		}
		checkLoser();
	}

	static private void checkLoser() {
		for (int i = bubbleHight - 1; i >= 0; i--) {
			for (int j = bubblewidth - 1; j >= 0; j--) {
				if (!bubbles[i][j].destroy) {
					if ((height - initialYDis - bubbles[i][j].y) < 5) {
						sounds.play(loseSound, 5.0f, 5.0f, 1, 0, 1.5f);
						lose = true;
					}
				}
			}
		}
	}

	static private void drawBubbles(Canvas canvas) {
		boolean change = false;
		if (t1 >= compressorPeriod && t1 % compressorPeriod == 0) {
			change = true;
			t1 = 0;
			noOfShiftedRows++;

			Log.d("no Of shifted Rows", noOfShiftedRows + "");
		}
		for (int i = 0; i < bubbleHight; i++) {
			for (int j = 0; j < bubblewidth; j++) {

				if (!bubbles[i][j].destroy) {

					if (change) {

						bubbles[i][j].noOfShiftedRows = 1;
						bubbles[i][j].y += bubbles[i][j].noOfShiftedRows * 30;
					}
					bubbles[i][j].draw(canvas);
				} else {
					if (bubbles[i][j].markedCheck) {
						// bubbles[i][j].x = bubbles[i][j].x+10;
						bubbles[i][j].y = bubbles[i][j].y + 15;
						bubbles[i][j].draw(canvas);
					}
				}
			}
		}
		if (!changingWaiting) {
			synchronized (waitingBubbles) {

				for (int i = 0; i < waitingBubbles.length; i++) {
					waitingBubbles[i].draw(canvas);
				}
			}
		}
		t1++;

	}

	/**
	 * check the collision for the moving bubble with the static bubble and put
	 * it in a suitable position on the array if it collides then put it on the
	 * queue (bubble to be destroyed)
	 */
	private static void checkCollision() {

		if (moving) {
			// check if hit the ceil
			if (movingBubble.y <= (initialYDis + (noOfShiftedRows * 30))) {
				int posX = (movingBubble.x - 30) / 30;
				Log.d("no Of shifted Rows", noOfShiftedRows + "");
				if (posX < 0) {
					posX = 0;
				}
				if (posX < bubblewidth) {
					if (bubbles[0][posX].destroy) {
						bubbles[0][posX].destroy = false;
						bubbles[0][posX].bitmap = bubbles_normal[movingBubble.colorIndex];
						bubbles[0][posX].x = posX * 30 + initialXdis;
						bubbles[0][posX].markedCheck = true;
						bubbles[0][posX].y = initialYDis
								+ (noOfShiftedRows * 30);
						bubbles[0][posX].colorIndex = movingBubble.colorIndex;
						bubbles[0][posX].noOfShiftedRows = noOfShiftedRows;
						moving = false;
						movingBubble.destroy = true;
						numOfBubble++;

						// VIP note in this case x and y in the bubble
						// represents
						// their index in the array
						bubblePositionsToDestroy.add(new Point(

						posX, 0));
						colorIndexDest = movingBubble.colorIndex;
						sounds.play(collide, 5.0f, 5.0f, 1, 0, 1.5f);
						colorsPresented[movingBubble.colorIndex]++;
					}
				}
			}
			for (int i = bubbleHight - 1; i >= 0 && moving; i--) {
				for (int j = bubblewidth - 1; j >= 0 && moving; j--) {
					Bubble curr = bubbles[i][j];
					if (!curr.destroy) {

						if ((curr.y + 30) >= movingBubble.y) {
							if (i < bubbleHight - 1) {
								if ((movingBubble.x - curr.x) >= -15
										&& (movingBubble.x - curr.x) <= 15) {// put

									// in

									// the

									// left

									// bottom
									Log.d("Hi", "bottom left  ");
									if (i % 2 == 0)

									{// for even rows
										if (j

										> 0) {
											if (bubbles[i + 1][j - 1].destroy) {
												bubbles[i + 1][j - 1].bitmap = bubbles_normal[movingBubble.colorIndex];
												bubbles[i + 1][j - 1].destroy = false;
												bubbles[i + 1][j - 1].x = curr.x - 15;
												bubbles[i + 1][j - 1].y = curr.y + 30;
												

												bubbles[i + 1][j - 1].markedCheck = true;
												bubbles[i + 1][j - 1].colorIndex = movingBubble.colorIndex;
												moving = false;
												movingBubble.destroy = true;
												numOfBubble++;
												bubblePositionsToDestroy
														.add(new Point(j - 1,
																i + 1));
												colorIndexDest = movingBubble.colorIndex;
												sounds.play(collide, 1.0f,
														1.0f, 0, 0, 1.5f);
												colorsPresented[movingBubble.colorIndex]++;

											}
										}
									} else {// for odd rows
										if (bubbles[i + 1][j].destroy) {
											bubbles[i + 1][j].destroy = false;
											bubbles[i + 1][j].bitmap = bubbles_normal[movingBubble.colorIndex];
											bubbles[i + 1][j].x = curr.x - 15;
											bubbles[i + 1][j].y = curr.y + 30;
											
											bubbles[i + 1][j].colorIndex = movingBubble.colorIndex;
											bubbles[i + 1][j].markedCheck = true;
											moving = false;
											movingBubble.destroy = true;
											numOfBubble++;
											bubblePositionsToDestroy
													.add(new Point(j,

													i + 1));
											colorIndexDest = movingBubble.colorIndex;
											sounds.play(collide, 1.0f, 1.0f, 0,
													0, 1.5f);
											colorsPresented[movingBubble.colorIndex]++;

										}
									}

								} else if ((movingBubble.x - curr.x) >= 15 &&

								(movingBubble.x - curr.x) <= 30) {// put

									// in

									// the

									// right

									// bottom
									Log.d("Hi", "bottom right  ");
									if (i % 2 == 0)

									{
										if (bubbles[i + 1][j].destroy) {
											bubbles[i + 1][j].destroy = false;
											bubbles[i + 1][j].bitmap = bubbles_normal[movingBubble.colorIndex];
											bubbles[i + 1][j].x = curr.x + 15;
											bubbles[i + 1][j].y = curr.y + 30;
											
											bubbles[i + 1][j].markedCheck = true;
											bubbles[i + 1][j].colorIndex = movingBubble.colorIndex;
											moving = false;
											movingBubble.destroy = true;
											numOfBubble++;
											bubblePositionsToDestroy
													.add(new Point(j,

													i + 1));
											colorIndexDest = movingBubble.colorIndex;
											sounds.play(collide, 1.0f, 1.0f, 0,
													0, 1.5f);
											colorsPresented[movingBubble.colorIndex]++;

										}
									} else {

										if (j

										< bubblewidth - 1) {
											if (bubbles[i + 1][j + 1].destroy) {
												bubbles[i + 1][j + 1].destroy = false;
												bubbles[i + 1][j + 1].bitmap = bubbles_normal[movingBubble.colorIndex];
												bubbles[i + 1][j + 1].x = curr.x + 15;
												bubbles[i + 1][j + 1].y = curr.y + 30;

												
												bubbles[i + 1][j + 1].colorIndex = movingBubble.colorIndex;
												bubbles[i + 1][j + 1].markedCheck = true;
												moving = false;
												movingBubble.destroy = true;
												numOfBubble++;
												bubblePositionsToDestroy
														.add(new Point(j + 1,

														i + 1));
												colorIndexDest = movingBubble.colorIndex;
												sounds.play(collide, 1.0f,
														1.0f, 0, 0, 1.5f);
												colorsPresented[movingBubble.colorIndex]++;

											}
										}
									}
								}
							}
						}
					

					}
				}
			}

		}

	}

}
