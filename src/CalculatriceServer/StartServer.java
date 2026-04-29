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

            // ===== ÉTAPE 3 : Création de l'objet serveur =====
            System.out.println("[3/4] Création de l'implémentation Calculatrice...");
            
            // Créer une instance de l'implémentation
            CalculatriceImpl calcImpl = new CalculatriceImpl();
            
            // Fournir la référence ORB pour permettre le shutdown
            calcImpl.setORB(orb);
            
            // Convertir le servant en référence CORBA
            org.omg.CORBA.Object ref = 
                rootpoa.servant_to_reference(calcImpl);
            
            // Affiner (narrow) la référence générique en Calculatrice
            Calculatrice calcRef = CalculatriceHelper.narrow(ref);
            System.out.println("    ✓ Objet Calculatrice créé");

            // ===== ÉTAPE 4 : Enregistrement au Naming Service =====
            System.out.println("[4/4] Enregistrement au Naming Service...");
            
            // Obtenir une référence au Naming Service
            org.omg.CORBA.Object objRef = 
                orb.resolve_initial_references("NameService");
            
            // Affiner en NamingContextExt
            NamingContextExt ncRef = 
                NamingContextExtHelper.narrow(objRef);
            
            // Créer un chemin de nommage pour l'objet
            // Format : "nom_du_service"
            NameComponent path[] = ncRef.to_name("CalculatriceService");
            
            // Enregistrer l'objet sous ce nom
            ncRef.rebind(path, calcRef);
            
            System.out.println("    ✓ Service enregistré comme 'CalculatriceService'");
            System.out.println();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║ SERVEUR CORBA PRÊT ET EN ATTENTE      ║");
            System.out.println("║ Service : CalculatriceService          ║");
            System.out.println("║ Port ORBD : 1050                       ║");
            System.out.println("║ Appuyez sur Ctrl+C pour arrêter       ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();

            // ===== ÉTAPE 5 : Attendre les invocations =====
            // Boucle infinie : le serveur attend les appels clients
            orb.run();
        } 
        catch (Exception e) 
        {
            System.err.println("╔════════════════════════════════════════╗");
            System.err.println("║ ERREUR AU DÉMARRAGE DU SERVEUR         ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("Message : " + e.getMessage());
            e.printStackTrace(System.out);
        }
        
        System.out.println("[SERVEUR] Arrêt du serveur...");
    }
}
