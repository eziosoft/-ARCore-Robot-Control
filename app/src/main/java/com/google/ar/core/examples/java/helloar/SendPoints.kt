package com.google.ar.core.examples.java.helloar

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 22/03/2019.
 */


class SendPoints() {

    interface ConnectionStatus {
        fun OnConnectionStatusChanged(connected: Boolean?)
    }

    lateinit var connectionStatus: ConnectionStatus

    lateinit var dout: DataOutputStream
    var socket: Socket? = null
    lateinit var IP: String
    var port: Int = 65432

    fun connect(IP: String, port: Int, connectionStatus: ConnectionStatus) {
        this.connectionStatus = connectionStatus
        this.port = port
        this.IP = IP
        GlobalScope.launch {
            try {
                socket = Socket(InetAddress.getByName(IP), port)

                if (socket != null) {
                    dout = DataOutputStream(socket?.getOutputStream())
                    connectionStatus.OnConnectionStatusChanged(socket?.isConnected)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun write(bytes: ByteArray) {
        GlobalScope.launch {
            if (socket != null) {
                if (socket!!.isConnected) {
                    try {
                        dout.write(bytes)
                    } catch (e: Exception) {
                    }
                } else {
                    socket = Socket(InetAddress.getByName(IP), port)
                    dout = DataOutputStream(socket?.getOutputStream())
                }
            }
        }

    }

    fun close() {
        GlobalScope.launch {
            if (socket != null) {
                if (socket?.isConnected!!) dout.close()
                connectionStatus.OnConnectionStatusChanged(socket?.isConnected)
            }

        }
    }

}