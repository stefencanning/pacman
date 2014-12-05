package pacman.entries.ghosts;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	static Game gameStat;
	static GHOST nextGhost;
	static String stateStat;
	boolean loaded = false;
	
	Random rnd=new Random();
	EnumMap<GHOST,MOVE> myMoves=new EnumMap<GHOST,MOVE>(GHOST.class);
	
	
	FSM machine = new FSM();
	String curState[]={"0","0","0","0"};
	MOVE[] moves=MOVE.values();
	MOVE last = MOVE.LEFT;
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
	{
		if(!loaded)
		{
			machine.load();
			loaded=true;
		}
		myMoves.clear();
		gameStat=game;

		if(game.doesGhostRequireAction(GHOST.values()[0]))
		{
			myMoves.put(GHOST.values()[0],game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.values()[0]),
					game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(GHOST.values()[0]),DM.PATH));
		}
		
		for(int i = 1; i < GHOST.values().length;i++)
		{
			nextGhost=GHOST.values()[i];
			stateStat=curState[i];
			machine.checks.get(machine.stateCheck.get(stateStat)).run();
			machine.actions.get(machine.stateAction.get(stateStat)).run();
			curState[i]=stateStat;
		}
		
		
		
		
		
		
		
/*
		curState=machine.updateState(curState, myMoves, game);

		machine.doStuff(curState, myMoves, game);
		*/
		
		
		
		
		
		//Place your game logic here to play the game as the ghosts
		
		return myMoves;
	}
	
	
	private class RunMine implements Runnable{	
		public String first="";
		public String second="";
		public String third="";
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	private class FSM
	{
		HashMap<String, String> states = new HashMap<String, String>()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
			}
		};
		
		HashMap<String, Runnable> actions = new HashMap<String, Runnable>()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
			}
		};
		
		HashMap<String, RunMine> checks = new HashMap<String, RunMine>()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
			}
		};
		
		HashMap<String, String> stateAction = new HashMap<String, String>()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
			}
		};
		
		HashMap<String, String> stateCheck = new HashMap<String, String>()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
			}
		};

		
		
		public void load()
		{

			states.put("attack", "0");
			states.put("hover", "1");
			states.put("run", "2");
			states.put("edible", "3");
			
			actions.put("0", new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					myMoves.put(nextGhost,gameStat.getApproximateNextMoveTowardsTarget(gameStat.getGhostCurrentNodeIndex(nextGhost),
							gameStat.getPacmanCurrentNodeIndex(),gameStat.getGhostLastMoveMade(nextGhost),DM.PATH));
				}
			});
			actions.put("1", new Runnable() {
				public void run() {
					int distCheck = gameStat.getShortestPathDistance(gameStat.getPacmanCurrentNodeIndex(), gameStat.getGhostCurrentNodeIndex(nextGhost));
					if(distCheck<25)
					{
						myMoves.put(nextGhost,gameStat.getApproximateNextMoveAwayFromTarget(gameStat.getGhostCurrentNodeIndex(nextGhost),
								gameStat.getPacmanCurrentNodeIndex(),gameStat.getGhostLastMoveMade(nextGhost),DM.PATH));
					}
					if(distCheck>50)
					{
						myMoves.put(nextGhost,gameStat.getApproximateNextMoveTowardsTarget(gameStat.getGhostCurrentNodeIndex(nextGhost),
								gameStat.getPacmanCurrentNodeIndex(),gameStat.getGhostLastMoveMade(nextGhost),DM.PATH));
					}
				}
			});
			actions.put("2", new Runnable() {
				public void run() {
					myMoves.put(nextGhost,gameStat.getApproximateNextMoveAwayFromTarget(gameStat.getGhostCurrentNodeIndex(nextGhost),
							gameStat.getPacmanCurrentNodeIndex(),gameStat.getGhostLastMoveMade(nextGhost),DM.PATH));
				}
			});
			actions.put("3", new Runnable() {
				public void run() {
					myMoves.put(nextGhost,gameStat.getApproximateNextMoveAwayFromTarget(gameStat.getGhostCurrentNodeIndex(nextGhost),
							gameStat.getPacmanCurrentNodeIndex(),gameStat.getGhostLastMoveMade(nextGhost),DM.PATH));
				}
			});
			
			checks.put("0", new RunMine() {
				@Override
				public void run() {
					int dist=0;
					for(int j =0; j < gameStat.getNumberOfActivePowerPills(); j++)
					{
						int distCheck = gameStat.getShortestPathDistance(gameStat.getPacmanCurrentNodeIndex(), gameStat.getActivePowerPillsIndices()[j]);
						if(dist==0)
						{
							dist = distCheck;
						}
						if(distCheck<dist)
						{
							dist=distCheck;
						}
					}
					if(dist!=0&&dist<30)
					{
						stateStat=states.get(first);
					}
					if(gameStat.getGhostEdibleTime(nextGhost)>0)
					{
						stateStat=states.get(second);
					}
				}
			});
			checks.put("1", new RunMine() {
				@Override
				public void run() {
					int dist=0;
					for(int j =0;j < gameStat.getNumberOfActivePowerPills();j++)
					{
						int distCheck = gameStat.getShortestPathDistance(gameStat.getPacmanCurrentNodeIndex(), gameStat.getActivePowerPillsIndices()[j]);
						if(dist==0)
						{
							dist = distCheck;
						}
						if(distCheck<dist)
						{
							dist=distCheck;
						}
					}
					if(dist!=0&&dist<15)
					{
						stateStat=states.get(first);
					}
					if(dist!=0&&dist>30)
					{
						stateStat=states.get(second);
					}
					if(gameStat.getGhostEdibleTime(nextGhost)>0)
					{
						stateStat=states.get(third);
					}
				}
			});
			checks.put("2", new RunMine() {
				@Override
				public void run() {
					int dist=0;
					for(int j =0; j < gameStat.getNumberOfActivePowerPills();j++)
					{
						int distCheck = gameStat.getShortestPathDistance(gameStat.getPacmanCurrentNodeIndex(), gameStat.getActivePowerPillsIndices()[j]);
						if(dist==0)
						{
							dist = distCheck;
						}
						if(distCheck<dist)
						{
							dist=distCheck;
						}
					}
					if(dist!=0&&dist>15)
					{
						stateStat=states.get(first);
					}
					if(gameStat.getGhostEdibleTime(nextGhost)>0)
					{
						stateStat=states.get(second);
					}
				}
			});
			checks.put("3", new RunMine() {
				@Override
				public void run() {
					if(gameStat.getGhostEdibleTime(nextGhost)<=0)
					{
						stateStat=states.get(first);
					}
				}
			});
			

		    try {
		 
			File file = new File("data/fsm.xml");
		 
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
		                             .newDocumentBuilder();
		 
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("state");
		 
			System.out.println("----------------------------");
		 
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
		 
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
					String state = eElement.getElementsByTagName("statename").item(0).getTextContent();
					System.out.println("statename :" + state);
					String check = eElement.getElementsByTagName("check").item(0).getTextContent();
					System.out.println("check :" + check);
					String action = eElement.getElementsByTagName("action").item(0).getTextContent();
					System.out.println("action :" + action);
					String first = eElement.getElementsByTagName("first").item(0).getTextContent();
					System.out.println("action :" + action);
					String second = eElement.getElementsByTagName("second").item(0).getTextContent();
					System.out.println("action :" + action);
					String third = eElement.getElementsByTagName("third").item(0).getTextContent();
					System.out.println("action :" + action);
					
					stateCheck.put(states.get(state),check);
					stateAction.put(states.get(state),action);
					checks.get(check).first=first;
					checks.get(check).second=second;
					checks.get(check).third=third;
		 
				}
			}
		 
		    } catch (Exception e) {
			System.out.println(e.getMessage());
		    }
		}
		
		/*
		public void doStuff(int[] state, EnumMap<GHOST,MOVE> myMoves,Game game )
		{
			if(game.doesGhostRequireAction(GHOST.values()[0]))
			{
				myMoves.put(GHOST.values()[0],game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.values()[0]),
						game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(GHOST.values()[0]),DM.PATH));
			}
			for(int i = 1; i < GHOST.values().length;i++)
			{
				if(state[i]==states.get("attack"))
				{
					if(game.doesGhostRequireAction(GHOST.values()[i]))
					{
						myMoves.put(GHOST.values()[i],game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.values()[i]),
								game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(GHOST.values()[i]),DM.PATH));
					}
				}
				if(state[i]==states.get("hover"))
				{
					int distCheck = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.values()[i]));
					if(distCheck<25)
					{
						if(game.doesGhostRequireAction(GHOST.values()[i]))
						{
							myMoves.put(GHOST.values()[i],game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(GHOST.values()[i]),
									game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(GHOST.values()[i]),DM.PATH));
						}
					}
					if(distCheck>50)
					{
						if(game.doesGhostRequireAction(GHOST.values()[i]))
						{
							myMoves.put(GHOST.values()[i],game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.values()[i]),
									game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(GHOST.values()[i]),DM.PATH));
						}
					}
				}
				if(state[i]==states.get("run"))
				{
					if(game.doesGhostRequireAction(GHOST.values()[i]))
					{
						myMoves.put(GHOST.values()[i],game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(GHOST.values()[i]),
								game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(GHOST.values()[i]),DM.PATH));
					}
				}
				if(state[i]==states.get("edible"))
				{
					if(game.doesGhostRequireAction(GHOST.values()[i]))
					{
						myMoves.put(GHOST.values()[i],game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(GHOST.values()[i]),
							game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(GHOST.values()[i]),DM.PATH));
					}
				}
			}
		}
		
		public int[] updateState(int[] state, EnumMap<GHOST,MOVE> myMoves,Game game)
		{
			for(int i = 0; i < GHOST.values().length;i++)
			{
				if(state[i]==states.get("attack"))
				{
					int dist=0;
					for(int j =0; j < game.getNumberOfActivePowerPills(); j++)
					{
						int distCheck = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getActivePowerPillsIndices()[j]);
						if(dist==0)
						{
							dist = distCheck;
						}
						if(distCheck<dist)
						{
							dist=distCheck;
						}
					}
					if(dist!=0&&dist<30)
					{
						state[i]=states.get("hover");
					}
					if(game.getGhostEdibleTime(GHOST.values()[i])>0)
					{
						state[i]=states.get("edible");
					}
				}
				else if(state[i]==states.get("hover"))
				{
					int dist=0;
					for(int j =0;j < game.getNumberOfActivePowerPills();j++)
					{
						int distCheck = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getActivePowerPillsIndices()[j]);
						if(dist==0)
						{
							dist = distCheck;
						}
						if(distCheck<dist)
						{
							dist=distCheck;
						}
					}
					if(dist!=0&&dist<15)
					{
						state[i]=states.get("run");
					}
					if(dist!=0&&dist>30)
					{
						state[i]=states.get("attack");
					}
					if(game.getGhostEdibleTime(GHOST.values()[i])>0)
					{
						state[i]=states.get("edible");
					}
				}
				else if(state[i]==states.get("run"))
				{
					int dist=0;
					for(int j =0; j < game.getNumberOfActivePowerPills();j++)
					{
						int distCheck = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getActivePowerPillsIndices()[j]);
						if(dist==0)
						{
							dist = distCheck;
						}
						if(distCheck<dist)
						{
							dist=distCheck;
						}
					}
					if(dist!=0&&dist>15)
					{
						state[i]=states.get("hover");
					}
					if(game.getGhostEdibleTime(GHOST.values()[i])>0)
					{
						state[i]=states.get("edible");
					}
				}
				else if(state[i]==states.get("edible"))
				{
					if(game.getGhostEdibleTime(GHOST.values()[i])<=0)
					{
						state[i]=states.get("attack");
					}
				}
			}
			return state;
		}
	
		*/
	}
}