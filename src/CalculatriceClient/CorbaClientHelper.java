package CalculatriceClient;

import CalculatriceApp.PdfService;
import CalculatriceApp.PdfServiceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/**
 * Classe utilitaire pour les clients CORBA afin de réduire la duplication de code.
 */
public class CorbaClientHelper {

    /**
     * Obtient une référence au PdfService.
     * Tente d'abord de le résoudre via le NamingService, puis utilise un fichier IOR comme solution de secours.
     *
     * @param orb L'instance de l'ORB.
     * @return Une référence vers le PdfService.
     * @throws RuntimeException si la connexion échoue par les deux méthodes.
     */
    public static PdfService getPdfService(ORB orb) {
        try {
            // 1) Essayer NamingService
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            org.omg.CORBA.Object refObj = ncRef.resolve_str("PdfService");
            System.out.println("[INFO] PdfService récupéré via NamingService.");
            return PdfServiceHelper.narrow(refObj);
        } catch (Exception e) {
            // 2) Fallback via IOR file
            System.out.println("[WARN] NamingService non joignable, tentative avec ior.txt...");
            try {
                java.nio.file.Path p = java.nio.file.Paths.get("c:/RONDOMNUMBER9/TP_Corba/ior.txt");
                String txt = new String(java.nio.file.Files.readAllBytes(p), java.nio.charset.StandardCharsets.UTF_8);
                String iorPdf = java.util.Arrays.stream(txt.split("\\r?\\n")).filter(line -> line.startsWith("IOR_PDF=")).map(line -> line.substring("IOR_PDF=".length()).trim()).findFirst().orElse(null);

                if (iorPdf == null) throw new RuntimeException("IOR_PDF absent dans ior.txt");

                org.omg.CORBA.Object iorObj = orb.string_to_object(iorPdf);
                System.out.println("[INFO] PdfService récupéré via ior.txt.");
                return PdfServiceHelper.narrow(iorObj);
            } catch (Exception e2) {
                throw new RuntimeException("Impossible de contacter NamingService et impossible de lire ior.txt", e2);
            }
        }
    }
}