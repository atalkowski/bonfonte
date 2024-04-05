package com.bonfonte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.bonfonte.util.DeepSet;
import com.bonfonte.util.MultQue;

public class FreeCell {
//	private static final Card KING_OF_HEARTS = new Card(Suit.HEARTS, Value.KING);
//	private static final Card KING_OF_CLUBS = new Card(Suit.CLUBS, Value.KING);
//	private static final Card KING_OF_DIAMONDS = new Card(Suit.DIAMONDS, Value.KING);
//	private static final Card KING_OF_SPADES = new Card(Suit.SPADES, Value.KING);
	public static final int DEBUG = 4;
	public static final int INFO = 3;
	public static final int WARN = 2;
	public static final int ERROR = 1;
	public static final int REPORT = 0;

	public static int LEVEL = INFO;

	Map<String, Column> colMap = new LinkedHashMap<>();
	List<Column> deck;
	List<Pile> piles; //= initPiles();
	List<Space> spaces; // = initList(4, Space::new);
	String name;
	String initialDeck;
	List<Move> solution;
	DeepSet<String> failedStates = new DeepSet<>(1000000);
	long timeTaken = 0;
	long totalMoves = 0;
	long validMoves = 0;
	int identicalMoveSearchDepth = 16;
	long maxValidMoves = 240L; // To be supplied
	long maxMoves = 10000000L; // To be supplied

	private static final void log(int level, String s, Object... args){
		if(level <= LEVEL){
			log(s, args);
		}
	}

	private static final String log(String s, Object... args){
		StringBuilder sb = new StringBuilder();
		int index = 0;
		int argn = 0;
		while(index < s.length()){
			char ch = s.charAt(index++);
			if(ch == '{'){
				if(index < s.length() && s.charAt(index) =='}'){
					index++;
					if(argn < args.length){
						Object arg = args[argn++];
						if(arg != null){
							sb.append(arg.toString());
						}else{
							sb.append("null");
						}
					}else{
						sb.append("{}");
					}
					continue;
				}
			}
			sb.append(ch);
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	private static class Move {//implements Comparable<Move>{
		Spot source;
		Spot target;
//		int score;
		public Move(Spot source, Spot target){
			this.source = source;
			this.target = target;
//			this.score = source.column.group.sourceScore + target.column.group.targetScore;
		}
//		@Override
//		public int compareTo(Move other) {
//			return 0;
//			// return Integer.compare(this.score, other.score);
//		}
		@Override
		public String toString(){
			return source.toString() + " -> " + target.toString();
		}
	}

	private static <T> List<T> initList(int size, Function<Integer,T> constructor){
		List<T> result = new ArrayList<>();
		for(int index = 0; index < size; index++){
			result.add(constructor.apply(index));
		}
		return result;
	}

	private enum Group{
		SPACE(2, 0), COLUMN(1, 2), PILE(0, 4);
		public int sourceScore;
		public int targetScore;
		private Group(int sourceScore, int targetScore){
			this.sourceScore = sourceScore;
			this.targetScore = targetScore;
		}
	}

	private static class Spot implements Comparable<Spot>{
		// Some card location in game e.g. SPACE-3 or COLUMN-6 or PILE-2 (clubs)
		String key;
		Card card;
		int rank = 40;
		int id;
		public Spot(Column column, Card card){
			this.key = column.key;
			this.card = card;
			this.id = column.id;
		}
		@Override
		public String toString(){
			return key + " " + Card.display(card);
		}
		public Column getColumn(Map<String, Column>colMap){
			return colMap.get(key);
		}
		public boolean sameAs(Spot other){
			if(other == null){
				return false;
			}
			return other.key.equals(this.key);
		}
		void setRank(FreeCell game){
			Column col = game.deck.get(id);
			rank = 30 - col.size();
			for(int index = 0; index<col.size(); index++){
				Card card = col.top(index);
				if(game.piles.get(card.suit.id).accepts(card)){
					rank = col.size() - index;
					break;
				}
			}
		}
		@Override
		public int compareTo(Spot o) {
			return Integer.compare(this.rank, o.rank);
		}
	}

	private enum SuitColor{
		RED, BLACK;
	}

	private enum Suit{
		HEARTS(SuitColor.RED, "h", 0),
		CLUBS(SuitColor.BLACK, "c", 1),
		DIAMONDS(SuitColor.RED, "d", 2),
		SPADES(SuitColor.BLACK, "s", 3);
		SuitColor color;
		String display;
		int id;
		private Suit(SuitColor color, String display, int id){
			this.color = color;
			this.display = display;
			this.id = id;
		}
		public static Suit from(char ch){
			switch(ch){
			case 'H': case 'h': return HEARTS;
			case 'C': case 'c': return CLUBS;
			case 'D': case 'd': return DIAMONDS;
			case 'S': case 's': return SPADES;
			}
			return null;
		}
		public static Suit from(int index){
			switch(index%4){
			case 0: return HEARTS;
			case 1: return CLUBS;
			case 2: return DIAMONDS;
			default: return SPADES;
			}
		}
		public boolean goesUnder(Suit suit){
			return this.color != suit.color;
		}
		public boolean accepts(Suit suit){
			return this == suit;
		}
	}

	private enum Value{
		ACE("A", 1), TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5),
		SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9), TEN("T", 10),
		JACK("J", 11), QUEEN("Q", 12), KING("K", 13),;

		String pip;
		int faceValue;
		private Value(String pip, int value){
			this.pip = pip;
			this.faceValue = value;
		}
		public boolean goesUnder(Value value){
			return faceValue == value.faceValue - 1;
		}
		public boolean accepts(Value value){
			return faceValue == value.faceValue + 1;
		}
		public static Value from(String ch){
			for(Value val : Value.values()){
				if(val.pip.equals(ch)){
					return val;
				}
			}
			return null;
		}
		public static Value from(int index){
			int pipVal = (index%13)+1;
			for(Value val : values()){
				if(val.faceValue == pipVal){
					return val;
				}
			}
			return null;
		}
	}

