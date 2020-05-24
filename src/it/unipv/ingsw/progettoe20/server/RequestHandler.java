package it.unipv.ingsw.progettoe20.server;

import it.unipv.ingsw.progettoe20.Protocol;
import it.unipv.ingsw.progettoe20.server.database.DatabaseFacade;
import it.unipv.ingsw.progettoe20.server.model.Ticket;

import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * Verifica la presenza di comandi validi nella richiesta ed esegue l'azione corrispondente.
 */
public class RequestHandler {
    private DatabaseFacade dbManager;
    private PrintWriter out;
    private GenerationIdTicket generator;

    /**
     * Costruisce un RequestHandler.
     *
     * @param dbManager reference al DatabaseManager.
     * @param out       reference PrintWriter sulla socket associata.
     */
    public RequestHandler(DatabaseFacade dbManager, PrintWriter out) {
        this.dbManager = dbManager;
        this.out = out;
        this.generator = new GenerationIdTicket();

    }

    /**
     * Divide comando e ID, verifica se il comando è valido ed esegue l'azione corrispondente.
     *
     * @param request richiesta effettuata dal client.
     * @return true se il client ha richiesto la chiusura della connessione, false altrimenti.
     */
    public boolean handle(String request) throws IllegalArgumentException {
        String[] parts = splitRequest(request);
        //TODO: implement commander pattern?
        switch (parts[0]) {
            // New ID generation requested
            case (Protocol.REQUEST_GENERATE_ID):
                String id;
                do {
                    id = generator.GenerateId();
                } while (dbManager.checkTicketById(id));
                Ticket newTicket = new Ticket(id);
                dbManager.updateTicket(newTicket);
                out.println(Protocol.RESPONSE_OK + Protocol.SEPARATOR + id);
                break;
            // ID existence check requested
            case (Protocol.REQUEST_CHECK_ID):
                try {
                    dbManager.checkTicketById(parts[1]);
                    out.println(Protocol.RESPONSE_OK);
                } catch (IllegalArgumentException i) {
                    System.out.println(i.getMessage());
                    out.println(Protocol.RESPONSE_ERROR + Protocol.SEPARATOR + i.getMessage());
                }
                break;
            // Pay amount calculation requested
            case (Protocol.REQUEST_PAY_AMOUNT):
                // TODO
                break;
            // Record deletion requested
            case (Protocol.REQUEST_DELETE_ID):
                try {
                    dbManager.removeTicket(dbManager.getTicketById(parts[1]));
                    out.println(Protocol.RESPONSE_OK);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    out.println(Protocol.RESPONSE_ERROR + Protocol.SEPARATOR + e.getMessage());
                }
                break;
            // Correctly obliterated check
            case (Protocol.REQUEST_PAYMENT_CHECK):
                try {
                    if (dbManager.getTicketById(parts[1]).isPaid()) {
                        out.println(Protocol.RESPONSE_PAID_TRUE);
                    } else {
                        out.println(Protocol.RESPONSE_PAID_FALSE);
                    }
                } catch (IllegalArgumentException i) {
                    System.out.println(i.getMessage());
                    out.println(Protocol.RESPONSE_ERROR + Protocol.SEPARATOR + i.getMessage());
                }
                break;
            // Accept payment requested
            case (Protocol.REQUEST_PAYMENT_ACCEPTED):
                try {
                    Ticket ticket = dbManager.getTicketById(parts[1]);
                    ticket.setPaymentTime(new Timestamp(System.currentTimeMillis()));
                    ticket.setPaid(true);
                    dbManager.updateTicket(ticket);
                    out.println(Protocol.RESPONSE_OK);
                } catch (IllegalArgumentException i) {
                    System.out.println(i.getMessage());
                    out.println(Protocol.RESPONSE_ERROR + Protocol.SEPARATOR + i.getMessage());
                }
                break;
            // A friendly ping
            case "ping":
                out.println("pong");
                break;
            // Connection end requested
            case (Protocol.REQUEST_END):
                return true;
        }
        return false;
    }

    private String[] splitRequest(String request) {
        String[] out = new String[2];
        String[] parts = request.split(Protocol.SEPARATOR);    // split request into 'command' and 'ID'
        if (parts.length != 2) {
            if (parts.length == 1) {
                out[0] = parts[0];
                return out;
            } else {
                throw new IllegalArgumentException("Request is not protocol compliant!");
            }
        }
        return parts;
    }
}
