import CalculatriceApp.*;
import CalculatriceClient.CorbaClientHelper;
import org.omg.CORBA.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class StartPdfClientFusionDecoupage {
    private static String readFileToBase64(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void main(String[] args) {
        try {
            // args[0]=pdf1
            // args[1]=pdf2
            // args[2]=outputPath (pdf)
            // args[3]=debut page
            // args[4]=fin page

            String pdf1Path = (args.length > 0 ? args[0] : "input1.pdf");
            String pdf2Path = (args.length > 1 ? args[1] : "input2.pdf");
            int debut = (args.length > 3 ? Integer.parseInt(args[3]) : 1);
            int fin = (args.length > 4 ? Integer.parseInt(args[4]) : 1);
            String outputPath = (args.length > 2 ? args[2] : "output_decoupage.pdf");

            // ORB comme dans StartClient.
            ORB orb = ORB.init(args, null);

            PdfService pdf = CorbaClientHelper.getPdfService(orb);

            String pdf1B64 = readFileToBase64(pdf1Path);
            String pdf2B64 = readFileToBase64(pdf2Path);

            // 1) Fusion
            ResultatPdf fusion = pdf.fusionPdf(pdf1B64, pdf2B64);
            if (!fusion.succes) {
                System.out.println("Fusion KO: " + fusion.message);
                return;
            }

            // 2) Découpage
            PlagePages plage = new PlagePages(debut, fin);
            ResultatPdf dec = pdf.decoupagePdf(fusion.nomFichierSortie, plage);
            if (!dec.succes) {
                System.out.println("Découpage KO: " + dec.message);
                return;
            }

            // nomFichierSortie contient le base64 du PDF résultat dans ce TP
            byte[] out = Base64.getDecoder().decode(dec.nomFichierSortie);
            Files.write(new File(outputPath).toPath(), out);

            System.out.println("OK. Découpage écrit dans: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
