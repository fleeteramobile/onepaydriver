package com.onepaytaxi.driver.data.apiData;

import java.util.List;

public class SettlementReqData {

    public Details details;

    public int status;

    public Info info;

    public String message;

    public class Details {

        public String wallet_amount;

        public int status;

        public String hints;

        public String total_earning;

        public String end_date;

        public String driver_commission_amount;

        public String tax;

        public String cash_collected;

        public int show_button;

        public String last_request_date;

        public String settlement_type;

        public String total_amount_driver;

        public String card_payment;

        public String start_date;

        public String admin_commission_amount;

        public List<ListClass> list;

    }

    public class Info {

        public List<Trip_list> trip_list;

        public String settlement_company_id;

        public String settlement_driver_id;

        public String settlement_tax_amount;

        public String settlement_wallet_collected;

        public String settlement_process_amount;

        public String settlement_total_earning;

        public String settlement_comments;

        public String settlement_card_collected;

        public String settlement_admin_commision_amount;

        public String settlement_type;

        public String settlement_admin_commision;

        public String settlement_cash_collected;

    }

    public class Trip_list {

        public String trip_id;

        public String tax;

        public String total_tripamount;

        public String payment_type;

    }

}