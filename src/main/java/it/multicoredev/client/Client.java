package it.multicoredev.client;

import it.multicoredev.mclib.network.client.ClientSocket;
import it.multicoredev.mclib.network.client.ServerAddress;
import it.multicoredev.mclib.network.protocol.PacketRegistry;
import it.multicoredev.protocol.C2SGamePacket;
import it.multicoredev.protocol.C2SMessagePacket;
import it.multicoredev.protocol.Game;

/**
 * BSD 3-Clause License
 * <p>
 * Copyright (c) 2022, Lorenzo Magni
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Client {
    private final ClientSocket clientSocket;

    private Client() {
        PacketRegistry.getInstance().registerPacket(C2SMessagePacket.class);
        PacketRegistry.getInstance().registerPacket(C2SGamePacket.class);

        clientSocket = new ClientSocket(new ServerAddress("localhost", 12987), new ClientNetworkHandler(new ClientPacketListener()));
        new Thread(() -> {
            try {
                clientSocket.connect();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        for (int i = 0; i < 500; i++) {
            try {
                System.out.println("Sending message " + i);
                //clientSocket.sendPacket(new C2SMessagePacket("Client", "Hello world! " + i));
                clientSocket.sendPacket(new C2SGamePacket(new Game("Game " + i, "Game description " + i, "1.0." + i, "LoreSchaeffer", "https://multicore.network/game" + i)));
            } catch (Throwable t) {
                i--;
                System.out.println(t.getMessage());
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