	private static class Card{
		Suit suit;
		Value value;
		public static Card makeCard(String text){
			if(text == null || text.length() != 2){
				return null;
			}
			Value value = Value.from(text.substring(0,  1));
			Suit suit = Suit.from(text.charAt(1));
			if(value == null || suit == null){
				return null;
			}
			return new Card(suit, value);
		}
		public Card(Suit suit, Value value){
			this.suit = suit;
			this.value = value;
		}
		public boolean goesUnder(Card other){
			return this.suit.goesUnder(other.suit)
					&& this.value.goesUnder(other.value);
		}
		public boolean accepts(Card other){
			return this.suit.accepts(other.suit)
					&& this.value.accepts(other.value);
		}
		@Override
		public String toString(){
			return value.pip + suit.display;
		}
		public static String display(Card card){
			if(card == null){
				return "--";
			}
			return card.toString();
		}
		@Override
		public boolean equals(Object other){
			if(other == null || !other.getClass().equals(this.getClass())){
				return false;
			}
			Card card = (Card)other;
			return card.suit == this.suit && card.value == this.value;
		}
	}

	private static class Column implements Comparable<Column>{
		Stack<Card> cards = new Stack<>();
		Group group;
		StringBuilder sb = new StringBuilder();
		int id;
		String key;
		public Column(int id){
			this(Group.COLUMN, id);
		}
		public Column(Group group, int id){
			this.group = group;
			this.id = id;
			this.key = group.name() + id;
		}
		public boolean isEmpty(){
			return cards.isEmpty();
		}
		public boolean notEmpty(){
			return !cards.isEmpty();
		}
		public Card top(int depth){
			if(depth < 0 || depth >= size()){
				return null;
			}
			int index = size() - depth - 1;
			return cards.elementAt(index);
		}
		public Card top(){
			return cards.empty() ? null : cards.peek();
		}
		public Card orderByCard(){
			return cards.isEmpty() ? null : cards.elementAt(0);
		}
		public Card pop(){
			return cards.empty() ? null : cards.pop();
		}
		public void push(Card card){
			if(card == null){
				return;
			}
			cards.push(card);
		}
		public boolean canAdd(Card card){
			if(isEmpty()){
				return true;
			}
			return card.goesUnder(top());
		}
		public Card getCardInLine(int line){
			if(line < cards.size()){
				return cards.get(line);
			}
			return null;
		}
		public int size(){
			return cards.size();
		}
		public String state(){
			sb.setLength(0);
			for(Card card:cards){
				sb.append(card.toString());
			}
			return sb.toString();
		}
		public String description(){
			return group + "[" + id + "]";
		}
		@Override
		public int compareTo(Column other) {
			Card mine = this.orderByCard();
			Card hers = this.orderByCard();
			if(mine == null){
				return hers == null ? 0 : 1;
			}
			if(hers == null){
				return -1;
			}
			int result = Integer.compare(mine.value.faceValue, hers.value.faceValue);
			if(result != 0){
				return 0;
			}
			return mine.suit.compareTo(hers.suit);
		}
		public boolean sameAs(Object other){
			if(other != null){
				if(other.getClass().equals(this.getClass())){
					Column column = (Column)other;
					return column.id == this.id && column.group == this.group;
				}
			}
			return false;
		}
	}

