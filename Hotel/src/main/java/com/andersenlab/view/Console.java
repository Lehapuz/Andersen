package com.andersenlab.view;

import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.Client;
import com.andersenlab.entity.Perk;
import com.andersenlab.exceptions.*;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.service.ApartmentService;
import com.andersenlab.service.ClientService;
import com.andersenlab.service.PerkService;

import java.util.List;
import java.util.Scanner;

public class Console {

    private final Scanner scanner = new Scanner(System.in);
    private final ClientService clientService;
    private final ApartmentService apartmentService;
    private final PerkService perkService;

    public Console(HotelFactory hotelFactory) {
        clientService = hotelFactory.getClientService();
        apartmentService = hotelFactory.getApartmentService();
        perkService = hotelFactory.getPerkService();
    }

    public void start() {

        System.out.println("Hotel Administrator Alpha v0.1");
        System.out.println("Print 'help' for the list of commands");

        while (true) {
            try {
                String command = scanner.nextLine().trim();
                String[] commandArray = command.split("\s+");

                if (commandArray.length < 1) {
                    throw new UnknownCommandException(command);
                } else {
                    commandArray[0] = commandArray[0].toLowerCase();
                }

                switch (commandArray[0]) {
                    case "exit" -> {
                        ConsolePrinter.exit();
                        return;
                    }
                    case "help" -> ConsolePrinter.commands();
                    case "client" -> executeCommand(commandArray, CommandType.CLIENT);
                    case "apartment" -> executeCommand(commandArray, CommandType.APARTMENT);
                    case "perk" -> executeCommand(commandArray, CommandType.PERK);
                    default -> throw new UnknownCommandException(commandArray[0]);
                }
            } catch (CommandSyntaxException e) {
                ConsolePrinter.printSyntaxError();
            } catch (HotelException e) {
                ConsolePrinter.printCustomError(e.getMessage());
            } catch (NumberFormatException e) {
                ConsolePrinter.printInvalidArgument(e.getMessage());
            }
        }
    }

    private void executeCommand(String[] commandArray, CommandType type) {
        if (commandArray.length < 2) {
            throw new CommandSyntaxException();
        } else {
            commandArray[1] = commandArray[1].toLowerCase();
        }

        switch (type) {
            case CLIENT -> clientCommand(commandArray);
            case APARTMENT -> apartmentCommand(commandArray);
            case PERK -> perkCommand(commandArray);
            default -> throw new CommandSyntaxException();
        }
    }

    /*
    *   client command list:
    *       client list
    *
    *       client get *client-id* +
    *       client debt *client-id* +
    *       client checkout *client-id* +
    *       client getperks *client-id* +
    *       client list *sort-type* +
    *
    *       client add *name* *quantity* +
    *       client serve *client-id* *perk-id* +
    *       client checkin *client-id* *stay-duration* +
    *
    *       client checkin *client-id* *stay-duration* *apartment-id*
    */

    private void clientCommand(String[] commandArray) {
        switch (commandArray.length) {
            case 2 -> {
                if (commandArray[1].equals("list")) {
                    ConsolePrinter.printList(clientService.getAll());
                } else {
                    throw new UnknownCommandException(commandArray[1]);
                }
            }
            case 3 -> {
                switch (commandArray[1]) {
                    case "get" ->
                        ConsolePrinter.printEntity(clientService.getById(Long.parseLong(commandArray[2])));
                    case "debt" ->
                        ConsolePrinter.printClientDebt(clientService.getStayCost(Long.parseLong(commandArray[2])));
                    case "checkout" ->
                        ConsolePrinter.printCheckout(clientService.checkOutApartment(Long.parseLong(commandArray[2])));
                    case "getperks" ->
                        ConsolePrinter.printClientPerks(clientService.getAllPerks(Long.parseLong(commandArray[2])));
                    case "list" -> {
                        List<Client> list =  clientService.getSorted(commandArray[2]);
                        ConsolePrinter.printList(list);
                    }
                    default -> throw new UnknownCommandException(commandArray[1]);
                }
            }
            case 4 -> {
                switch (commandArray[1]) {
                    case "add" ->
                        ConsolePrinter.printAddedClient(clientService.save(
                                commandArray[2],
                                Integer.parseInt(commandArray[3])));
                    case "serve" ->
                        ConsolePrinter.printServedPerk(clientService.addPerk(
                                Long.parseLong(commandArray[2]),
                                Long.parseLong(commandArray[3])));
                    case "checkin" ->
                        ConsolePrinter.printCheckIn(clientService.checkInApartment(
                                Long.parseLong(commandArray[2]),
                                Integer.parseInt(commandArray[3]),
                                0));
                    default -> throw new UnknownCommandException(commandArray[1]);
                }
            }
            case 5 -> {
                if (commandArray[1].equals("checkin")) {
                    ConsolePrinter.printCheckIn(clientService.checkInApartment(
                            Long.parseLong(commandArray[2]),
                            Integer.parseInt(commandArray[3]),
                            Long.parseLong(commandArray[4])));
                } else {
                    throw new UnknownCommandException(commandArray[1]);
                }
            }
            default -> throw new CommandSyntaxException();
        }
    }

