package brickBraker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {

	// ---------------------------
	// ####   Graphic values   ####
	// ----------------------------
	// # Gameplay
	private int width = 690;
	private int height = 590;	
	// # Paddle
	private int paddlePosY = 550;
	private int paddleWidth = 100;
	private int paddleHeight = 8;
	// # Ball
	private final int ballDiam = 20;
	private int ballPosX;
	private int ballPosY;
	// # Bricks
	private final int brickWallWidth = 540;
	private final int brickWallHeight = 150;
	private final int brickRows = 3;
	private final int brickCols = 7;
	private MapGenerator bricks;
	// ---------------------------
	
	// ----------------------------
	// ####   Initial values   ####
	// ----------------------------
	// # Initial position X of the paddel
	// TODO change to static final
	private static final int INIT_PLAYER_X = 310;
	private final int initPlayerX = 310;
	// # Initial direction for the ball
	private final int initBallXDir = -1;
	private final int initBallYDir = -2;
	// # Initial amount of bricks
	private final int initTotalBricks = 21;
	// # Initial score
	private final int initScore = 0;
	// ---------------------------
	
	// ---------------------------
	// ####  Game attributes  ####
	// ---------------------------
	// # General
	private boolean play = false;
	private int score;
	private final int hitScore = 5;
	private int totalBricks;
	// # Player movement
	private int playerX = 310;
	private int playerStep = 20;
	private int ballXDir = -1;
	private int ballYDir = -2;
	// # Timer
	private Timer timer;
	private int delay = 8;
	// ---------------------------
	
	public Gameplay() {
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		timer = new Timer(delay, this);
		timer.start();
		
		bricks = new MapGenerator(brickWallWidth, brickWallHeight, brickRows, brickCols);
		
		initializeAttributes();
		
		// Initialize ball position in a random place between brick wall and paddle
		setBallPosY();
		setBallPosX();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		// Background
		g.setColor(Color.black);
		g.fillRect(3, 3, width, height);
		
		// Border
		// Adding 3 pixel yellow borders. Bottom is borderless 
		// so the ball can go through
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 3, height); 		// Left 
		g.fillRect(0, 0, width, 3);  		// Top 
		g.fillRect(width, 0, 3, height);    // Right 
		
		// Paddle
		g.setColor(Color.green);
		g.fillRect(playerX, paddlePosY, paddleWidth, paddleHeight);
		
		// Ball
		g.setColor(Color.red);
		g.fillOval(ballPosX, ballPosY, ballDiam, ballDiam);
		
		// Brick wall
		bricks.draw((Graphics2D) g);
		
		// Score board
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString(""+score, 590, 30);
		
		// Game over
		if(ballPosY > 570) {
			
			stopGame();
			showSign(g, "Game over!");
			
		}
		
		// Winning the game
		if(totalBricks <= 0) {
			
			stopGame();
			showSign(g, "You won!");
			
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		timer.start();
		if(play) {
			// Check if ball is touching paddle
			if(new Rectangle(ballPosX, ballPosY, ballDiam, ballDiam)
					.intersects(new Rectangle(playerX, paddlePosY, paddleWidth, paddleHeight))) {
				ballYDir = -ballYDir;
			}
			
			// Check if ball hit brick
			A:
			for(int i = 0; i < bricks.map.length; i++) {
				for(int j = 0; j < bricks.map[0].length; j++) {
					
					if(bricks.map[i][j] > 0) {
						int brickX = (j+1) * bricks.brickWidth;
						int brickY = (i+1) * bricks.brickHeight;
						int brickWidth = bricks.brickWidth;
						int brickHeight = bricks.brickHeight;
						
						Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballPosX, ballPosY, ballDiam, ballDiam);
						
						if(ballRect.intersects(brickRect)) {
							destroyBrick(i, j);
							
							// Make the ball bounce off the hit brick
							if(ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
								bounceX();
							} else {
								bounceY();
							}
							
							break A;
							
						}
					}
					
				}
			}
			
			ballPosX += ballXDir;
			ballPosY += ballYDir;
			
			// Check if ball is touching left border
			if(ballPosX < 0) {
				bounceX();
			}
			// Check if ball is touching top border
			if(ballPosY < 0) {
				bounceY();
			}
			// Check if ball is touching right border
			if(ballPosX > 670) {
				bounceX();
			}
		}
		
		repaint();
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		// If player presses right, check if paddle is touching 
		// right border. If not, move right. 
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			
			if(playerX < width-paddleWidth) {
				moveRight();
			}
			
		}
		// If player presses left, check if paddle is touching 
		// left border. If not, move right.
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			
			if(playerX > 3) {
				moveLeft();
			}
		}
		// If game is stopped and player presses Enter, restart it.
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(!play) {
				resetGame();
			}
		}
		
	}
	
	public void moveRight() {
		play = true;
		playerX += playerStep;
	}
	
	public void moveLeft() {
		play = true;
		playerX -= playerStep;
	}
	
	public void stopGame() {
		play = false;
		timer.stop();
		ballXDir = 0;
		ballYDir = 0;
	}
	
	public void resetGame() {
		play = true;
		initializeAttributes();
		bricks = new MapGenerator(brickWallWidth, brickWallHeight, brickRows, brickCols);
		setBallPosY();
		setBallPosX();
		
		repaint();
	}
	
	public void initializeAttributes() {
		ballXDir = initBallXDir;
		ballYDir = initBallYDir;
		playerX = INIT_PLAYER_X;
		score = initScore;
		totalBricks = initTotalBricks;
	}
	
	// Set the ball's Y to a random int between the base of the brick wall and the top of the paddle (+10 pixels breather)
	private void setBallPosY() {
		Random r = new Random();
		ballPosY = r.ints(bricks.getBaseY(), paddlePosY - 10).findFirst().getAsInt();
	}
	
	// Set the ball's X to a random int between the the left and right borders of the board.
	private void setBallPosX() {
		Random r = new Random();
		ballPosX = r.ints(10, width-10).findFirst().getAsInt();
	}
	
	// Changes direction of X to make ball bounce.
	private void bounceX() {
		ballXDir = -ballXDir;
	}
	
	// Changes direction of Y to make ball bounce.
	private void bounceY() {
		ballYDir = -ballYDir;
	}
	
	// If ball hit brick, remove the brick and increase score.
	private void destroyBrick(int i, int j) {
		bricks.setBrickValue(0, i, j);
		totalBricks--;
		score += hitScore;
	}

	private void showSign(Graphics g, String msg) {
		g.setColor(Color.red);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString(msg + " Time: " + timer.getDelay(), 190, 300);
		
		g.setFont(new Font("serif", Font.BOLD, 20));
		g.drawString("Press Enter to restart", 190, 350);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	
}
