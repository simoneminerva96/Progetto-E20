package it.unipv.ingsw.progettoe20.server.database;

public class Queries {

// GENERAL DB QUERIES
    /**
     * Mostra una lista dei database presenti.
     */
    static final String LIST_DB = "SHOW DATABASES;";

    /**
     * Crea un database. Necessita di una stringa consecutiva che specifichi il nome del database.
     */
    static final String CREATE_DB = "CREATE DATABASE "; // with PreparedStatement don't work because of ''

    /**
     * Sposta la posizione in un database. Necessita di una stringa consecutiva che specifichi il nome del database.
     */
    static final String USE_DB = "USE "; // same

    /**
     * Crea la tabella dei ticket.
     */
    static final String CREATE_TABLE_TICKETS = "CREATE TABLE " + DBConstants.TICKET_TABLE + " ("
            + DBConstants.TICKET_FIRST_COLUMN + " varchar(" + DBConstants.TICKET_ID_LENGTH + ") not NULL, "
            + DBConstants.TICKET_SECOND_COLUMN + " DATETIME, " + DBConstants.TICKET_THIRD_COLUMN + " DATETIME, "
            + DBConstants.TICKET_FOURTH_COLUMN + " BOOLEAN, " + " PRIMARY KEY ( " + DBConstants.TICKET_FIRST_COLUMN + " ))";

    /**
     * Crea la tabella dei livelli.
     */
    static final String CREATE_TABLE_LEVELS = "CREATE TABLE " + DBConstants.LEVEL_TABLE + " ("
            + DBConstants.LEVEL_FIRST_COLUMN + " varchar(" + DBConstants.LEVEL_NAME_LENGTH + ") not NULL, "
            + DBConstants.LEVEL_SECOND_COLUMN + " INT not NULL, " + DBConstants.LEVEL_THIRD_COLUMN + " INT not NULL, "
            + " PRIMARY KEY ( " + DBConstants.LEVEL_FIRST_COLUMN + " ))";

// TICKET QUERIES
    /**
     * Crea un nuovo ticket. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String TICKET_NEW = "INSERT INTO " + DBConstants.TICKET_TABLE + " ("
            + DBConstants.TICKET_FIRST_COLUMN + ", " + DBConstants.TICKET_SECOND_COLUMN + ", "
            + DBConstants.TICKET_THIRD_COLUMN + ", " + DBConstants.TICKET_FOURTH_COLUMN + ") " + "VALUES (?, ?, ?, ?)";

    /**
     * Aggiorna un ticket. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String TICKET_UPDATE = "UPDATE " + DBConstants.TICKET_TABLE + " SET "
            + DBConstants.TICKET_SECOND_COLUMN + " = ?, " + DBConstants.TICKET_THIRD_COLUMN + " = ?, "
            + DBConstants.TICKET_FOURTH_COLUMN + " = ? " + "WHERE " + DBConstants.TICKET_FIRST_COLUMN + " = ?";

    /**
     * Rimuove un ticket. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String TICKET_REMOVE = "DELETE FROM " + DBConstants.TICKET_TABLE + " WHERE "
            + DBConstants.TICKET_FIRST_COLUMN + " = ?";

    /**
     * Restituisce il ticket scelto. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String TICKET_GET = "SELECT * FROM " + DBConstants.TICKET_TABLE + " WHERE "
            + DBConstants.TICKET_FIRST_COLUMN + " = ?";


// LEVEL QUERIES
    /**
     * Crea un nuovo livello. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String LEVEL_NEW = "INSERT INTO " + DBConstants.LEVEL_TABLE + " ("
            + DBConstants.LEVEL_FIRST_COLUMN + ", " + DBConstants.LEVEL_SECOND_COLUMN + ", "
            + DBConstants.LEVEL_THIRD_COLUMN + ") " + "VALUES (?, ?, ?)";

    /**
     * Aggiorna un livello. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String LEVEL_UPDATE = "UPDATE " + DBConstants.LEVEL_TABLE + " SET "
            + DBConstants.LEVEL_SECOND_COLUMN + " = ?, " + DBConstants.LEVEL_THIRD_COLUMN + " = ? "
            + "WHERE " + DBConstants.LEVEL_FIRST_COLUMN + " = ?";

    /**
     * Rimuove un livello. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String LEVEL_REMOVE = "DELETE FROM " + DBConstants.LEVEL_TABLE + " WHERE "
            + DBConstants.LEVEL_FIRST_COLUMN + " = ?";

    /**
     * Restituisce il livello scelto. Necessita dei parametri presenti nella query, nello stesso ordine.
     */
    static final String LEVEL_GET = "SELECT * FROM " + DBConstants.LEVEL_TABLE + " " + "WHERE "
            + DBConstants.LEVEL_FIRST_COLUMN + " = ?";
}

