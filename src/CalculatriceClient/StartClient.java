import CalculatriceApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.util.Scanner;

public class StartClient 
{
    private static Calculatrice calculatrice;
    private static Scanner scanner;

    public static void main(String[] args) 
    {
        try 
        {
            System.out.println(">>> Démarrage client CORBA <<<");

            ORB orb = ORB.init(args, null);

            org.omg.CORBA.Object objRef =
                orb.resolve_initial_references("NameService");

            NamingContextExt ncRef =
                NamingContextExtHelper.narrow(objRef);

            calculatrice = CalculatriceHelper.narrow(
                ncRef.resolve_str("CalculatriceService")
            );

            System.out.println("Service connecté ✓");

            scanner = new Scanner(System.in);

            menu(orb);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void menu(ORB orb)
    {
        while (true)
        {
            System.out.println("\n===== MENU =====");
            System.out.println("1 Add");
            System.out.println("2 Sub");
            System.out.println("3 Mul");
            System.out.println("4 Div");
            System.out.println("5 Modulo");
            System.out.println("6 Power");
            System.out.println("7 Quit");
            System.out.print("Choix: ");

            String choix = scanner.nextLine();

            try
            {
                switch (choix)
                {
                    case "1":
                        System.out.print("a: ");
                        double a = Double.parseDouble(scanner.nextLine());
                        System.out.print("b: ");
                        double b = Double.parseDouble(scanner.nextLine());
                        System.out.println(calculatrice.add(a, b));
                        break;

                    case "2":
                        System.out.print("a: ");
                        a = Double.parseDouble(scanner.nextLine());
                        System.out.print("b: ");
                        b = Double.parseDouble(scanner.nextLine());
                        System.out.println(calculatrice.substract(a, b));
                        break;

                    case "3":
                        System.out.print("a: ");
                        a = Double.parseDouble(scanner.nextLine());
                        System.out.print("b: ");
                        b = Double.parseDouble(scanner.nextLine());
                        System.out.println(calculatrice.multiply(a, b));
                        break;

                    case "4":
                        System.out.print("a: ");
                        a = Double.parseDouble(scanner.nextLine());
                        System.out.print("b: ");
                        b = Double.parseDouble(scanner.nextLine());
                        System.out.println(calculatrice.divide(a, b));
                        break;

                    case "5":
                        System.out.print("a: ");
                        int x = Integer.parseInt(scanner.nextLine());

                        System.out.print("b: ");
                        int y = Integer.parseInt(scanner.nextLine());

                        int mod = calculatrice.modulo(x, y);
                        System.out.println("Résultat: " + mod);
                        break;

                    case "6":
                        System.out.print("base: ");
                        double base = Double.parseDouble(scanner.nextLine());

                        System.out.print("exp: ");
                        int exp = Integer.parseInt(scanner.nextLine());

                        double pow = calculatrice.power(base, exp);
                        System.out.println("Résultat: " + pow);
                        break;

                    case "7":
                        calculatrice.shutdown();
                        System.exit(0);
                }
            }
            catch (Exception e)
            {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
    }
}