	private static class Space extends Column{
		public Space(int id){
			super(Group.SPACE, id);
		}
		@Override
		public boolean canAdd(Card card){
			return this.isEmpty();
		}
		@Override
		public Card orderByCard(){
			return top();
		}
	}

	private static class Pile extends Column{
		Suit suit;
		public Pile(int id, Suit suit){
			super(Group.PILE, id);
			this.suit = suit;
		}
		boolean accepts(Card card){
			if (!card.suit.accepts(this.suit)){
				return false;
			}
			Card topCard = top();
			if(topCard == null){
				return card.value == Value.ACE;
			}
			return card.accepts(topCard);
		}
		@Override
		public String state(){
			return cardState(this.top());
		}
		@Override
		public Card orderByCard(){
			return top();
		}
	}

	private void makeMove(Move move){
		Card card = move.source.getColumn(colMap).pop();
		move.target.getColumn(colMap).push(card);
		totalMoves++;
		validMoves++;
		if(totalMoves%1000 == 0L){
			log(INFO, "{} moves: valid {}:\n{}", totalMoves, validMoves, showCards());
			if(totalMoves%1000000 == 0 || validMoves > 1000){
				log(DEBUG, "Status after {}:\n{}", totalMoves, this.showCards());
				log(DEBUG, "State:{}", this.state());
			}

		}
	}

	private void peekMove(Move move){
		Card card = move.source.getColumn(colMap).pop();
		move.target.getColumn(colMap).push(card);
	}

	private String failState(){
		String state = state();
		this.failedStates.add(state);
		return state;
	}

	private void undoMove(Move move){
		Card card = move.target.getColumn(colMap).pop();
		move.source.getColumn(colMap).push(card);
		validMoves--;
	}

	private void peekUndo(Move move){
		Card card = move.target.getColumn(colMap).pop();
		move.source.getColumn(colMap).push(card);
	}

	private void addToMap(List<? extends Column> cols){
		cols.forEach(col -> colMap.put(col.key, col));
	}

	private void initAllDecks(String values){
		piles = initPiles();
		spaces = initList(4, Space::new);
		deck = initList(8, Column::new);
		addToMap(spaces);
		addToMap(deck);
		addToMap(piles);

		String[] groups = values.split("/");
		int groupIndex = 0;
		while(groupIndex < groups.length){
			String[] cards = groups[groupIndex++].split(" ");
			for(int cardIndex = 0; cardIndex < cards.length; cardIndex++){
				Card card = Card.makeCard(cards[cardIndex]);
				if(card == null){
					continue;
				}
				switch(groupIndex){
				case 1: // Spaces
					if(cardIndex < 4){
						Space space = spaces.get(cardIndex);
						space.push(card);
					}
				break;
				case 2: // Piles
					if(cardIndex < 4){
						Pile pile = piles.get(cardIndex);
						for(Value val : Value.values()){
							if(val.faceValue > card.value.faceValue){
								break;
							}
							pile.push(new Card(card.suit, val));
						}
					}
				break;
				default:
					int deckNo = cardIndex % 8;
					Column column = deck.get(deckNo);
					column.push(card);
				}
			}
		}
	}

	private String displaySlotCard(Card card){
		return "|" + " " + Card.display(card) + " ";
	}

	private String displayPile(Pile pile){
		return displaySlotCard(pile.top());
	}

	private static List<Pile> initPiles(){
		List<Pile> result = new ArrayList<>(4);
		int index = 0;
		for(Suit suit: Suit.values()){
			Pile pile = new Pile(index++, suit);
			result.add(pile);
		}
		return result;
	}

	public int findMaxColumn(){
		int max = 0;
		for(int col = 0; col < 8; col++){
			int colSize = deck.get(col).size();
			if(colSize > max){
				max = colSize;
			}
		}
		return max;
	}

