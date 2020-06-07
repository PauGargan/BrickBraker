package brickBraker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class MapGenerator {

	public int map[][];
	public int wallWidth;
	public int wallHeight;
	public int brickWidth;
	public int brickHeight;
	
	public MapGenerator(int width, int height, int row, int col) {
		
		map = new int[row][col];
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				map[i][j] = 1;
			}
		}
		
		wallWidth = width;
		wallHeight = height;
		
		brickWidth = wallWidth/col;
		brickHeight = wallHeight/row;
		
	}
	
	public void draw(Graphics2D g) {
		
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				
				if(map[i][j] > 0) {
					g.setColor(Color.white);
					g.fillRect((j+1) * brickWidth, (i+1) * brickHeight, brickWidth, brickHeight);
					
					// Border
					g.setStroke(new BasicStroke(3));
					g.setColor(Color.black);
					g.drawRect((j+1) * brickWidth, (i+1) * brickHeight, brickWidth, brickHeight);
					
				}
				
			}
		}
		
	}
	
	public void setBrickValue(int value, int row, int col) {
		map[row][col] = value;
	}
	
	/*  gerBaseY()
	 * 	Returns the Y for the base of the map. It includes a top and bottom border  
	 *  of the same size as the bricks height.
	 */
	public int getBaseY() {
		return (map.length + 2) * brickHeight; 
	}
	
	public void getY() {
		
	}
}
