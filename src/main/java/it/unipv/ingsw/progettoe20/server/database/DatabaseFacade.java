package it.unipv.ingsw.progettoe20.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import it.unipv.ingsw.progettoe20.server.database.dao.LevelDao;
import it.unipv.ingsw.progettoe20.server.database.dao.PriceDao;
import it.unipv.ingsw.progettoe20.server.database.dao.TicketDao;
import org.apache.commons.dbcp2.BasicDataSource;

/*  Resources
JDBC: https://www.tutorialspoint.com/jdbc/
Database Connection Pooling: https://devcenter.heroku.com/articles/database-connection-pooling-with-java
BasicDataSource Doc: https://commons.apache.org/proper/commons-dbcp/api-1.2.2/org/apache/commons/dbcp/BasicDataSource.html
Statement execute/executeQuery/executeUpdate: https://www.edureka.co/community/12548/java-execute-vs-executequery-vs-executeupdate
PreparedStatement usage: https://www.javatpoint.com/PreparedStatement-interface
 */

import it.unipv.ingsw.progettoe20.ErrorStrings;
import it.unipv.ingsw.progettoe20.server.Logger;
import it.unipv.ingsw.progettoe20.server.model.Level;
import it.unipv.ingsw.progettoe20.server.model.Price;
import it.unipv.ingsw.progettoe20.server.model.Ticket;

/**
 * Gestisce la connessione al database.
 */
public class DatabaseFacade {
    private static DatabaseFacade dbFacade = null;
    private BasicDataSource connectionPool;
    private TicketDao ticketDao;
    private LevelDao levelDao;
    private PriceDao priceDao;

    /**
     * Costruisce un nuovo DatabaseFacade. Chiede la password del database, la
     * controlla e inizializza la connessione. Il pool di connessioni viene
     * inizializzato ad una connessione.
     */
    public DatabaseFacade() {
        passwordInit();

        String dbUrl = DBConstants.DB_URL;
        connectionPool = new BasicDataSource();
        connectionPool.setUsername(DBConstants.USER);
        connectionPool.setPassword(DBConstants.PASS);
        connectionPool.setDriverClassName(DBConstants.JDBC_DRIVER);
        connectionPool.setUrl(dbUrl);
        connectionPool.setMaxTotal(DBConstants.MAX_CONNECTIONS);
        System.out.print("Connecting...");
        connectionPool.setInitialSize(1);
        ticketDao = new TicketDao(connectionPool);
        levelDao = new LevelDao(connectionPool);
        priceDao = new PriceDao(connectionPool);

        System.out.println("done");
    }

    public static DatabaseFacade getInstance() {
        if (dbFacade == null) {
            dbFacade = new DatabaseFacade();
        }
        return dbFacade;
    }