	private static String centerTitle(String text, int length){
		String title = (text == null || text.trim().isEmpty()) ? "" : text;
		boolean padLeft = false;
		title = " " + title + " ";
		while(title.length() < length){
			if(padLeft){
				title = "=" + title;
			}else{
				title = title + "=";
			}
			padLeft = !padLeft;
		}
		return title;
	}

	private static String cardState(Card card){
		if(card==null){
			return "-";
		}
		return card.toString();
	}

	public <T extends Column> List<T> sort(List<T> columns){
		List<T> copy = new ArrayList<>();
		copy.addAll(columns);
		Collections.sort(copy);
		return copy;
	}

	public String state(){
		StringBuilder sb = new StringBuilder();
		List<Space> sortedSpaces = sort(spaces);
		for(int i = 0; i < 4; i++){
			Card next = sortedSpaces.get(i).top();
			sb.append(cardState(next));
		}
		sb.append("/");
		// Piles are already sorted
		for(Pile pile : piles){
			sb.append(pile.state());
		}
		sb.append("/");
		for(Column column : sort(deck)){
			sb.append(column.state());
		}
		return sb.toString();
	}

	public String showCards(){
	/*
	 * ==========================================
	 * | Td | 2c | -- | -- | Ah- | -- | 2d | -- |
	 * ==========================================
	 * | -- | 2c | -- | -- | Ah- | -- | 2d | -- |
	 *
	 */
		StringBuilder sb = new StringBuilder();
		String dline = "=========================================";
		sb.append("\n").append(dline).append("\n");
		for(int i = 0; i < 4; i++){
			sb.append(displaySlotCard(spaces.get(i).top()));
		}
		for(Pile pile : piles){
			sb.append(displayPile(pile));
		}
		sb.append("|\n");
		String title = centerTitle(name, dline.length());
		sb.append(title);
		int depth = findMaxColumn();
		for(int line = 0; line < depth; line++){
			sb.append("\n");
			for(Column column:deck){
				Card card = column.getCardInLine(line);
				sb.append(displaySlotCard(card));
			}
			sb.append('|');
		}
		sb.append("\n").append(dline).append("\n");

		return sb.toString();
	}

	public boolean gameOver(){
		return totalMoves >= maxMoves;
	}

	public FreeCell(String cardList, String name, long maxSolutionMoves, long maxMoves, int identicalMoveSearchDepth){
		this.name = name;
		this.initialDeck = cardList;
		this.maxMoves = maxMoves;
		this.maxValidMoves = maxSolutionMoves;
		this.identicalMoveSearchDepth = identicalMoveSearchDepth;
		initAllDecks(cardList);
	}

	public String displayMoves(List<Move> moves){
		StringBuilder sb = new StringBuilder(" total=" + moves.size()).append(":");
		moves.forEach(move -> sb.append("\n   ").append(move.toString()));
		return sb.toString();
	}

	public enum PlayOutcome{
		READY, MOVED, BINGO, OUT_OF_MOVES, HIT_MOVE_LIMIT;
	}

	public static class Play{
		FreeCell game;
		Play parent;
		Play child;
		List<Move> possibleMoves;
		Move move;

		int movePos = 0;
		PlayOutcome outcome;
		String state;
		public Play(FreeCell game, Play parent){
			this.game = game;
			this.parent = parent;
			if(parent != null){
				parent.child = this;
			}
			state = game.state();
			possibleMoves = new ArrayList<>();
			if(game.completed()){
				setState(PlayOutcome.BINGO);
				return;
			}

			possibleMoves = game.findPossibleMoves(this);
			if(possibleMoves.isEmpty()){
				setState(PlayOutcome.OUT_OF_MOVES);
			}else{
				setState(PlayOutcome.READY);
			}
		}

		public PlayOutcome setState(PlayOutcome outcome){
			this.outcome = outcome;
			return outcome;
		}

		PlayOutcome findASolution(){
			boolean outOfMoves = movePos >= possibleMoves.size();
			if(move != null){ // Undo previous failed move:
				if(outOfMoves) {
					game.failState(); // Record failed states in size aware DeepSet
				}
				game.undoMove(move);
			}
			if(outOfMoves){
				return setState(PlayOutcome.OUT_OF_MOVES);
			}
			if(game.gameOver()){
				return setState(PlayOutcome.HIT_MOVE_LIMIT);
			}
			move = possibleMoves.get(movePos++);
			game.makeMove(move);
			if(game.completed()){
				return setState(PlayOutcome.BINGO);
			}

			return setState(PlayOutcome.MOVED);
		}

	}

