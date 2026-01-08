package com.mayan.sospluginmodlue.model;

import java.util.ArrayList;

/**
 * Created by developer on 24/11/16.
 * Response data for company domain api
 */
public class CompanyDomainResponse {

    public String message;

    public String encode;

    public String status;

    public ANDROIDPaths androidPaths;

    public String baseurl, api_base;

    public String apikey;

    public String default_language;

    public String accessKey;

    public String auth_key;

    public class ANDROIDPaths {
        public ArrayList<Passenger_language> passenger_language;

        public String colorcode;

        public ArrayList<Driver_language> driver_language;

        public String static_image;
    }

    public class Passenger_language {
        public String language, design_type, language_code;

        public String url;


    }

    public class Driver_language {
        public String language;

        public String url;


    }
}
