package com.bonfonte.wealth;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Sweets {

    /*
        Suppose 1 1 2 4 5 4 3 2 2 1
        solves  1 1 2 3 5 4 3 2 1 1

    */
    static void backFill(int[] scores, int[] sweets, int j){
        sweets[j] += 1;
        while(j-- > 0){
            if(scores[j] <= scores[j+1]) return;
            if(scores[j] > scores[j+1]){
                if(sweets[j] > sweets[j+1]) return;
                sweets[j] += 1;
            }
        }
    }

    // Complete the candies function below.
    static long candies(int n, int[] arr) {
        int curScore = Integer.MAX_VALUE;
        int[] sweets = new int[arr.length];
        int ups = 0;
        int downs = 0;
        int j = 0;
        while(j < arr.length){
            if(curScore < arr[j]){
                ups++;
                downs = 0;
                sweets[j] = ups;
            }
            else{
                ups = 0;
                if(curScore > arr[j]) downs++;
                if(downs > 1){
                    backFill(arr, sweets, j-1);
                }
                downs = 1;
            }
            curScore = arr[j++];
        }
        int xtra = 0;
        for(int x : sweets) xtra += x;
        return arr.length + xtra;
    }
    
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        int[] arr = new int[n];

        for (int i = 0; i < n; i++) {
            int arrItem = scanner.nextInt();
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
            arr[i] = arrItem;
        }

        long result = candies(n, arr);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