	boolean completed(){
		for(int suit = 0; suit < 4; suit++){
			if(piles.get(suit).size() < 13){
				return false;
			}
		}
		return true;
	}

	public void showMoves(int showFrequency, String description, List<Move> moves){
		if(moves == null) {
			log(INFO, "No {} moves found", description);
			return;
		}
		int index = 0;
		int showFreq = 0;
		FreeCell temp = new FreeCell(this.initialDeck, this.name, this.maxValidMoves, this.maxMoves, this.identicalMoveSearchDepth);
		log("Here are the {} moves:", description);
		log(temp.showCards());
		for(Move move : moves){
			temp.makeMove(move);
			index++;
			showFreq++;
			log("Move {}: {}", index, move.toString());
			if(showFreq >= showFrequency){
				log("Leading to this state:=>{}", temp.showCards());
				showFreq = 0;
			}
		}
	}

	public void showSolution(){
		showMoves(1, "solution", solution);
		showMoves(100, "moves", solution);
	}

	private List<Move> getFinalSolution(Play originalPlay){
		List<Move> moves = new ArrayList<>();
		Play play = originalPlay;
		while(play != null){
			if(play.move != null){
				moves.add(play.move);
			}
			play = play.child;
		}
		return moves;
	}

	/**************** STRATEGY ****************/
	public List<Move> findSolution(){
		String initialDeck = this.showCards();
		
		long now = System.currentTimeMillis();
		Play play = new Play(this, null);
		Play originalPlay = play;
		boolean running = true;
		List<Move> moves = null;
		if(this.completed()){
			moves = new ArrayList<>();
			log(REPORT, "Nothing to do .. game is completed!!");
		}
		while(running && play != null){
			PlayOutcome outcome = play.findASolution();
// Not working...
//			if(totalMoves % 1000L == 99L){
//				moves = getFinalSolution(originalPlay);
//				showMoves(false, "to date", moves);
//			}

			if(totalMoves % 100000L == 199L){
				List<Move> temp = getFinalSolution(originalPlay);
				showMoves(25, "to date", temp);
				System.out.println("Ready...");
			}

			switch(outcome){
			case READY:
			case MOVED:
				Play nextPlay = new Play(this, play);
				play = nextPlay;
				continue;
			case BINGO:
			case HIT_MOVE_LIMIT:
				moves = getFinalSolution(originalPlay);
				running = false;
				break;
			case OUT_OF_MOVES:
				play = play.parent; // exhausted this plays moves; go back and try another route;
				break;
			}
		}

		timeTaken = System.currentTimeMillis() - now;
		this.solution = moves;
		log("Initial state:\n{}", initialDeck);
		log("Final state:\n{}", this.showCards());
		showSolution();
		if(completed()){
			log("Found valid solution after " + totalMoves + " moves and " + timeTaken + "ms");
		}else{
			log("Did not find solution after " + totalMoves + " moves and " + timeTaken + "ms");
		}
		return moves;
	}

	private List<Spot> findOpenSpots(List<? extends Column> columns){
		return columns.stream().filter(Column::isEmpty)
				.map(column -> new Spot(column, null)).collect(Collectors.toList());
	}
	
	private List<Spot> getFirstOpenSpot(List<? extends Column> columns){
		return columns.stream().filter(Column::isEmpty)
				.map(column -> new Spot(column, null)).limit(1).collect(Collectors.toList());
	}

	private List<Spot> getTopSpots(List<? extends Column> columns){
		return columns.stream().filter(Column::notEmpty)
				.map(column -> new Spot(column, column.top())).collect(Collectors.toList());
	}

