import CalculatriceApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

/**
 * Classe de démarrage du serveur CORBA
 * 
 * Responsabilités :
 * 1. Initialiser l'ORB
 * 2. Créer et enregistrer l'implémentation
 * 3. S'enregistrer auprès du Naming Service
 * 4. Attendre les invocations des clients
 */
public class StartServer 
{
    public static void main(String args[]) 
    {
        try 
        {
            // ===== ÉTAPE 1 : Initialisation de l'ORB =====
            System.out.println(">>> Démarrage du serveur CORBA <<<");
            System.out.println("[1/4] Initialisation de l'ORB...");
            
            ORB orb = ORB.init(args, null);

            // ===== ÉTAPE 2 : Récupération du POA (Portable Object Adapter) =====
            System.out.println("[2/4] Configuration du POA (Portable Object Adapter)...");
            
            POA rootpoa = POAHelper.narrow(
                orb.resolve_initial_references("RootPOA")
            );
            
            // Activation du gestionnaire de POA
            rootpoa.the_POAManager().activate();
            System.out.println("    ✓ POA activé");

            // ===== ÉTAPE 3 : Création des objets serveur =====
            System.out.println("[3/5] Création des implémentations...");

            // --- Calculatrice (math) ---
            System.out.println("    [3.1] Calculatrice : création du servant...");
            CalculatriceImpl calcImpl = new CalculatriceImpl();
            calcImpl.setORB(orb);
            org.omg.CORBA.Object calcRefObj = rootpoa.servant_to_reference(calcImpl);
            Calculatrice calcRef = CalculatriceHelper.narrow(calcRefObj);
            System.out.println("    ✓ Objet Calculatrice créé");

            // --- PdfService ---
            System.out.println("    [3.2] PdfService : création du servant...");
            PdfServiceImpl pdfImpl = new PdfServiceImpl();
            org.omg.CORBA.Object pdfRefObj = rootpoa.servant_to_reference(pdfImpl);
            PdfService pdfRef = PdfServiceHelper.narrow(pdfRefObj);
            System.out.println("    ✓ Objet PdfService créé");


            // ===== ÉTAPE 4 : Enregistrement au Naming Service =====
            System.out.println("[4/5] Enregistrement au Naming Service...");

            boolean noNameService = false;
            for (String a : args) {
                if (a != null && (a.equalsIgnoreCase("-noNameService") || a.equalsIgnoreCase("-bindLocalOnly"))) {
                    noNameService = true;
                    break;
                }
            }

            if (!noNameService)
            {
                try {
                    // Obtenir une référence au Naming Service
                    org.omg.CORBA.Object objRef =
                        orb.resolve_initial_references("NameService");

                    // Affiner en NamingContextExt
                    NamingContextExt ncRef =
                        NamingContextExtHelper.narrow(objRef);

                    // Enregistrer CalculatriceService
                    NameComponent pathCalc[] = ncRef.to_name("CalculatriceService");
                    ncRef.rebind(pathCalc, calcRef);
                    System.out.println("    ✓ Service enregistré comme 'CalculatriceService'");

                    // Enregistrer PdfService
                    NameComponent pathPdf[] = ncRef.to_name("PdfService");
                    ncRef.rebind(pathPdf, pdfRef);
                    System.out.println("    ✓ Service enregistré comme 'PdfService'");
                } catch (Exception e) {
                    System.err.println("[WARN] NamingService non joignable, démarrage en mode local-only. Message: " + e.getMessage());
                    noNameService = true;
                }
            }

            // Toujours persister l'IOR pour permettre aux clients de se connecter sans NamingService.
            // (Si NamingService fonctionne, les clients peuvent l'ignorer.)
            try {
                System.out.println("    [IOR] Persistance IOR (mode fallback vers clients) ...");
                String iorCalc = orb.object_to_string(calcRef);
                String iorPdf = orb.object_to_string(pdfRef);

                String path = "c:/RONDOMNUMBER9/TP_Corba/ior.txt";
                String payload =
                    "IOR_CALC=" + iorCalc + System.lineSeparator() +
                    "IOR_PDF=" + iorPdf + System.lineSeparator();

                java.nio.file.Files.write(
                    java.nio.file.Paths.get(path),
                    payload.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                );

                System.out.println("    ✓ ior.txt généré");
            }
        } catch (Exception ex) {
            System.err.println("[WARN] Impossible d'écrire ior.txt : " + ex.getMessage());
        }


            System.out.println();
            System.out.println("========================================");
            System.out.println("SERVEUR CORBA PRET ET EN ATTENTE");
            System.out.println("Service : CalculatriceService");
            System.out.println("Port ORBD : 1050");
            System.out.println("Appuyez sur Ctrl+C pour arreter");
            System.out.println("========================================");

            System.out.println();
            System.out.println("Services : CalculatriceService, PdfService");


            // ===== ÉTAPE 5 : Attendre les invocations =====
            // Boucle infinie : le serveur attend les appels clients
            orb.run();
        } 
        catch (Exception e) 
        {
            System.err.println("******************************");
            System.err.println("ERREUR AU DEMARRAGE DU SERVEUR");
            System.err.println("******************************");

            System.err.println("Message : " + e.getMessage());
            e.printStackTrace(System.out);
        }
        
        System.out.println("[SERVEUR] Arrêt du serveur...");
    }
}
