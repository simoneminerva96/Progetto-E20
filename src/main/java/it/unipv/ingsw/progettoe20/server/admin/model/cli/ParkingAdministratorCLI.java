package it.unipv.ingsw.progettoe20.server.admin.model.cli;

import java.util.Scanner;

public class ParkingAdministratorCLI extends AbstractAdministratorCLI {

	private String insertText;
	private Scanner scanner;

	public ParkingAdministratorCLI(Scanner scanner, String insertText) {
		super(scanner, insertText);
	}

	@Override
	protected void handlerAdministratorCLI(Scanner scanner, String insertText2) {
		// TODO Auto-generated method stub

	}
}
