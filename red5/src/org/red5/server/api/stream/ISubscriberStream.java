package org.red5.server.api.stream;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright (c) 2006-2010 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

/**
 * ISubscriberStream is a stream from subscriber's point of view. That is, it
 * provides methods for common stream operations like play, pause or seek.
 */
public interface ISubscriberStream extends IClientStream {
	/**
	 * Start playing.
	 * 
	 * @throws IOException if an IO error occurred while starting to play the stream
	 */
	void play() throws IOException;

	/**
	 * Pause at a position for current playing item.
	 * 
	 * @param position
	 *            Position for pause in millisecond.
	 */
	void pause(int position);

	/**
	 * Resume from a position for current playing item.
	 * 
	 * @param position
	 *            Position for resume in millisecond.
	 */
	void resume(int position);

	/**
	 * Stop playing.
	 */
	void stop();

	/**
	 * Seek into a position for current playing item.
	 * 
	 * @param position
	 *            Position for seek in millisecond.
	 * @throws OperationNotSupportedException if the stream doesn't support seeking.
	 */
	void seek(int position) throws OperationNotSupportedException;

	/**
	 * Check if the stream is currently paused.
	 * 
	 * @return stream is paused
	 */
	boolean isPaused();

	/**
	 * Should the stream send video to the client?
	 * 
	 * @param receive
	 */
	void receiveVideo(boolean receive);

	/**
	 * Should the stream send audio to the client?
	 * 
	 * @param receive
	 */
	void receiveAudio(boolean receive);
	
	/**
	 * Return the streams state enum.
	 * 
	 * @return current state
	 */
	public StreamState getState();	
	
	/**
	 * Sets the streams state enum.
	 * 
	 * @param state sets current state
	 */
	public void setState(StreamState state);

	/**
	 * Notification of state change and associated parameters.
	 * 
	 * @param state new state
	 * @param changed parameters associated with the change
	 */
	public void onChange(final StreamState state, final Object... changed);

	/**
	 * Returns the Executor.
	 * @return executor
	 */
	ScheduledThreadPoolExecutor getExecutor();		
	
}
