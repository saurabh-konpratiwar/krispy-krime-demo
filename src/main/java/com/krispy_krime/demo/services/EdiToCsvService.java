package com.krispy_krime.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;


@Service
@Slf4j
public class EdiToCsvService {
    public void convertEdiToCsv(String inputPath, String outputPath) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            // Correct Header for 852
            writer.write("LineNo,UPC,TotalQty,Store,StoreQty");
            writer.newLine();

            String line;

            // Current record data
            String lineNo = "", upc = "", totalQty = "";

            while ((line = reader.readLine()) != null) {

                String[] segments = line.split("~");

                for (String segment : segments) {

                    if (segment.trim().isEmpty()) continue;

                    String[] e = segment.split("\\*");

                    switch (e[0]) {

                        //  LIN → Product
                        case "LIN":
                            lineNo = getValue(e, 1);
                            upc = getValue(e, 3);
                            break;

                        //  ZA → Total Quantity
                        case "ZA":
                            totalQty = getValue(e, 2);
                            break;

                        //  SDQ → Store Distribution
                        case "SDQ":

                            // SDQ format:
                            // SDQ*EA*92*Store1*Qty1*Store2*Qty2...

                            for (int i = 3; i < e.length; i += 2) {

                                String store = getValue(e, i);
                                String storeQty = getValue(e, i + 1);

                                // Write one row per store
                                writer.write(String.join(",",
                                        lineNo, upc, totalQty, store, storeQty));
                                writer.newLine();
                            }
                            break;

                        //  Ignore other segments
                        default:
                            break;
                    }
                }
            }

            log.info(" Successfully Converted EDI 852 TO CSV");
        }
    }

    //  Safe method
    private String getValue(String[] arr, int index) {
        return (index < arr.length && arr[index] != null)
                ? arr[index].trim()
                : "";
    }

//    public void convertEdiToCsv(String inputPath, String outputPath) throws IOException{
//        Smooks smooks = new Smooks("smooks-config.xml");
//        ExecutionContext context = smooks.createExecutionContext();
//
//        StringWriter xmlOutput = new StringWriter();
//
//        // Step 1: Convert EDI → XML
//        smooks.filterSource(context,
//                new StreamSource(new File(inputPath)),
//                new StreamResult(xmlOutput));
//
//        String xml = xmlOutput.toString();
//        System.out.println("Generated XML:\n" + xml);
//
//        // Step 2: Write CSV
//        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
//
//        writer.write("ProductId,Quantity\n");
//
//        String[] lines = xml.split("\n");
//
//        String productId = "";
//        String quantity = "";
//
//        for (String line : lines) {
//
//            if (line.contains("<ProductId>")) {
//                productId = line.replaceAll("<.*?>", "").trim();
//            }
//
//            if (line.contains("<Qty>")) {
//                quantity = line.replaceAll("<.*?>", "").trim();
//
//                // Write row when both available
//                writer.write(productId + "," + quantity + "\n");
//            }
//        }
//
//        writer.close();
//
//        System.out.println("CSV generated successfully!");
//    }
}

