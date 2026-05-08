import CalculatriceApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class StartPdfClientMotDePasseEtCreation {
    private static String readFileToBase64(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void main(String[] args) {
        try {
            // args:
            // [0]=pdf input
            // [1]=password
            // [2]=out password.pdf
            // [3]=texte à mettre dans le pdf créé (simple TP)
            // [4]=out created.pdf

            String inPath = (args.length > 0 ? args[0] : "input.pdf");
            String password = (args.length > 1 ? args[1] : "secret123");
            String outProtected = (args.length > 2 ? args[2] : "output_protected.pdf");
            String texte = (args.length > 3 ? args[3] : "Bonjour CORBA + PDFBox");
            String outCreated = (args.length > 4 ? args[4] : "output_created.pdf");

            ORB orb = ORB.init(args, null);

            PdfService pdf = null;
            try {
                // 1) Essayer NamingService
                // CORBA: utiliser le NamingService (mode fiable).
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
                org.omg.CORBA.Object refObj = ncRef.resolve_str("PdfService");
                pdf = PdfServiceHelper.narrow(refObj);
            } catch (Exception e) {
                // 2) Fallback via IOR file
                try {
                    java.nio.file.Path p = java.nio.file.Paths.get("c:/RONDOMNUMBER9/TP_Corba/ior.txt");
                    String txt = new String(java.nio.file.Files.readAllBytes(p), java.nio.charset.StandardCharsets.UTF_8);
                    String iorPdf = null;
                    for (String line : txt.split("\\r?\\n")) {
                        if (line.startsWith("IOR_PDF=")) {
                            iorPdf = line.substring("IOR_PDF=".length()).trim();
                        }
                    }
                    if (iorPdf == null) throw new RuntimeException("IOR_PDF absent dans ior.txt");

                    org.omg.CORBA.Object iorObj = orb.string_to_object(iorPdf);
                    pdf = PdfServiceHelper.narrow(iorObj);
                    System.out.println("[INFO] PdfService récupéré via ior.txt");
                } catch (Exception e2) {
                    throw new RuntimeException("Impossible de contacter NamingService et impossible de lire ior.txt", e2);
                }
            }




            String inB64 = readFileToBase64(inPath);

            ResultatPdf prot = pdf.ajoutMotDePasse(inB64, password);
            if (!prot.succes) {
                System.out.println("Ajout mot de passe KO: " + prot.message);
                return;
            }
            Files.write(new File(outProtected).toPath(), Base64.getDecoder().decode(prot.nomFichierSortie));

            ResultatPdf created = pdf.creationPdf(texte);
            if (!created.succes) {
                System.out.println("Creation PDF KO: " + created.message);
                return;
            }
            Files.write(new File(outCreated).toPath(), Base64.getDecoder().decode(created.nomFichierSortie));

            System.out.println("OK. Prot->" + outProtected + " / Created->" + outCreated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

