package com.bonfonte.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

public class CityLibrary {

	static final boolean DEBUG = true;
	
	static void dbg(String s) {
		if(DEBUG) log(s);
	}

	static void log(String s) {
		System.out.println(s);
	}
	
	static class City{
		Set<City> roads = new HashSet<>();
		int id;
		boolean visited = false;
		
		public City(int id) {
			this.id = id;
		}
		
		public void connect(City b) {
			this.roads.add(b);
			b.roads.add(this);
		}
		
		public void clear() {
			this.visited = false;
		}
		
		public void walk(Consumer<City> todo) {
			if(visited) return;
			visited = true;
			todo.accept(this);
			for(City city : roads) {
				city.walk(todo);
			}
		}
		
		public String toString() {
			return "" + id;
		}
	}
	
	static class Graph{
		long costLib, costRoad;
		List<City> cities;
		List<List<City>> networks = new ArrayList<>();
		
		public Graph(int n, int c_lib, int c_road, int[][] cities) {
			this.costLib = c_lib;
			this.costRoad = c_road;
			this.cities = new ArrayList<>(n);
			for(int c = 1; c <= n; c++) {
				this.cities.add(new City(c));
			}
			for(int[] road : cities) {
				City a = this.cities.get(road[0]-1);
				City b = this.cities.get(road[1]-1);
				a.connect(b);
			}
			for(City city : this.cities) {
				if(city.visited) continue;
				final List<City> network = new ArrayList<>();
				city.walk(c -> network.add(c));
				networks.add(network);
			}
			clear();
		}
		
		public void clear() {
			for(City city : cities) city.clear();
		}
		
		public String toString() {
			String res = "Graph with " + cities.size() + " cities:"
					+ "\nCost Liby: " + costLib
					+ "\nCost Road: " + costRoad
					+ "\nNetworks : " + networks.size();
			
			int n = 0;
			for(List<City> network : networks) {
				n++;
				res += "\n N" + n + ": " + network;
			}
			return res;
		}
		
		public long calcMinCost() {
			int totalNets = networks.size();
			int totalCities = cities.size();
			long costOfLibPerCity = totalCities * costLib;
			long costOfLibPerNetwork = totalNets * costLib;
			long costOfNetworkRoads = networks.stream().mapToLong(list -> (list.size() - 1) * costRoad).sum();
			dbg("Cost of lib per city = " + costOfLibPerCity);
			dbg("Cost of lib per netw = " + costOfLibPerNetwork);
			dbg("Cost of netwrk roads = " + costOfNetworkRoads);
			return Math.min(costOfLibPerNetwork + costOfNetworkRoads, costOfLibPerCity);
		}
	}
	
	static long roadsAndLibraries(int n, int c_lib, int c_road, int[][] cities) {
		Graph g = new Graph(n, c_lib, c_road, cities);
		dbg("Got graph as follows: " + g);
		long minCost = g.calcMinCost();
		dbg("Got min cost as " + minCost);
		return minCost;
	}
	
	public static class TestCityLibrary{
		
		@Test
		public void test2() {
			int[][] roads = { {1, 3}, {3, 4}, {2, 4}, {1, 2}, {2, 3}, {5, 6}};
			long minCost = roadsAndLibraries(6, 2, 6, roads);
			Assert.assertEquals("Cost should be 12", 12L, minCost);
		}
	}
	
}