	private List<Spot> getTopPileSpots(){
		return piles.stream().filter(column -> column.size() > 2)
				.map(column -> new Spot(column, column.top())).collect(Collectors.toList());
	}

//	private List<Spot> getDeepSpots(List<? extends Column> columns, int depth){
//		return columns.stream().filter(column -> column.size() > depth)
//				.map(column -> new Spot(column, column.itemAt(depth))).collect(Collectors.toList());
//	}
//
//	private List<Spot> getAllDeepSpots(int depth){
//		List<Spot> spots = getDeepSpots(spaces, depth);
//		spots.addAll(getDeepSpots(deck, depth));
//		spots.addAll(getDeepSpots(piles, depth));
//		return spots;
//	}
	private int computeBuriedRank(Column column){
		int result = 0;
		int depth = 0;
		while(depth < column.size()){
			Card card = column.top(depth++);
			int pip = 3 - card.value.faceValue;
			if(pip > 0){
				result += depth * pip;
			}
		}
		return result;
	}
	
	private Optional<Move> makeAndCheckValidMove(Play play, Spot source, Spot target){
		Column column = target.getColumn(this.colMap);
		if(column.size() > 10){
			int buriedRank = computeBuriedRank(column);
			if(buriedRank > 10){
				return Optional.empty();
			}
		}
		Move move = new Move(source, target);
		peekMove(move);
		String newState = state();
		peekUndo(move);
		Play parent = play.parent;
		int depth = 0;
		while(parent != null){
			depth++;
			if(depth < identicalMoveSearchDepth){
				Move pMove = parent.move;
				// Disallow a move that is just moving the same card twice or undoing a recent move
				if(pMove.source.sameAs(source) && ( depth == 1 || pMove.target.sameAs(target))){
					// Don't undo a move on the stack!
				 	log(DEBUG, "Skipping repeated {} -> {}", source, target);
					return Optional.empty();
				}
				if(pMove.source.sameAs(target) && pMove.target.sameAs(source)){
					// Don't undo a move on the stack!
				 	log(DEBUG, "Skipping undo move {} -> {}", source, target);
					return Optional.empty();
				}
			}

			if(parent.state.equals(newState)){
				log(DEBUG, "Rejecting move {} as it is circular at parent {}",  move, depth);
				return Optional.empty();
			}
			parent = parent.parent;
		}
		if(failedStates.contains(newState)){
			return Optional.empty();
		}
		return Optional.of(move);
	}

	public static <T> List<T> join(List<T> left, List<T> right){
		List<T> res = new ArrayList<>();
		res.addAll(left);
		res.addAll(right);
		return res;
	}

	private static int random(int max){
		return ((int)(Math.random() * max))%max;
	}

	public static <T> List<T> randomize(List<T> list){
		List<T> res = new ArrayList<>();
		int size = list.size();
		while(size-- > 1){
			int item = random(size);
			T cell = list.remove(item);
			res.add(cell);
		}
		list.addAll(res);
		return list;
	}

	private void rankSpots(List<Spot> spots){
		spots.forEach(spot -> spot.setRank(this));
		Collections.sort(spots);
	}

	public static int findChain(Column column, int max){
		if(column.size() == 0){
			return 0;
		}
		int depth = 0;
		Card card = column.top(depth++);
		while(depth < column.size()){
			Card next = column.top(depth);
			if(card.goesUnder(next)){
				depth++;
				if(depth >= max){
					break;
				}
				card = next;
			}else{
				break;
			}
		}
		return depth;
	}
	
	private int findSolutionProximity(){
		int level = 0;
		for(Spot spot : getTopSpots(piles)){
			level += 2 * spot.card.value.faceValue;
		}
		return level;
		
	}
	
	private List<Column> orderColumnByBuriedRank(List<Column> columns){
		TreeMap<Integer, Column> sortMap = new TreeMap<>();
		int index = 0;
		for(Column col : columns){
			if(col.size() == 0){
				continue;
			}
			index++;
			int rank = computeBuriedRank(col) * 100 + index;
			sortMap.put(rank, col);
		}
		return sortMap.descendingKeySet().stream()
			.map(sortMap::get)
			.collect(Collectors.toList());
	}
	
