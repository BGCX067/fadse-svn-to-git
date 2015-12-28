/*
 * This file is part of the FADSE tool.
 * 
 *   Authors: Horia Andrei Calborean {horia.calborean at ulbsibiu.ro}, Andrei Zorila
 *   Copyright (c) 2009-2010
 *   All rights reserved.
 * 
 *   Redistribution and use in source and binary forms, with or without modification,
 *   are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 * 
 *   The names of its contributors NOT may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 * 
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *   ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *   OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Horia Calborean
 */
public class FadseConnector {

    private static FadseConnector instance;
    private Socket socket;
    ObjectInputStream dis;
    ObjectOutputStream dos;

    private FadseConnector(int port, InetAddress ip) {
        connect(ip, port);
    }

    public static FadseConnector getInstance(int port, InetAddress ip) {
        if (instance == null) {
            instance = new FadseConnector(port,ip);
        }
        return instance;
    }

    private boolean connect(InetAddress address, int port) {
        System.out.println("Trying to connect ...");
        try {
            System.out.println("address: "+ address);
            System.out.println("port: "+ port);
            socket = new Socket(address, port);
            dis = new ObjectInputStream(socket.getInputStream());
            dos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connected succesfully");
        } catch (IOException ex) {
            //Logger.getLogger(FadseConnector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to connect");
            return false;
        }
        return true;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public Object getNumberOfActiveSimulations() {
        Object response = null;
        try {
            System.out.println("Reading object");
            response = dis.readObject();
            System.out.println("Object read");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FadseConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FadseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Hello :-)");
            FadseConnector fc = new FadseConnector(4448, Inet4Address.getByName("localhost"));
            System.out.println("one");
            Object no = fc.getNumberOfActiveSimulations();
            System.out.println(no);
            System.out.println("two");
            //fc.connect(Inet4Address.getByName("localhost"), 4448);
        } catch (UnknownHostException ex) {
            Logger.getLogger(FadseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
