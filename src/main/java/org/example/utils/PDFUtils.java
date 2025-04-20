package org.example.utils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.IOException;

public class PDFUtils {
    public static void addLogo(Document document, String logoPath) throws DocumentException, IOException {
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(100, 100);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);
        document.add(Chunk.NEWLINE);
    }

    public static void addClientInfo(Document document, String clientName, String clientEmail) throws DocumentException {
        Paragraph clientInfo = new Paragraph();
        clientInfo.add(new Phrase("Client: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        clientInfo.add(new Phrase(clientName + " (" + clientEmail + ")"));
        document.add(clientInfo);
    }

    public static void addPaymentTerms(Document document) throws DocumentException {
        Paragraph terms = new Paragraph("\nConditions de paiement:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD));
        document.add(terms);
        document.add(new Paragraph("Paiement à 30 jours fin de mois"));
        document.add(new Paragraph("TVA non applicable, art. 293 B du CGI"));
    }
}