	private List<Move> findPossibleMoves(Play play){
		if(this.validMoves >= maxValidMoves){
			if(this.validMoves >= maxValidMoves + findSolutionProximity()){
				return new ArrayList<>();
			}
		}
//		List<Spot> emptySpots = join(findOpenSpots(deck), findOpenSpots(spaces));
//		int totalEmpty = emptySpots.size();
//		List<Column> deckOrdered = orderColumnByBuriedRank(deck);
		List<Spot> deckTops = getTopSpots(deck);
		List<Spot> spaceTops = getTopSpots(spaces);
		List<Spot> openDeckSlot = getFirstOpenSpot(deck);
		List<Spot> openSpaceSlot = getFirstOpenSpot(spaces);
		List<Move> result = new ArrayList<>();
		rankSpots(deckTops);
		
		/* First find all moves which move cards up to the home piles */
		for(Spot source : join(deckTops, spaceTops)){
			for(Pile pile: piles){
				if(pile.accepts(source.card)){
					Spot target = new Spot(pile, pile.top());
					Optional<Move> move = makeAndCheckValidMove(play, source, target);
					if(move.isPresent()){
						result.add(move.get());
						break;
					}
				}
			}
		}


		/* Now find deck or space cards that can be replayed */
		for(Spot source : join(deckTops, spaceTops)){
			for(Column col: deck){
				if(col.canAdd(source.card)){
					Spot target = new Spot(col, col.top());
					Optional<Move> move = makeAndCheckValidMove(play, source, target);
					if(move.isPresent()){
						result.add(move.get());
					}
				}
			}
		}

		for (Spot target : join(openDeckSlot, openSpaceSlot)){
			for(Spot source : deckTops){
				Optional<Move> move = makeAndCheckValidMove(play, source, target);
				if(move.isPresent()){
					result.add(move.get());
				}
			}
		}

		// This has to be withdrawn...because it makes the whole 
		// algorithm non-deterministic.
//		// Finally we may have to move top spots back occasionally as a move
//		if (random(100) < 5){
//			for (Spot target : openDeckSlot){
//				for(Spot source : pileSpots){
//					Optional<Move> move = makeAndCheckValidMove(play, source, target);
//					if(move.isPresent()){
//						result.add(move.get());
//					}
//				}
//			}
//		}

		return result;
	}

	public static class FreeCellTest{
		private static final String deck0 =
				"/Kh Qc Kd Ks/Kc";

		private static final String deck1 =
				   "Kc 8s/Qh Jc Kd 2s"
				+ "/Kh Ks 7s"
				+ "/Qc Qs 6s"
				+ "/Js 5s"
				+ "/Ts 4s"
				+ "/9s 3s";

		private static final String deck3 = "//"
				+ "7s Kd Ks 5d 6s Qh Kc 9h"
				+ " 4h 7d Ac 2s 4c 5c Jd Qs"
				+ " Jh As 5h 9s Js 9d Ah Th"
				+ " Ts 3d Tc 2d Ad Td 8s 8d"
				+ " 3h 7h 6c 9c Jc 2c 3s 6d"
				+ " 5s Qc 8c 2h 7c 8h 6h Qd"
				+ " Kh 4d 3c 4s";

		private static final String deck4 = "//"
				+ "Jd 9s 8c Td 6c 5h 2s Ks "
				+ "2d 5s 3d 6h 7c Kh Qh 2h "
				+ "3s 6d Ac Ah 4s Qc Kc 4c "
				+ "Ad 8s 6s Kd 3c Jc Jh Th "
				+ "Qs Ts As 9h Js 5d 4h 8h "
				+ "Tc 7s 7h 9d 2c 5c 4d 9c "
				+ "8d 7d Qd 3h";

		private static final String deck5 = "//"
				+ "Qh 4c 5c 7s 6h Ts Ac Jc "
				+ "3h 8c Qd 7d 8h 8d Tc Qc "
				+ "2d 5d Kc 8s Qs 6c 4h 5s "
				+ "9c 5h Ad 6s 7c 3d 2s As "
				+ "Js Th Ah 2c 7h 3s Kd 9d "
				+ "Jh 6d 9h 2h Td 4s 4d 3c "
				+ "Jd Kh Ks 9s ";

		private static final String deck6 = "//"
				+ "2d 2h 8d Qd 2c Qs Ad 3h "
				+ "4s 3c Jh Qc 4d As 7s 2s "
				+ "8h 5h Jd Th 9h 5c 7c 6c "
				+ "8s Ts 6h Td Jc Tc Ac 4h "
				+ "3d 3s 5s 7d Kh 6s Kc Js "
				+ "Qh 9s Ks 8c Ah 6d Kd 5d "
				+ "7h 4c 9d 9c ";

		private static final String deck7 = "//"
				+ "4c Ts 2s 2h Th 2c Kc As "
				+ "5h 8s Td 3d 9h Ks Qs 4s "
				+ "7s 8h 8c 8d 9d Qh 4h 6h "
				+ "Jh 2d 6c 6s Qd 3s 4d Kh "
				+ "Jd 9s Tc 6d 5c Ac Js 7h "
				+ "7d 7c 3h 9c Ah Qc Jc 3c "
				+ "5s Kd 5d Ad ";

