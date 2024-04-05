package com.hack;

/* 


Imagine we have an image. We'll represent this image as a simple 2D array where every pixel is a 1 or a 0. The image you get is known to have a single rectangle of 0s on a background of 1s. 

Write a function that takes in the image and returns the coordinates of the rectangle of 0's -- either top-left and bottom-right; or top-left, width, and height.

Sample output:
x: 3, y: 2, width: 3, height: 2


2,3 3,5


3,2 5,3 -- it's ok to reverse columns/rows as long as you're consistent




int[][] image = {
  {1, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 0, 0, 0, 1},
  {1, 1, 1, 0, 0, 0, 1},
  {1, 1, 1, 1, 1, 1, 1}
};
 */


public class ImageProblem {

	static class Row{
		int start = -1; // Start of first 0
		int end = -1; // End of the 0

		public Row(int[] input){ // inx 
			int inx = 0; 
			while (inx < input.length){
				if(input[inx] == 0){
					start = inx++;
					break;
				}
				inx++;
			}
			end = start;
			if(inx >= input.length){
				return;
			}
			while (inx < input.length){
				if(input[inx++] == 1){
					return;
				}
				end++;
			}
		}
	}

	static class Coords{
		int topleft = -1;
		int toprow = -1;
		int botright = -1;
		int botrow = -1;
		@Override
		public String toString(){
			return "x: " + topleft + ", y:" + toprow + ", width:" + (botright - topleft + 1) 
					+ ", height: " + (botrow - toprow + 1);
		}
	}

	static void log(String s){
		System.out.println(s);
	}

	static Coords getCoords(int[][] image){
		int startRow = -1;
		int endRow = -1;
		Row origRow = null;
		int inx = 0; 
		while (inx < image.length){
			Row row = new Row(image[inx]);
			if(row.start != -1){
				startRow= inx;
				origRow = row; 
				break;
			}
			inx++;
		}
		log("Got start row " + startRow );


		if(origRow == null){
			return new Coords();
		}

		while (inx < image.length){
			Row row = new Row(image[inx]);
			if(row.start == -1){
				endRow = inx-1;
				break;
			}
			inx++;
		}
		log("Got end row " + endRow );

		if(endRow == -1) endRow = image.length - 1;
		Coords result = new Coords();
		result.topleft = origRow.start;
		result.toprow = startRow;
		result.botright = origRow.end;
		result.botrow = endRow;
		return result;
	}



	/*
	 * To execute Java, please define "static void main" on a class
	 * named Solution.
	 *
	 * If you need more classes, simply define them inline.
	 */

	public static void main(String[] args) {
		int[][] image = {
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 0, 0, 0, 1},
				{1, 1, 1, 0, 0, 0, 1},
				{1, 1, 1, 1, 1, 1, 1}
		};

		Coords coords = getCoords(image);
		System.out.println(coords);

		
		int[][] image2 = { { 1, 1, 1, 0 }, { 1, 1, 1, 1 } };
		System.out.println(getCoords(image2));
		
	}
}


