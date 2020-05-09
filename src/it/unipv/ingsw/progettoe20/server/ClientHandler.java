package it.unipv.ingsw.progettoe20.server;

import it.unipv.ingsw.progettoe20.server.database.DatabaseManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

// TODO: add comments, find better solution for switch statement, write something in error catches

/**
 * Il ClientHandler è l'unità che si occupa della connessione con il client.
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private DatabaseManager dbManager;
    private PrintWriter out;
    private boolean end = false;

    /**
     * Costruttore del ClientHandler.
     *
     * @param socket    socket utilizzata per la connessione.
     * @param dbManager reference al dbManager istanziato da ParkingManager.
     * @param name      nome del thread.
     * @throws IOException se ci sono problemi nel collegamento alla socket.
     */
    public ClientHandler(Socket socket, DatabaseManager dbManager, String name) throws IOException {
        super(name);
        this.socket = socket;
        this.dbManager = dbManager;
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Legge il contenuto della socket e verifica la presenza di richieste. Alla richiesta di chiusura termina la connessione.
     */
    public void run() {
        try {
            while (!end) {
                String request = listenSocket(socket.getInputStream());
                requestSorter(request);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        try {
            out.println("Connection closed by client");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Legge il contenuto della socket.
     *
     * @param input il collegamento in lettura alla socket.
     * @return richiesta letta.
     * @throws IOException se ci sono problemi nel collegamento alla socket.
     */
    private String listenSocket(InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;

        while (((c = input.read()) >= 0) && (c != 0x0a /* <LF> */)) {
            if (c != 0x0d /* <CR> */) {
                sb.append((char) c);
            } else {
                // Ignore <CR>.
            }
        }
        return sb.toString();
    }

    /**
     * Verifica la presenza di comandi validi nella richiesta ed esegue l'azione corrispondente. Per fare ciò divide comando e ID e verifica se il comando è valido.
     *
     * @param request richiesta effettuata dal client.
     * @throws SQLException se ci sono problemi nell'accesso al database.
     */
    private void requestSorter(String request) throws SQLException {    // please find a better name
        String[] parts = request.split(":");    // split request into 'command' and 'ID'

        switch (parts[0]) {
            case (Protocol.REQUEST_GENERATE_ID):
                // TODO
                break;
            case (Protocol.REQUEST_PAY_AMOUNT):
                // TODO
                break;
            case (Protocol.REQUEST_DELETE_ID):
                dbManager.removeRecord(parts[1]);
                break;
            case (Protocol.REQUEST_END):
                end = true;
                break;
            case "ping":
                out.println("pong");
                break;
        }
    }
}
