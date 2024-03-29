package org.red5.server.api.service;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions to invoke methods on connections.
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Joachim Bauch (jojo@struktur.de)
 *
 */
public class ServiceUtils {

	private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);

	/**
	 * Invoke a method on the current connection.
	 * 
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 * @return <code>true</code> if the connection supports method calls,
	 *         otherwise <code>false</code>
	 */
	public static boolean invokeOnConnection(String method, Object[] params) {
		return invokeOnConnection(method, params, null);
	}

	/**
	 * Invoke a method on the current connection and handle result.
	 * 
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 * @param callback object to notify when result is received
	 * @return <code>true</code> if the connection supports method calls,
	 *         otherwise <code>false</code>
	 */
	public static boolean invokeOnConnection(String method, Object[] params, IPendingServiceCallback callback) {
		IConnection conn = Red5.getConnectionLocal();
		if (conn != null) {
			log.debug("Connection for invoke: {}", conn);
			return invokeOnConnection(conn, method, params, callback);
		} else {
			log.warn("Connection was null (thread local), cannot execute invoke request");
			return false;
		}
	}

	/**
	 * Invoke a method on a given connection.
	 * 
	 * @param conn connection to invoke method on
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 * @return <code>true</code> if the connection supports method calls,
	 *         otherwise <code>false</code>
	 */
	public static boolean invokeOnConnection(IConnection conn, String method, Object[] params) {
		return invokeOnConnection(conn, method, params, null);
	}

	/**
	 * Invoke a method on a given connection and handle result.
	 * 
	 * @param conn connection to invoke method on
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 * @param callback object to notify when result is received
	 * @return <code>true</code> if the connection supports method calls,
	 *         otherwise <code>false</code>
	 */
	public static boolean invokeOnConnection(IConnection conn, String method, Object[] params,
			IPendingServiceCallback callback) {
		if (conn instanceof IServiceCapableConnection) {
			if (callback == null) {
				((IServiceCapableConnection) conn).invoke(method, params);
			} else {
				((IServiceCapableConnection) conn).invoke(method, params, callback);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Invoke a method on all connections to the current scope.
	 * 
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 */
	public static void invokeOnAllConnections(String method, Object[] params) {
		invokeOnAllConnections(method, params, null);
	}

	/**
	 * Invoke a method on all connections to the current scope and handle
	 * result.
	 * 
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 * @param callback object to notify when result is received
	 */
	public static void invokeOnAllConnections(String method, Object[] params, IPendingServiceCallback callback) {
		IConnection conn = Red5.getConnectionLocal();
		if (conn != null) {
			log.debug("Connection for invoke on all: {}", conn);
			IScope scope = conn.getScope();
			log.debug("Scope for invoke on all: {}", scope);
			invokeOnAllConnections(scope, method, params, callback);
		} else {
			log.warn("Connection was null (thread local), scope cannot be located and cannot execute invoke request");
		}
	}

	/**
	 * Invoke a method on all connections to a given scope.
	 * 
	 * @param scope scope to get connections for
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 */
	public static void invokeOnAllConnections(IScope scope, String method, Object[] params) {
		invokeOnAllConnections(scope, method, params, null);
	}

	/**
	 * Invoke a method on all connections to a given scope and handle result.
	 * 
	 * @param scope scope to get connections for
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 * @param callback object to notify when result is received
	 */
	public static void invokeOnAllConnections(IScope scope, String method, Object[] params,
			IPendingServiceCallback callback) {
		invokeOnClient(null, scope, method, params, callback);
	}

	/**
	 * Invoke a method on all connections of a client to a given scope.
	 *  
	 * @param client client to get connections for
	 * @param scope scope to get connections of the client from
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 */
	public static void invokeOnClient(IClient client, IScope scope, String method, Object[] params) {
		invokeOnClient(client, scope, method, params, null);
	}

	/**
	 * Invoke a method on all connections of a client to a given scope and
	 * handle result.
	 * 
	 * @param client client to get connections for
	 * @param scope scope to get connections of the client from
	 * @param method name of the method to invoke
	 * @param params parameters to pass to the method
	 * @param callback object to notify when result is received
	 */
	public static void invokeOnClient(IClient client, IScope scope, String method, Object[] params,
			IPendingServiceCallback callback) {
		Set<IConnection> connections;
		if (client == null) {
			connections = new HashSet<IConnection>();
			Collection<Set<IConnection>> conns = scope.getConnections();
			for (Set<IConnection> set : conns) {
				connections.addAll(set);
			}
		} else {
			connections = scope.lookupConnections(client);
			if (connections == null) {
				// Client is not connected to the scope
				return;
			}
		}

		if (callback == null) {
			for (IConnection conn : connections) {
				invokeOnConnection(conn, method, params);
			}
		} else {
			for (IConnection conn : connections) {
				invokeOnConnection(conn, method, params, callback);
			}
		}

		if (connections != null) {
			connections.clear();
			connections = null;
		}
	}

	/**
	 * Notify a method on the current connection.
	 * 
	 * @param method name of the method to notify
	 * @param params parameters to pass to the method
	 * @return <code>true</code> if the connection supports method calls,
	 *         otherwise <code>false</code>
	 */
	public static boolean notifyOnConnection(String method, Object[] params) {
		IConnection conn = Red5.getConnectionLocal();
		if (conn != null) {
			log.debug("Connection for notify: {}", conn);
			return notifyOnConnection(conn, method, params);
		} else {
			log.warn("Connection was null (thread local), cannot execute notify request");
			return false;
		}
	}

	/**
	 * Notify a method on a given connection.
	 * 
	 * @param conn connection to notify method on
	 * @param method name of the method to notify
	 * @param params parameters to pass to the method
	 * @return <code>true</code> if the connection supports method calls,
	 *         otherwise <code>false</code>
	 */
	public static boolean notifyOnConnection(IConnection conn, String method, Object[] params) {
		if (conn instanceof IServiceCapableConnection) {
			((IServiceCapableConnection) conn).notify(method, params);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Notify a method on all connections to the current scope.
	 * 
	 * @param method name of the method to notify
	 * @param params parameters to pass to the method
	 */
	public static void notifyOnAllConnections(String method, Object[] params) {
		IConnection conn = Red5.getConnectionLocal();
		if (conn != null) {
			log.debug("Connection for notify on all: {}", conn);
			IScope scope = conn.getScope();
			log.debug("Scope for notify on all: {}", scope);
			notifyOnAllConnections(scope, method, params);
		} else {
			log.warn("Connection was null (thread local), scope cannot be located and cannot execute notify request");
		}
	}

	/**
	 * Notify a method on all connections to a given scope.
	 * 
	 * @param scope scope to get connections for
	 * @param method name of the method to notify
	 * @param params parameters to pass to the method
	 */
	public static void notifyOnAllConnections(IScope scope, String method, Object[] params) {
		notifyOnClient(null, scope, method, params);
	}

	/**
	 * Notify a method on all connections of a client to a given scope.
	 *  
	 * @param client client to get connections for
	 * @param scope scope to get connections of the client from
	 * @param method name of the method to notify
	 * @param params parameters to pass to the method
	 */
	@SuppressWarnings("unchecked")
	public static void notifyOnClient(IClient client, IScope scope, String method, Object[] params) {
		Set<IConnection> connections = Collections.EMPTY_SET;
		if (client == null) {
			connections = new HashSet<IConnection>();
			Collection<Set<IConnection>> conns = scope.getConnections();
			for (Set<IConnection> set : conns) {
				connections.addAll(set);
			}
		} else {
			connections = scope.lookupConnections(client);
		}

		for (IConnection conn : connections) {
			notifyOnConnection(conn, method, params);
		}

		if (connections != null) {
			connections.clear();
			connections = null;
		}

	}

}
