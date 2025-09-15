package com.thunderfat.springboot.backend.spain.facturae;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thunderfat.springboot.backend.model.entity.Factura;
import com.thunderfat.springboot.backend.model.entity.LineaFactura;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para generación de facturas en formato Facturae.
 * Requerido para transacciones B2B a partir de 2026 según normativa española.
 * 
 * Genera archivos XML conformes al estándar Facturae 3.2.2 de la AEAT.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Slf4j
@Service
public class FacturaeGeneratorService {

    @Value("${thunderfat.spain.facturae.version:3.2.2}")
    private String facturaeVersion;

    @Value("${thunderfat.spain.facturae.output-directory:${java.io.tmpdir}/facturae}")
    private String outputDirectory;

    @Value("${thunderfat.spain.company.nif}")
    private String companyNif;

    @Value("${thunderfat.spain.company.nombre}")
    private String companyNombre;

    @Value("${thunderfat.spain.company.direccion}")
    private String companyDireccion;

    @Value("${thunderfat.spain.company.codigo-postal}")
    private String companyCodigoPostal;

    @Value("${thunderfat.spain.company.poblacion}")
    private String companyCiudad;

    @Value("${thunderfat.spain.company.provincia}")
    private String companyProvincia;

    @Value("${thunderfat.spain.company.codigo-pais:ES}")
    private String companyPais;

    /**
     * Genera un archivo Facturae XML para una factura
     * 
     * @param factura Factura a convertir a formato Facturae
     * @return Ruta del archivo XML generado
     */
    public String generarFacturaeXml(Factura factura) {
        log.info("Generando Facturae XML para factura: {}", factura.getNumeroFactura());

        try {
            // Crear documento XML
            Document document = crearDocumentoFacturae(factura);
            
            // Guardar en archivo
            String rutaArchivo = guardarArchivoXml(document, factura.getNumeroFactura());
            
            log.info("Facturae XML generado exitosamente: {}", rutaArchivo);
            return rutaArchivo;
            
        } catch (Exception e) {
            log.error("Error generando Facturae XML para factura {}: {}", 
                     factura.getNumeroFactura(), e.getMessage(), e);
            throw new FacturaeGenerationException("Error generando Facturae XML", e);
        }
    }

    /**
     * Crea el documento XML según el estándar Facturae
     */
    private Document crearDocumentoFacturae(Factura factura) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Elemento raíz
        Element facturaeElement = document.createElement("fe:Facturae");
        facturaeElement.setAttribute("xmlns:fe", "http://www.facturae.gob.es/formato/Versiones/Facturae_3_2_2.xsd");
        facturaeElement.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        document.appendChild(facturaeElement);

        // FileHeader
        agregarFileHeader(document, facturaeElement, factura);

        // Parties (Emisor y Receptor)
        agregarParties(document, facturaeElement, factura);

        // Invoices
        agregarInvoices(document, facturaeElement, factura);

