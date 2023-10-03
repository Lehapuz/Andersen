package com.andersenlab.view;

import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.Client;
import com.andersenlab.entity.Perk;

import java.util.List;

public class ConsolePrinter {

    public static void exit() {
        System.out.print("Exiting program...");
    }

    public static void printEntity(Object object) {
        System.out.println(object);
    }

    public static void printAddedClient(Client client) {
        System.out.printf("You added new client! id: %s, name: %s, quantity: %s\n",
                client.getId(), client.getName(), client.getQuantityOfPeople());
    }

    public static void printAddedApartment(Apartment apartment) {
        System.out.printf("You added new apartment! id: %s, capacity: %s, price: %s\n",
                apartment.getId(), apartment.getCapacity(), apartment.getPrice());
    }

    public static void printAddedPerk(Perk perk) {
        System.out.printf("You added new perk! id: %s, name: %s, price: %s\n",
                perk.getId(), perk.getName(), perk.getPrice());
    }

    public static void printPerkPriceChange(Perk perk) {
        System.out.printf("You changed price for perk '%s' to %s\n", perk.getName(), perk.getPrice());
    }

    public static void printClientDebt(double debt) {
        if (debt == 0) {
            System.out.println("This client has no debt yet!");
        } else {
            System.out.printf("Current debt for this client is %s\n", debt);
        }
    }

    public static void printApartmentPrice(Apartment apartment) {
        System.out.printf("Current price for the apartment '%s' is - %s\n", apartment.getId(), apartment.getPrice());
    }

    public static void printApartmentStatusChange(Apartment apartment) {
        System.out.printf("You changed status of apartment '%s' to %s\n", apartment.getId(), apartment.getStatus());
    }

    public static void printApartmentPriceChange(Apartment apartment) {
        System.out.printf("You changed price for apartment '%s' to %s\n", apartment.getId(), apartment.getPrice());
    }

    public static void printServedPerk(Perk perk) {
        System.out.printf("You served perk '%s' for the client!\n", perk.getName());
    }

    public static void printPerkPrice(Perk perk) {
        System.out.printf("Current price for perk '%s' is %s\n", perk.getName(), perk.getPrice());
    }

    public static void printClientPerks(List<Perk> list) {
        if (list.size() == 0) {
            System.out.println("This client has no ordered perks yet!");
        } else {
            for (Perk perk : list) {
                System.out.println(perk);
            }
        }
    }

    public static void printList(List<?> list) {
        if (list.size() == 0) {
            System.out.println("Requested list is empty! :(");
        } else {
            for (Object object : list) {
                System.out.println(object);
            }
        }
    }

    public static void printCheckIn(Client client) {
        System.out.printf("Client '%s' was successfully checked-in in apartment %s!\n",
                client.getId(), client.getApartment().getId());
    }

    public static void printCheckout(double debt) {
        System.out.printf("Client successfully checked-out. Their current bill is: %s\n", debt);
    }

    public static void commands() {
        System.out.println("|‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾|");
        System.out.println("| help                                                        see this list                             |");
        System.out.println("| exit                                                        close the application                     |");
        System.out.println("|———————————————————————————————————————————————————————————————————————————————————————————————————————|");
        System.out.println("| client add *name* *quantity*                                add client                                |");
        System.out.println("| client get *client-id*                                      get client by id                          |");
        System.out.println("| client debt *client-id*                                     get debt for client by id                 |");
        System.out.println("| client list                                                 get full client list                      |");
        System.out.println("| client list *sort-type*                                     get sorted client list                    |");
        System.out.println("| (client sort-types: ID, NAME, CHECKOUT, STATUS)                                                       |");
        System.out.println("| client checkin *client-id* *stay-duration*                  check-in client in a free apartment       |");
        System.out.println("| client checkin *client-id* *stay-duration* *apartment-id*   check-in client in specified apartment    |");
        System.out.println("| client checkout *client-id*                                 check-out client and display current debt |");
        System.out.println("| client serve *client-id* *perk-id*                          serve client with specified perk          |");
        System.out.println("| client getperks *client-id*                                 get list of all perks served to the client|");
        System.out.println("|———————————————————————————————————————————————————————————————————————————————————————————————————————|");
        System.out.println("| apartment add *capacity* *price*                            add new apartment                         |");
        System.out.println("| apartment get *client-id*                                   get apartment by id                       |");
        System.out.println("| apartment price *apartment-id*                              get price for the apartment               |");
        System.out.println("| apartment price *apartment-id* *new-price*                  set new price for apartment               |");
        System.out.println("| apartment list                                              get full apartment list                   |");
        System.out.println("| apartment list *sort-type*                                  get sorted apartment list                 |");
        System.out.println("| (apartment sort-types: ID, PRICE, CAPACITY, STATUS)                                                   |");
        System.out.println("| apartment changestatus *apartment-id*                       change status of the apartment to opposite|");
        System.out.println("|———————————————————————————————————————————————————————————————————————————————————————————————————————|");
        System.out.println("| perk add *name* *price*                                     add new perk                              |");
        System.out.println("| perk get *perk-id*                                          get perk by id                            |");
        System.out.println("| perk price *perk-id*                                        get price for perk                        |");
        System.out.println("| perk price *perk-id* *new-price*                            set price for perk                        |");
        System.out.println("| perk list                                                   get full perk list                        |");
        System.out.println("| perk list *sort-type*                                       get sorted perk list                      |");
        System.out.println("| (perk sort-types: ID, NAME, PRICE)                                                                    |");
        System.out.println("|_______________________________________________________________________________________________________|");
    }

    public static void printInvalidArgument(String msg) {
        System.out.print(msg.substring(msg.indexOf('"')) + " isn't a valid number.\s");
        printHelp();

    }

    public static void printSyntaxError() {
        System.out.print("Command syntax error.\s");
        printHelp();
    }

    public static void printCustomError(String error) {
        System.out.println(error);
    }

    public static void printHelp() {
        System.out.println("Type 'help' for instructions.");
    }
}
