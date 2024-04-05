package com.hack.ubertest;
/*
Given a rectangular city grid:

- B R - -
R B B - -
- B - B -
- R - - -

"-" is street   (can only drive on street)
"R" is rider    (riders on street)
"B" is building (can not drive through building)

Given a driver position: 
Print all reachable riders' positions 
  from the driver 
  within a max number of steps.

input:
driver position = (0, 0)
max steps = 5
(city grid)

output:
[(0, 1), (1, 3)]

N, S, E, W directions only (no diagonal)

class Loc 
   Loc n, s, e, w.
   boolean 
Loc from .. .driver

    LinkedList<Loc> init = new... // driver driver
    Loc ... 0,0, 0,1
*/


import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    
    static void log(String s){
        System.out.println(s);
    }
    
    static class Loc{
        int x;
        int y; 
        char type; // B - R
        Loc n, s, e, w;
        boolean visited;
        int id;
        
        Loc(int x, int y, char type){
            this.x = x;
            this.y = y;
            this.type = type;
        }
        
        static void linkWest(Loc a, Loc b){
            if( a == null ) return;
            a.e = b;
            b.w = a;
        }
        
        static void linkNorth(Loc a, Loc b){
            if( a == null ) return;
            a.s = b;
            b.n = a;
        }
    
        List<Loc> findNbr(){
            List<Loc> result = Arrays.asList(n, s, e, w).stream()
                .filter(n -> n != null && n.type != 'B' && n.visited == false)
                .collect(Collectors.toList());
            return result;
        }
        
        @Override
        public String toString(){
            return "[" + x + "," + y + "]";
        }
        public String toString2(){
            return "[" + x + "," + y + "] n=" + n + " s=" + s + " e=" + e + " w=" + w;
        }
    }
    
    static class Problem{
        Loc driver;
        int maxdist;
        Problem( int max ){
            this.maxdist = max;
        }
    }
    
    static Problem buildMap(String[] grid, int maxdist, int xd, int yd){ // xd yd =>> driver
    
        Problem result = new Problem(maxdist);
        Loc driver = null;

        int y = 0;
        List<Loc> above = new ArrayList<>();
        
        for ( String street : grid ){
            int x = 0;
            Loc prev = null;
            List<Loc> line = new ArrayList<>();
            for(int i = 0 ; i < street.length(); i++) {
                Loc loc = new Loc(x, y, street.charAt(i));
                Loc.linkWest(prev, loc);
                if(above.size() > 0){
                    Loc.linkNorth(above.get(i), loc);
                }
                if(x == xd && y == yd) {
                    driver = loc;
                }
                prev = loc;
                line.add(loc);
                x++;
            }
            for(Loc l : line ){
                log(l.toString2());
            }
            above = line;
            y++;
        }
        result.driver = driver;
        return result;    
    }
  
    public static List<Loc> findPaths(Problem prob){
        // Cells a b c
        // Next d e f g 
        List<Loc> cells = Arrays.asList(prob.driver);
        List<Loc> riders = new ArrayList<>();
        int steps = 0;
        
        while(steps++ < prob.maxdist && cells.size() > 0){
            log("Scanning locs " + cells);
            List<Loc> next = new ArrayList<>();
            for(Loc loc : cells){
                if(loc.type == 'R'){
                    riders.add(loc);
                }
                // ... compute cells...
                List<Loc> nbrs = loc.findNbr();
                log("Neighbors:" + nbrs);
                next.addAll(loc.findNbr());                
                loc.visited = true;
            }    
            cells = next;
        }
        return riders;        
    }
    
    public static void main(String args[] ) throws Exception {
        String[] grid = { "-BR--", "RBB--", "-B-B-", "-R---" };
        Problem prob = buildMap(grid, 5, 0, 0);
        List<Loc> riders = findPaths(prob);
        log(riders + "");
        
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
    }
}