        return document;
    }

    /**
     * Agrega la cabecera del archivo Facturae
     */
    private void agregarFileHeader(Document document, Element root, Factura factura) {
        Element fileHeader = document.createElement("FileHeader");
        root.appendChild(fileHeader);

        // Información del esquema
        Element schemaVersion = document.createElement("SchemaVersion");
        schemaVersion.setTextContent(facturaeVersion);
        fileHeader.appendChild(schemaVersion);

        Element modality = document.createElement("Modality");
        modality.setTextContent("I"); // Individual
        fileHeader.appendChild(modality);

        Element invoiceIssuerType = document.createElement("InvoiceIssuerType");
        invoiceIssuerType.setTextContent("EM"); // Emisor
        fileHeader.appendChild(invoiceIssuerType);

        // Información de terceros (opcional)
        Element thirdParty = document.createElement("ThirdParty");
        Element taxIdentification = document.createElement("TaxIdentification");
        Element personTypeCode = document.createElement("PersonTypeCode");
        personTypeCode.setTextContent("J"); // Jurídica
        taxIdentification.appendChild(personTypeCode);

        Element residenceTypeCode = document.createElement("ResidenceTypeCode");
        residenceTypeCode.setTextContent("R"); // Residente
        taxIdentification.appendChild(residenceTypeCode);

        Element taxIdentificationNumber = document.createElement("TaxIdentificationNumber");
        taxIdentificationNumber.setTextContent(companyNif);
        taxIdentification.appendChild(taxIdentificationNumber);

        thirdParty.appendChild(taxIdentification);
        fileHeader.appendChild(thirdParty);

        // Lote de facturas
        Element batch = document.createElement("Batch");
        Element batchIdentifier = document.createElement("BatchIdentifier");
        batchIdentifier.setTextContent("LOTE-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        batch.appendChild(batchIdentifier);

        Element invoicesCount = document.createElement("InvoicesCount");
        invoicesCount.setTextContent("1");
        batch.appendChild(invoicesCount);

        Element totalInvoicesAmount = document.createElement("TotalInvoicesAmount");
        Element totalAmount = document.createElement("TotalAmount");
        totalAmount.setTextContent(factura.getTotal().toString());
        totalInvoicesAmount.appendChild(totalAmount);
        batch.appendChild(totalInvoicesAmount);

        fileHeader.appendChild(batch);
    }

    /**
     * Agrega información de las partes (emisor y receptor)
     */
    private void agregarParties(Document document, Element root, Factura factura) {
        Element parties = document.createElement("Parties");
        root.appendChild(parties);

        // Emisor
        Element sellerParty = document.createElement("SellerParty");
        agregarDatosEmisor(document, sellerParty);
        parties.appendChild(sellerParty);

        // Receptor
        Element buyerParty = document.createElement("BuyerParty");
        agregarDatosReceptor(document, buyerParty, factura);
        parties.appendChild(buyerParty);
    }

    /**
     * Agrega datos del emisor
     */
    private void agregarDatosEmisor(Document document, Element sellerParty) {
        Element taxIdentification = document.createElement("TaxIdentification");
        
        Element personTypeCode = document.createElement("PersonTypeCode");
        personTypeCode.setTextContent("J"); // Jurídica
        taxIdentification.appendChild(personTypeCode);

        Element residenceTypeCode = document.createElement("ResidenceTypeCode");
        residenceTypeCode.setTextContent("R"); // Residente
        taxIdentification.appendChild(residenceTypeCode);

        Element taxIdentificationNumber = document.createElement("TaxIdentificationNumber");
        taxIdentificationNumber.setTextContent(companyNif);
        taxIdentification.appendChild(taxIdentificationNumber);

        sellerParty.appendChild(taxIdentification);

        // Datos administrativos
        Element administrativeInfo = document.createElement("AdministrativeCentres");
        Element administrativeCentre = document.createElement("AdministrativeCentre");
        
        Element centreCode = document.createElement("CentreCode");
        centreCode.setTextContent("0001");
        administrativeCentre.appendChild(centreCode);

        Element roleTypeCode = document.createElement("RoleTypeCode");
        roleTypeCode.setTextContent("01"); // Oficina emisora
        administrativeCentre.appendChild(roleTypeCode);

        Element name = document.createElement("Name");
        name.setTextContent(companyNombre);
        administrativeCentre.appendChild(name);

        Element address = document.createElement("AddressInSpain");
        Element addressDetail = document.createElement("Address");
        addressDetail.setTextContent(companyDireccion);
        address.appendChild(addressDetail);

        Element postCode = document.createElement("PostCode");
        postCode.setTextContent(companyCodigoPostal);
        address.appendChild(postCode);

        Element town = document.createElement("Town");
        town.setTextContent(companyCiudad);
        address.appendChild(town);

        Element province = document.createElement("Province");
        province.setTextContent(companyProvincia);
        address.appendChild(province);

        Element countryCode = document.createElement("CountryCode");
        countryCode.setTextContent(companyPais);
        address.appendChild(countryCode);

        administrativeCentre.appendChild(address);
        administrativeInfo.appendChild(administrativeCentre);
        sellerParty.appendChild(administrativeInfo);
    }

    /**
     * Agrega datos del receptor
     */
    private void agregarDatosReceptor(Document document, Element buyerParty, Factura factura) {
        Element taxIdentification = document.createElement("TaxIdentification");
        
        Element personTypeCode = document.createElement("PersonTypeCode");
        personTypeCode.setTextContent("F"); // Física
        taxIdentification.appendChild(personTypeCode);

        Element residenceTypeCode = document.createElement("ResidenceTypeCode");
        residenceTypeCode.setTextContent("R"); // Residente
        taxIdentification.appendChild(residenceTypeCode);

        Element taxIdentificationNumber = document.createElement("TaxIdentificationNumber");
        taxIdentificationNumber.setTextContent(factura.getPaciente().getDni() != null ? 
                                             factura.getPaciente().getDni() : "00000000T");
        taxIdentification.appendChild(taxIdentificationNumber);

        buyerParty.appendChild(taxIdentification);

        // Datos administrativos del cliente
        Element individualData = document.createElement("Individual");
        Element name = document.createElement("Name");
        name.setTextContent(factura.getPaciente().getNombre());
        individualData.appendChild(name);

        Element firstSurname = document.createElement("FirstSurname");
        firstSurname.setTextContent(factura.getPaciente().getApellidos() != null ? 
                                   factura.getPaciente().getApellidos().split(" ")[0] : "");
        individualData.appendChild(firstSurname);

        buyerParty.appendChild(individualData);
    }

    /**
     * Agrega información de las facturas
     */
    private void agregarInvoices(Document document, Element root, Factura factura) {
        Element invoices = document.createElement("Invoices");
        root.appendChild(invoices);

        Element invoice = document.createElement("Invoice");
        invoices.appendChild(invoice);

        // Cabecera de la factura
        Element invoiceHeader = document.createElement("InvoiceHeader");
        
        Element invoiceNumber = document.createElement("InvoiceNumber");
        invoiceNumber.setTextContent(factura.getNumeroFactura());
        invoiceHeader.appendChild(invoiceNumber);

        Element invoiceDocumentType = document.createElement("InvoiceDocumentType");
        invoiceDocumentType.setTextContent("FC"); // Factura completa
        invoiceHeader.appendChild(invoiceDocumentType);

        Element invoiceClass = document.createElement("InvoiceClass");
        invoiceClass.setTextContent("OO"); // Original
        invoiceHeader.appendChild(invoiceClass);

        invoice.appendChild(invoiceHeader);

        // Datos de la factura
        Element invoiceIssueData = document.createElement("InvoiceIssueData");
        
        Element issueDate = document.createElement("IssueDate");
        issueDate.setTextContent(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        invoiceIssueData.appendChild(issueDate);

        Element invoicingPeriod = document.createElement("InvoicingPeriod");
        Element startDate = document.createElement("StartDate");
        startDate.setTextContent(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        invoicingPeriod.appendChild(startDate);
        
        Element endDate = document.createElement("EndDate");
        endDate.setTextContent(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        invoicingPeriod.appendChild(endDate);
        
        invoiceIssueData.appendChild(invoicingPeriod);

        Element invoiceCurrencyCode = document.createElement("InvoiceCurrencyCode");
        invoiceCurrencyCode.setTextContent("EUR");
        invoiceIssueData.appendChild(invoiceCurrencyCode);

        Element taxCurrencyCode = document.createElement("TaxCurrencyCode");
        taxCurrencyCode.setTextContent("EUR");
        invoiceIssueData.appendChild(taxCurrencyCode);

        Element languageName = document.createElement("LanguageName");
        languageName.setTextContent("es");
        invoiceIssueData.appendChild(languageName);

        invoice.appendChild(invoiceIssueData);

        // Líneas de la factura
        agregarLineasFactura(document, invoice, factura);

        // Totales
        agregarTotalesFactura(document, invoice, factura);
    }

    /**
     * Agrega las líneas de detalle de la factura
     */
    private void agregarLineasFactura(Document document, Element invoice, Factura factura) {
        Element items = document.createElement("Items");
        invoice.appendChild(items);

        for (LineaFactura linea : factura.getLineas()) {
            Element invoiceLine = document.createElement("InvoiceLine");
            
            Element itemDescription = document.createElement("ItemDescription");
            itemDescription.setTextContent(linea.getDescripcion());
            invoiceLine.appendChild(itemDescription);

            Element quantity = document.createElement("Quantity");
            quantity.setTextContent(linea.getCantidad().toString());
            invoiceLine.appendChild(quantity);

            Element unitOfMeasure = document.createElement("UnitOfMeasure");
            unitOfMeasure.setTextContent("01"); // Unidades
            invoiceLine.appendChild(unitOfMeasure);

            Element unitPriceWithoutTax = document.createElement("UnitPriceWithoutTax");
            unitPriceWithoutTax.setTextContent(linea.getPrecioUnitario().toString());
            invoiceLine.appendChild(unitPriceWithoutTax);

            Element totalCost = document.createElement("TotalCost");
            totalCost.setTextContent(linea.getBaseImponible().toString());
            invoiceLine.appendChild(totalCost);

            items.appendChild(invoiceLine);
        }
    }

    /**
     * Agrega los totales de la factura
     */
    private void agregarTotalesFactura(Document document, Element invoice, Factura factura) {
        Element invoiceTotals = document.createElement("InvoiceTotals");
        
        Element totalGrossAmount = document.createElement("TotalGrossAmount");
        totalGrossAmount.setTextContent(factura.getBaseImponible().toString());
        invoiceTotals.appendChild(totalGrossAmount);

        Element totalTaxOutputs = document.createElement("TotalTaxOutputs");
        totalTaxOutputs.setTextContent(factura.getImporteIva().toString());
        invoiceTotals.appendChild(totalTaxOutputs);

        Element invoiceTotal = document.createElement("InvoiceTotal");
        invoiceTotal.setTextContent(factura.getTotal().toString());
        invoiceTotals.appendChild(invoiceTotal);

        invoice.appendChild(invoiceTotals);

        // Información fiscal
        Element taxesOutputs = document.createElement("TaxesOutputs");
        Element tax = document.createElement("Tax");
        
        Element taxTypeCode = document.createElement("TaxTypeCode");
        taxTypeCode.setTextContent("01"); // IVA
        tax.appendChild(taxTypeCode);

        Element taxRate = document.createElement("TaxRate");
        taxRate.setTextContent(factura.getTipoIva().toString());
        tax.appendChild(taxRate);

        Element taxableBase = document.createElement("TaxableBase");
        Element totalAmount = document.createElement("TotalAmount");
        totalAmount.setTextContent(factura.getBaseImponible().toString());
        taxableBase.appendChild(totalAmount);
        tax.appendChild(taxableBase);

        Element taxAmount = document.createElement("TaxAmount");
        Element totalTaxAmount = document.createElement("TotalAmount");
        totalTaxAmount.setTextContent(factura.getImporteIva().toString());
        taxAmount.appendChild(totalTaxAmount);
        tax.appendChild(taxAmount);

        taxesOutputs.appendChild(tax);
        invoice.appendChild(taxesOutputs);
    }

    /**
     * Guarda el documento XML en un archivo
     */
    private String guardarArchivoXml(Document document, String numeroFactura) throws Exception {
        // Crear directorio si no existe
        Path directory = Paths.get(outputDirectory);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String nombreArchivo = "FACTURAE_" + numeroFactura.replace("/", "_") + "_" + 
                              LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xml";
        Path rutaArchivo = directory.resolve(nombreArchivo);

        // Configurar transformer para formato XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        // Escribir archivo
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(Files.newOutputStream(rutaArchivo));
        transformer.transform(source, result);

        return rutaArchivo.toString();
    }

    /**
     * Valida si una factura requiere formato Facturae
     */
    public boolean requiereFacturae(Factura factura) {
        return factura.requiereFormatoFacturae();
    }

    /**
     * Convierte el documento XML a string (para testing o preview)
     */
    public String documentoAString(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }
}
