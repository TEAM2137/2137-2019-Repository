package org.torc.robot2019.functions;

import java.util.ArrayList;

import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.MainRunTime;


/**
 * A class which allows for a list of commands and optional 
 * timeouts for them to be dynamically added to, and run from.
 * NOTE: Requires InheritedPeriodic to be implemented properly.
 */
public class CommandList implements InheritedPeriodic {
	
	private static ArrayList<CommandList> CommandLists = new ArrayList<CommandList>();
	
	private ArrayList<CommandListEntry> comList = new ArrayList<CommandListEntry>();
	
	private int iPos = 0;
	
	private boolean started = false;
	
	public CommandList() {
		MainRunTime.AddToPeriodic(this);
		CommandLists.add(this);
	}
	
	public static void StopAllCommandLists() {
		for (CommandList cList : CommandLists) {
			if (cList.started) {
				cList.end();
			}
		}
	}
	
	public void start() {
		if (started) {
			System.out.println("CommandList already started!!");
			return;
		}
		started = true;
		comList.get(iPos).command.start();
	}	
	
	public void end() {
		if (comList.size() >= 1) {
			comList.get(iPos).command.cancel();
		}		
		iPos = 0;
		started = false;
		endOfList = false;
	}
	
	public void addSequential(CLCommand command) {
		comList.add(new CommandListEntry(command, true));
	}
	
	public void addParallel(CLCommand command) {
		comList.add(new CommandListEntry(command, false));
	}
	
	public int getListLength() {
		return comList.size();
	}
	
	public boolean getStarted() {
		return started;
	}
	
	private boolean endOfList = false;
	
	@Override
	public void Periodic() {
		if (started) {
			while ( !endOfList && !comList.get(iPos).isSequential ) {
				comList.get(iPos).command.start();
				iPos++;
				
				if (iPos > comList.size() - 1) {
					endOfList = true;
				}
			}
			
			if (!endOfList && comList.get(iPos).isSequential) {
				if (!comList.get(iPos).command.isFinished() && !comList.get(iPos).command.isRunning()) {
					comList.get(iPos).command.start();
				}
				if (comList.get(iPos).command.isFinished()) {
					iPos++;
					
					if (iPos > comList.size() - 1) {
						endOfList = true;
					}
				}
			}
			
			if (endOfList) {
				iPos = 0;
				started = false;
				endOfList = false;
				System.out.println("Stopped Commandlist!");
				return;
			}
		}
	}
}

class CommandListEntry {
	CLCommand command;
	boolean isSequential;
	
	CommandListEntry(CLCommand comm, boolean sequential) {
		command = comm;
		isSequential = sequential;
	}
	
}