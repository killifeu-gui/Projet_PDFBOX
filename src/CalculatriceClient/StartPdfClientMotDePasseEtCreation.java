import CalculatriceApp.*;
import CalculatriceClient.CorbaClientHelper;
import org.omg.CORBA.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class StartPdfClientMotDePasseEtCreation {
    private static String readFileToBase64(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String readTextFile(String path) throws Exception {
        return new String(Files.readAllBytes(new File(path).toPath()));
    }

    public static void main(String[] args) {
        try {
            // args[0]=pdf
            // args[1]=password
            // args[2]=text file
            // args[3]=outputPath (pdf)

            String pdfPath = (args.length > 0 ? args[0] : "input1.pdf");
            String password = (args.length > 1 ? args[1] : "password");
            String textPath = (args.length > 2 ? args[2] : "input.txt");
            String outputPath = (args.length > 3 ? args[3] : "output_creation.pdf");

            // ORB comme dans StartClient.
            ORB orb = ORB.init(args, null);

            PdfService pdf = CorbaClientHelper.getPdfService(orb);

            // 1) Ajout mot de passe
            String pdfB64 = readFileToBase64(pdfPath);
            ResultatPdf ajoutMdp = pdf.ajoutMotDePasse(pdfB64, password);
            if (!ajoutMdp.succes) {
                System.out.println("Ajout mot de passe KO: " + ajoutMdp.message);
                return;
            }
            byte[] outMdp = Base64.getDecoder().decode(ajoutMdp.nomFichierSortie);
            String outputMdpPath = "output_mdp.pdf";
            Files.write(new File(outputMdpPath).toPath(), outMdp);
            System.out.println("OK. PDF avec mot de passe ecrit dans: " + outputMdpPath);

            // 2) Creation PDF
            String texte = readTextFile(textPath);
            ResultatPdf creation = pdf.creationPdf(texte);
            if (!creation.succes) {
                System.out.println("Creation PDF KO: " + creation.message);
                return;
            }

            // nomFichierSortie contient le base64 du PDF résultat dans ce TP
            byte[] outCreation = Base64.getDecoder().decode(creation.nomFichierSortie);
            Files.write(new File(outputPath).toPath(), outCreation);

            System.out.println("OK. Creation PDF ecrite dans: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
