package it.unipv.ingsw.progettoe20.client.ObliterationColumn.oblModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Questa classe Rappresenta la colonna di pagamento del parcheggio
 * Si occupa quindi di confermare che la transazione, identificata dal id del ticket
 * é andata a buon fine
 */
public class ObliterationColumn  {

    private Socket socket;
    private BufferedReader in;
    private  PrintWriter out;

    /**
     * metodo che inizializza la classe Obliteration column come client
     */
    public ObliterationColumn()  {
        try {
            socket = new Socket("localhost", 9000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch(IOException i){
            System.out.println("rip");
        }
    }


    /**
     * metodo che cerca l'id nel database
     * @param id
     * @return true se l'id é presente nel database, false se invece non viene trovato
     */
    public boolean checkId(String id){
        try {
            System.out.println("id:"+id);
            out.println("id:"+ id);//testing
            String answer = in.readLine();
            System.out.println(answer);//testing
            if (answer.equals("done")) {
                socket.close();
                return true;
            } else return false;
        }
        catch (IOException i){
            return false;
        }
    }

    /**
     *Metodo che conferma il pagamento
     * @param id
     * @return
     */
    public static boolean pay(String id){

        return true;
    }
}
