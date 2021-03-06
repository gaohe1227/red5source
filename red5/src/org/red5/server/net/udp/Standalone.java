package org.red5.server.net.udp;

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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class Standalone {

	public static final int PORT = 5150;

	public static void main(String[] args) throws Exception {
        IoAcceptor acceptor = new NioDatagramAcceptor();
        //set our handler
        acceptor.setHandler(new BasicHandler());
        //bind
		Set<SocketAddress> addresses = new HashSet<SocketAddress>();			
		addresses.add(new InetSocketAddress(PORT));	
		acceptor.bind(addresses);
		
        System.out.println("Listening on port " + PORT);
	}

}
