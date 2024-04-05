package com.hack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class SwapsTest {
    // Complete the minimumSwaps function below.
    static int findNextNonSortPos(int pos, int[] arr){
        while(pos < arr.length){
            if(arr[pos] != pos) break;
            pos++;
        }
        return pos;
    }
    
    static void log(String s) {
    	System.out.println(s);
    }
    
    static String asString(int[] arr) {
    	StringBuilder sb = new StringBuilder("[");
    	for(int i : arr) {
    		sb.append(' ');
    		sb.append(i);
    	}
    	sb.append(" ]");
    	return sb.toString();
    }

    static int makeSwaps(int pos, int[] arr){
        int swaps = 0;
        // Required: 0 1 2 3 4 5 6 // Starting pos = 2
        //               p n
        // State 1 : 0 1 3 5 4 6 2 // Swapping [2] = 3 for [3] = 5 => pos = 5
        
        // State 2 : 0 1 5 3 4 6 2 // Swapping [5] = 6 for [6] = 2 => pos = 2
        // State 3 : 0 1 5 3 4 2 6 // Swapping [2] = 5 for [5] = 2 => pos = 5
        // State 4 : 0 1 2 3 4 5 6 // Now [5] = 5 and we are done with a cycle.

        for(;;){
            int next = arr[pos];
            log("State " + swaps + ":" + asString(arr));
            if(next == pos) {
                log("Returning " + swaps + " swaps\n" + asString(arr));
            	return swaps;
            }
            
            log("Moving item at " + pos + "[" + next + "] to its correct location currently holding [" + arr[next] + "] -> setting pos to " + arr[next] );
            arr[pos] = arr[next];
            arr[next] = next;
            pos = arr[pos];
            swaps++;
        }
    }

    static void normalize(int[] arr){
        // Make the numbers and index conform:
        for(int i = 0; i < arr.length; i++){
            arr[i] = arr[i] - 1; // We want to avoid the stupid count from 1 issue.
        }        
    }

    static int minimumSwaps(int[] arr) {
        int total = 0;
        int pos = 0;
        normalize(arr);
        while(true){
            pos = findNextNonSortPos(pos, arr);
            if(pos >= arr.length) break;
            total += makeSwaps(pos, arr);
        }
        return total;
    }
    
    static String input = "5\n" + 
    		"2 3 5 1 4";

    public static void main(String[] args) throws IOException {
    	InputStream inpStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inpStream);// (System.in);
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        int[] arr = new int[n];

        String[] arrItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < n; i++) {
            int arrItem = Integer.parseInt(arrItems[i]);
            arr[i] = arrItem;
        }

        int res = minimumSwaps(arr);

  //      bufferedWriter.write(String.valueOf(res));
  //      bufferedWriter.newLine();

  //      bufferedWriter.close();
        log("Answer is " + res);
        
        scanner.close();
    }
}
