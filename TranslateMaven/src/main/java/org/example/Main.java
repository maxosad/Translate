package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static final String API_KEY = "Api-Key ";
    public static final String authorization = "your ip key";
    public static final String FOLDER_ID = "your folder id";
    public static final String uriTranslate = "https://translate.api.cloud.yandex.net/translate/v2/translate";
    public static final String TRANSLATIONS = "translations";
    public static final String TEXT = "text";
    public static final String AUTHORIZATION = "Authorization";
    public static final String SOURCE_LANGUAGE_CODE = "sourceLanguageCode";
    public static final String TARGET_LANGUAGE_CODE = "targetLanguageCode";
    public static final String TEXTS = "texts";
    public static final String FOLDER_ID1 = "folderId";
    public static final String INPUT_TEXT = "Input text";
    public static final String INPUT_SOURCE_LANGUAGE = "Input source language";
    public static final String INPUT_TARGET_LANGUAGE = "Input target language";
    public static final String SPACE = " ";


    public static final String translate (String texts, String source, String target) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, API_KEY + authorization);
        var personJsonObject = new JSONObject();
        personJsonObject.put(SOURCE_LANGUAGE_CODE, source);
        personJsonObject.put(TARGET_LANGUAGE_CODE, target);
        personJsonObject.put(TEXTS, texts );
        personJsonObject.put(FOLDER_ID1, FOLDER_ID);

        RestTemplate restTemplate = new RestTemplate();

        var request = new HttpEntity<>(personJsonObject.toString(), headers);


        return restTemplate.postForObject(uriTranslate, request, String.class);
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(INPUT_TEXT);
        String line = scanner.nextLine();
        System.out.println(INPUT_SOURCE_LANGUAGE);
        var source = scanner.nextLine();
        System.out.println(INPUT_TARGET_LANGUAGE);
        var target = scanner.nextLine();
        var tokens = line.split(SPACE);

        try(var executor = Executors.newFixedThreadPool(10);) {
            List<Callable<String>> tasks = new ArrayList<>();
            for (String token : tokens) {
                tasks.add(() -> translate(token, source, target));
            }


            List<Future<String>> futures = new ArrayList<>();

            for (Callable<String> task : tasks) {
                futures.add(executor.submit(task));
            }
            try {
                StringJoiner joiner = new StringJoiner(SPACE);
                for (Future<String> future : futures) {
                    String s = future.get();
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(s);
                    var arr = ((JSONObject)((JSONArray) json.get(TRANSLATIONS)).getFirst()).get(TEXT);
                    joiner.add((String) arr);
                }
                String collect = joiner.toString();

                System.out.println(collect);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}