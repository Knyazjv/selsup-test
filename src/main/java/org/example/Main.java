package org.example;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 10);

        List<CrptApi.Product> products = new ArrayList<>();
        products.add(new CrptApi.Product("string", "2020-01-23",
                "string", "string", "string",
                "2020-01-23", "string", "string", "string"));

        CrptApi.DocLpIntroduceGoods document = new CrptApi.DocLpIntroduceGoods(
                new CrptApi.Description("string"), "string", "string", "LP_INTRODUCE_GOODS", true,
                "string", "string", "string","2020-01-23",
                "string", products, "2020-01-23", "string");


        HttpResponse<String> response = crptApi.createDocument(document, "signature");
       System.out.println(response.body());
    }
}