    /**
     * Chiede e controlla la password inserita finché non è corretta.
     */
    private void passwordInit() {
        String password;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.print("Enter database password for user " + DBConstants.USER + ": ");
            password = scanner.nextLine();
        } while (!checkPassword(password));
    }

    /**
     * Controlla che la password del database sia corretta. Per fare ciò tenta una
     * connessione al database.
     *
     * @param password La password da controllare.
     * @return true se la password è corretta, false altrimenti.
     */
    private boolean checkPassword(String password) {
        try {
            Class.forName(DBConstants.JDBC_DRIVER);
            DriverManager.getConnection(DBConstants.DB_URL, DBConstants.USER, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException sqle) {
            System.out.println("Password is incorrect, please try again");
            return false;
        }
        DBConstants.PASS = password;
        return true;
    }

    /**
     * Inizializza il database, se non presente. Controlla la presenza del database,
     * se assente crea il database e le table.
     */
    public void initDatabase() {
        Connection connection;
        ArrayList<String> dbList;

        try {
            connection = connectionPool.getConnection();
            dbList = getTablesList();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        // Checks if database already exist
        try {
            Statement stmt = connection.createStatement();

            if (!dbList.contains(DBConstants.TICKET_TABLE)) {
                // Create table for tickets
                System.out.print("Creating table for tickets \"" + DBConstants.TICKET_TABLE + "\"...");
                stmt.executeUpdate(Queries.CREATE_TABLE_TICKETS);
                System.out.println("done");
            }

            if (!dbList.contains(DBConstants.LEVEL_TABLE)) {
                // Create table for levels
                System.out.print("Creating table for levels \"" + DBConstants.LEVEL_TABLE + "\"...");
                stmt.executeUpdate(Queries.CREATE_TABLE_LEVELS);
                System.out.println("done");
            }

            if (!dbList.contains(DBConstants.PRICES_TABLE)) {
                // Create table for prices
                System.out.print("Creating table for prices \"" + DBConstants.PRICES_TABLE + "\"...");
                stmt.executeUpdate(Queries.CREATE_TABLE_PRICES);
                System.out.println("done");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restituisce un ArrayList di stringhe con i nomi delle table presenti.
     *
     * @return un ArrayList di stringhe con i nomi delle table presenti.
     * @throws SQLException se ci sono problemi nell'accesso al database.
     */
    private ArrayList<String> getTablesList() throws SQLException {
        ArrayList<String> response = new ArrayList<>();
        Connection connection = connectionPool.getConnection();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(Queries.LIST_DB_TABLES);
        while (rs.next()) {
            response.add(rs.getString(1)); // 1 = first column
        }
        return response;
    }

    /**
     * Modifica un record sul database, se non è presente, lo crea.
     *
     * @param ticket oggetto Ticket da salvare nel DB.
     */
    public void updateTicket(Ticket ticket) {
        ticketDao.update(ticket);
    }

    /**
     * Controlla che un ticket sia presente nella table. Se non è presente lancia
     * un'eccezione.
     *
     * @param id identificatore del record.
     * @return booleano di verifica.
     */
    public boolean checkTicketById(String id) {
        return ticketDao.checkTicketById(id);
    }

    /**
     * Rimuove il ticket passato come argomento dal database.
     *
     * @param ticket oggetto Ticket da rimuovere dal DB.
     * @throws IllegalArgumentException se il ticket non è presente nel DB.
     */
    public void removeTicket(Ticket ticket) throws IllegalArgumentException {
        ticketDao.delete(ticket);
    }

    /**
     * Restituisce un Ticket prelevato dal database, selezionato mediante ID.
     *
     * @param id identificatore del Ticket.
     * @return ticket richiesto.
     */
    public Ticket getTicketById(String id) {
        return ticketDao.get(id);
    }

    /**
     * Modifica un record sul database, se non è presente, lo crea.
     *
     * @param level oggetto Level da salvare nel DB.
     */
    public void updateLevel(Level level) {
        levelDao.update(level);
    }

    /**
     * Rimuove il livello passato come argomento dal database.
     *
     * @param level oggetto Level da rimuovere dal DB.
     * @throws IllegalArgumentException se il livello non è presente nel DB.
     */
    public void removeLevel(Level level) throws IllegalArgumentException {
        levelDao.delete(level);
    }

    /**
     * Restituisce un Level prelevato dal database, selezionato mediante nome.
     *
     * @param name identificatore del livello.
     * @return Level richiesto.
     */
    public Level getLevelByName(String name) {
        return levelDao.get(name);
    }

    /**
     * Restituisce la lista di Level presenti nel database.
     *
     * @return LevelList.
     */
    public List<Level> getLevelList() {
        return levelDao.getAll();
    }

    /**
     * Modifica un record sul database, se non è presente, lo crea.
     *
     * @param price oggetto Price da salvare nel DB.
     */
    public void updatePrice(Price price) {
        priceDao.update(price);
    }

    /**
     * Rimuove la tariffa passata come argomento dal database.
     *
     * @param price oggetto Price da rimuovere dal DB.
     * @throws IllegalArgumentException se il livello non è presente nel DB.
     */
    public void removePrice(Price price) throws IllegalArgumentException {
        priceDao.delete(price);
    }

    /**
     * Restituisce un Price prelevato dal database, selezionato mediante minutaggio.
     *
     * @param minutes identificatore della tariffa.
     * @return Level richiesto.
     */
    public Price getPricelByMinutes(int minutes) {
        return priceDao.get(String.valueOf(minutes));
    }

    /**
     * Restituisce la lista di Price presenti nel database.
     *
     * @return LevelList.
     */
    public List<Price> getPriceList() {
        return priceDao.getAll();
    }
}