		private String randomDeck() {
			Set<Integer> ints = new LinkedHashSet<>();
			while(ints.size() < 52){
				int next = random(52);
				ints.add(next);
			}
			StringBuilder sb = new StringBuilder("//");
			for(Integer index : ints){
				Card card = new Card(Suit.from(index), Value.from(index));
				sb.append(card.toString());
				sb.append(" ");
			}
			return sb.toString().trim();
		}

		private String solveADeck(String name, String deck, long maxSolutionMoves, int imDepth, Long... tests){
			List<FreeCell> games = new ArrayList<>();
			for(long test : tests){
				FreeCell game = new FreeCell(deck, name + " : max=@" + test, maxSolutionMoves, test, imDepth);
				games.add(game);
				log(game.showCards());
				game.findSolution();
				if(game.completed()){
					break;
				}
			}
			StringBuilder result = new StringBuilder();
			for(FreeCell game : games){
				log(game.showCards());
				if(game.completed() && game.solution != null){
					game.showSolution();
				}
				result.append(log("Game {} ended after {}ms, {} attempted moves and solution {}\n",
						name, game.timeTaken, game.totalMoves, 
						game.solution == null ? "not found in max moves" : ("in " + game.solution.size() + " moves")));
			}
			return result.toString();
		}

//		@Test
		public void solveDeck0(){
			solveADeck("Trivial #0", deck0, 200L, 10, 100L);
		}

//		@Test
		public void solveDeck1(){
			solveADeck("Simple #1", deck1, 200L, 10, 100000L);
		}

//		@Test
		public void solveDeck2(){
			String deck2 = randomDeck();
			log(solveADeck("Random #2", deck2, 200L, 10, 10000000L));
		}

//		@Test
		public void solveDeck3(){
//			String d20 = solveADeck("C#3 d20", deck3, 20, 5000000L);
//			String d15 = solveADeck("C#3 d15", deck3, 15, 5000000L);
//			String d10 = solveADeck("C#3 d10", deck3, 10, 5000000L);
//			String d05 = solveADeck("C#3 d05", deck3, 5, 5000000L);
//			log("Final results:\nd05:{}\nd10:{}\nd15:{}", d05, d10, d15);
			log(solveADeck("Complex #3", deck3, 200L, 10, 5000000L));
		}

//		@Test
		public void solveDeck4(){
			log(solveADeck("Hard #4", deck4, 200L, 10, 10000000L));
		}

//		@Test
//		public void solveDeck5(){
//			log(solveADeck("Hard #5", deck5, 10, 10000000L));
//		}

//		@Test
//		public void solveDeck6(){
//			log(solveADeck("Hard #6", deck6, 300L, 10, 50000000L));
//		}

//		@Test
//		public void solveDeck7() {
//			log(solveADeck("Hard #7", deck7, 25L, 10, 50000L));
//		}

//		@Test
//		public void solveDeck71(){
//			log(solveADeck("Hard #7", deck7, 360L, 10, 50000000L));
//		}

		private void checkChain(int max, int expected, String[] cards){
			int id = 0;
			Column column = new Column(id);
			for(String card : cards){
				column.push(Card.makeCard(card));
			}
			int actual = FreeCell.findChain(column, max);
			Assert.assertTrue(actual == expected);
		}

		// @Test
		public void checkChains(){
			String[] test1 = { "7d", "Qc", "Jh", "Ts" };
			String[] test2 = { "Qc", "Jh", "Ts" };
			String[] test3 = { "7d", "Qc", "Jh", "Ts", "6c" };
			String[] test4 = {};
			checkChain(2, 2, test1);
			checkChain(4, 3, test1);
			checkChain(3, 3, test2);
			checkChain(4, 1, test3);
			checkChain(4, 0, test4);
		}

		public void testMultQue(){
			MultQue<Integer> mq = new MultQue<Integer>(5);
			for (int i = 0; i < 100; i++){
				mq.add(i);
			}
			Iterator<Integer> iter = mq.getIterator();
			int ix = 0;
			while(iter.hasNext()){
				System.out.println("Elt " + ix + " -> " + iter.next());
				ix++;
			}
		}

	}


}
