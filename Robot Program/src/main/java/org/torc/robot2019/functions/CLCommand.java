package org.torc.robot2019.functions;

import edu.wpi.first.wpilibj.command.Command;

public class CLCommand extends Command {
	
	/**
	 * Used to tell the command that is running if it is finshed.
	 * This is used instead of personally implementing the orginal isFinished()
	 * method.
	 */
	protected boolean CLCommandDone = false;
	
	@Override
	public final boolean isFinished() {
		return CLCommandDone;
	}
	
	
}
