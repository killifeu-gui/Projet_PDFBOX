import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class CreatePDF {
    public static void main(String[] args) throws Exception {
        PDDocument doc = new PDDocument();
        doc.addPage(new PDPage());
        doc.save("dummy.pdf");
        doc.close();
        System.out.println("PDF créé");
    }
}