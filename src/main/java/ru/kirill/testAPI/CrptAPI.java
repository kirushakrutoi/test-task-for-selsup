package ru.kirill.testAPI;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

//Тестовое задание для Selsup
public class CrptAPI implements Runnable{
    private static final String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private TimeUnit timeUnit;
    private int requestLimit;
    private int countReq;
    private static final Logger logger = Logger.getLogger(CrptAPI.class.getName());

    public CrptAPI(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        countReq = 0;
        Thread thread = new Thread(this);
        thread.start();// Запуск потока который сбрасывает количество запросов
    }

    //Метод для отправки документа в JSON формате
    public void createAndSendJSON(Document doc, String signature) {
        if(!(countReq == requestLimit) && !(countReq > requestLimit)){
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .POST(HttpRequest.BodyPublishers.ofString(getJson(doc)))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                logger.info("Request has been sent");
                logger.info("Request has been sent \n" + "Response: " + response.body());
                countReq++;
            } catch (IOException | InterruptedException | URISyntaxException e) {
                logger.severe(e.getMessage());
            }
        } else {
            logger.info("Limit on the number of requests has been exceeded");
        }
    }

    private String getJson(Document document) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(document);
    }

    private static Document getDocFromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Document.class);
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(timeUnit.toMillis(1));
            } catch (InterruptedException e){
                e.getStackTrace();
            }

            countReq=0;
        }
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Document {
    @JsonProperty("description")
    private Description description;
    @JsonProperty("doc_id")
    private String docId;
    @JsonProperty("doc_status")
    private String docStatus;
    @JsonProperty("doc_type")
    private String docType;
    @JsonProperty("importRequest")
    private Boolean importRequest;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("participant_inn")
    private String participantInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonProperty("production_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date productionDate;
    @JsonProperty("production_type")
    private String productionType;
    @JsonProperty("products")
    private List<Product> products;
    @JsonProperty("reg_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    private Date regDate;
    @JsonProperty("reg_number")
    private String regNumber;

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Description {
    @JsonProperty("participantInn")
    private String participantInn;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Product {

    @JsonProperty("certificate_document")
    private String certificateDocument;
    @JsonProperty("certificate_document_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date certificateDocumentDate;
    @JsonProperty("certificate_document_number")
    private String certificateDocumentNumber;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonProperty("production_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date productionDate;
    @JsonProperty("tnved_code")
    private String tnvedCode;
    @JsonProperty("uit_code")
    private String uitCode;
    @JsonProperty("uitu_code")
    private String uituCode;
}