    /*
     *   apartment command list:
     *      apartment list
     *
     *      apartment get *client-id*
     *      apartment list *sort-type*
     *      apartment price *apartment-id*
     *      apartment changestatus *apartment-id*
     *
     *      apartment add *capacity* *price*
     *      apartment price *apartment-id* *new-price*
     */

    private void apartmentCommand(String[] commandArray) {
        switch (commandArray.length) {
            case 2 -> {
                if (commandArray[1].equals("list")) {
                    ConsolePrinter.printList(apartmentService.getAll());
                } else throw new UnknownCommandException(commandArray[1]);
            }
            case 3 -> {
                switch (commandArray[1]) {
                    case "get" ->
                        ConsolePrinter.printEntity(apartmentService.getById(Long.parseLong(commandArray[2])));
                    case "list" -> {
                        List<Apartment> list = apartmentService.getSorted(commandArray[2].toLowerCase());
                        ConsolePrinter.printList(list);
                    }
                    case "price" ->
                            ConsolePrinter.printApartmentPrice(apartmentService.getById(Long.parseLong(commandArray[2])));
                    case "changestatus" ->
                            ConsolePrinter.printApartmentStatusChange(apartmentService.changeStatus(Long.parseLong(commandArray[2])));
                    default -> throw new UnknownCommandException(commandArray[1]);
                }
            }
            case 4 -> {
                switch (commandArray[1]) {
                    case "add" ->
                        ConsolePrinter.printAddedApartment(apartmentService.save(
                                Integer.parseInt(commandArray[2]), Double.parseDouble(commandArray[3])));
                    case "price" ->
                        ConsolePrinter.printApartmentPriceChange(apartmentService.changePrice(
                                Long.parseLong(commandArray[2]), Double.parseDouble(commandArray[3])));
                    default -> throw new UnknownCommandException(commandArray[1]);
                }
            }
            default -> throw new CommandSyntaxException();
        }
    }

    /*  perk command list:
            perk list

            perk price *perk-id*
            perk get *perk-id*
            perk list *sort-type*

            perk add *name* *price*
            perk price *perk-id* *new-price*
     */

    private void perkCommand(String[] commandArray) {
        switch (commandArray.length) {
            case 2 -> {
                if (commandArray[1].equals("list")) {
                    ConsolePrinter.printList(perkService.getAll());
                } else throw new UnknownCommandException(commandArray[1]);
            }
            case 3 -> {
                switch (commandArray[1]) {
                    case "get" ->
                        ConsolePrinter.printEntity(perkService.getById(Long.parseLong(commandArray[2])));
                    case "price" ->
                        ConsolePrinter.printPerkPrice(perkService.getById(Long.parseLong(commandArray[2])));
                    case "list" -> {
                        List<Perk> list = perkService.getSorted(commandArray[2].toLowerCase());
                        ConsolePrinter.printList(list);
                    }
                    default -> throw new UnknownCommandException(commandArray[1]);
                }
            }
            case 4 -> {
                switch (commandArray[1]) {
                    case "add" ->
                        ConsolePrinter.printAddedPerk(perkService.save(commandArray[2], Double.parseDouble(commandArray[3])));
                    case "price" ->
                        ConsolePrinter.printPerkPriceChange(perkService.changePrice(
                                Long.parseLong(commandArray[2]), Double.parseDouble(commandArray[3])
                        ));
                    default -> throw new UnknownCommandException(commandArray[1]);
                }
            }
            default -> throw new CommandSyntaxException();
        }
    }

    private enum CommandType {
        CLIENT, APARTMENT, PERK
    }
}
