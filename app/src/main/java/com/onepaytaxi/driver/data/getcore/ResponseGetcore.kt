package com.onepaytaxi.driver.data.getcore

data class ResponseGetcore(
    val activity_bg: Int,
    val auth_key: Any,
    val detail: List<Detail>,
    val dispatcher_phone_number: String,
    val driver_app_help_url: String,
    val driver_settlement_enable: Int,
    val driver_wallet_enable: Int,
    val error_logs: Int,
    val gt_lst_time: Int,
    val https_node_url: String,
    val iOSStaticImage: Int,
    val language_color: LanguageColor,
    val language_color_status: LanguageColorStatus,
    val message: String,
    val mobile_socket_http_domain: String,
    val mobile_socket_http_url: String,
    val reconnect_socket: Int,
    val status: Int,
    val street_pickup_enable: Int,
    val timezone: String
) {
    data class Detail(
        val Fb_Profile: String,
        val Itune_Driver: String,
        val Itune_Passenger: String,
        val aboutpage_description: String,
        val admin_email: String,
        val android_driver_version: Int,
        val android_foursquare_api_key: String,
        val android_foursquare_status: String,
        val android_google_api_key: String,
        val android_local_map_enable: Int,
        val android_mapbox_key: String,
        val android_passenger_version: Int,
        val api_base: String,
        val app_name: String,
        val book_later_tone: String,
        val book_now_tone: String,
        val call_masking: Int,
        val cancellation_fare_setting: String,
        val cancellation_setting: String,
        val country_code: String,
        val country_iso_code: String,
        val current_time: Int,
        val default_city_id: Int,
        val default_city_name: String,
        val default_language: String,
        val default_payment_id: Int,
        val domain_expiry_date: String,
        val driver_menu1: Int,
        val driver_menu2: Int,
        val driver_referral_settings: Int,
        val driver_referral_settings_message: String,
        val driver_waiting_time_interval: Int,
        val dynamic_fare_enable: Int,
        val email_contact: String,
        val facebook_key: String,
        val facebook_secretkey: String,
        val facebook_share: String,
        val fb_profile: String,
        val fm_gateway_url: String,
        val gateway_array: List<GatewayArray>,
        val google_business_key: Int,
        val hotline_number: String,
        val ios_foursquare_api_key: String,
        val ios_foursquare_status: String,
        val ios_google_geo_key: String,
        val ios_google_map_key: String,
        val ios_mapbox_key: String,
        val is_bank_card_id_mantatory: String,
        val itune_driver: String,
        val itune_passenger: String,
        val last_forceupdate_version: Int,
        val last_forceupdate_version_ios: String,
        val load_balancing: Int,
        val logo_base: String,
        val manual_waiting_enable: Int,
        val map_settings: MapSettings,
        val mapbox_key: String,
        val mercado_gateway_url: String,
        val metric: String,
        val mobile_socket: String,
        val mobile_socket_url: String,
        val model_details: List<ModelDetail>,
        val noimage_base: String,
        val otp_waitingtime: Int,
        val passenger_payment_option: List<PassengerPaymentOption>,
        val playstore_driver: String,
        val playstore_passenger: String,
        val referral_code_info: String,
        val referral_settings: Int,
        val referral_settings_message: String,
        val restricton_count: Int,
        val share_content: String,
        val site_country: Int,
        val site_currency: String,
        val site_logo: String,
        val skip_credit: String,
        val skip_driver_email: Int,
        val skip_driver_signup_fleet: Int,
        val skip_driver_signup_flet: Int,
        val skip_passenger_email: Int,
        val sos_msg: String,
        val sos_setting: Int,
        val stops_enable: Int,
        val tax: String,
        val tell_to_friend_subject: String,
        val twitter_share: String,
        val utc_time: Int,
        val vehicle_color_list: List<VehicleColor>,
        val vehicle_info_list: List<VehicleInfo>,
        val vehicle_plate_prefix_list: List<VehiclePlatePrefix>,
        val vehicle_state_list: List<VehicleState>
    ) {
        data class GatewayArray(
            val _id: Int,
            val pay_mod_default: Int,
            val pay_mod_id: Int,
            val pay_mod_name: String
        )

        data class MapSettings(
            val display_current_location: Int,
            val enable_route: Int,
            val is_google_direction: Int,
            val is_google_distance: Int,
            val is_google_geocode: Int
        )

        data class ModelDetail(
            val _id: Int,
            val above_km: Int,
            val additional_fare_per_km: String,
            val base_fare: Int,
            val below_above_km: Int,
            val below_km: Int,
            val cancellation_fare: Int,
            val enable_local: Int,
            val evening_charge: Int,
            val evening_fare: Int,
            val evening_timing_from: String,
            val evening_timing_to: String,
            val fare_type: Int,
            val focus_image: String,
            val focus_image_ios: String,
            val km_wise_fare: Int,
            val min_fare: Int,
            val min_km: Int,
            val minutes_fare: Int,
            val model_fare_stage1_fare: Int,
            val model_fare_stage1_from: Int,
            val model_fare_stage1_to: Int,
            val model_fare_stage2_fare: Int,
            val model_fare_stage2_from: Int,
            val model_fare_stage2_to: Int,
            val model_fare_stage3_fare: Int,
            val model_fare_stage3_from: Int,
            val model_fare_stage3_to: Int,
            val model_id: Int,
            val model_name: String,
            val model_size: Int,
            val night_charge: Int,
            val night_fare: Int,
            val night_timing_from: String,
            val night_timing_to: String,
            val rental_outstation_url: String,
            val show_order: Int,
            val unfocus_image: String,
            val unfocus_image_ios: String,
            val waiting_fare: Int
        )

        data class PassengerPaymentOption(
            val _id: Int,
            val pay_mod_default: Int,
            val pay_mod_id: Int,
            val pay_mod_name: String
        )

        data class VehicleColor(
            val _id: Int,
            val color: String,
            val status: String
        )

        data class VehicleInfo(
            val _id: Int,
            val manufacturer_name: String,
            val manufacturer_status: String,
            val model: List<Model>
        ) {
            data class Model(
                val _id: Int,
                val model_manufacturer_id: Int,
                val model_vehicleid: Int,
                val name: String,
                val status: String
            )
        }

        data class VehiclePlatePrefix(
            val _id: Int,
            val plate_prefix: String,
            val status: String
        )

        data class VehicleState(
            val country_name: String,
            val state_countryid: Int,
            val state_default: Int,
            val state_id: Int,
            val state_name: String,
            val state_status: String
        )
    }

    data class LanguageColor(
        val android: Android,
        val iOS: IOS
    ) {
        data class Android(
            val colorcode: String,
            val driverColorCode: String,
            val driver_language: List<DriverLanguage>,
            val passenger_language: List<PassengerLanguage>
        ) {
            data class DriverLanguage(
                val design_type: String,
                val language: String,
                val language_code: String,
                val url: String
            )

            data class PassengerLanguage(
                val design_type: String,
                val language: String,
                val language_code: String,
                val url: String
            )
        }

        data class IOS(
            val colorcode: String,
            val driverColorCode: String,
            val driver_language: List<DriverLanguage>,
            val passenger_language: List<PassengerLanguage>
        ) {
            data class DriverLanguage(
                val design_type: String,
                val language: String,
                val language_code: String,
                val url: String
            )

            data class PassengerLanguage(
                val design_type: String,
                val language: String,
                val language_code: String,
                val url: String
            )
        }
    }

    data class LanguageColorStatus(
        val android_driver_colorcode: String,
        val android_driver_language: String,
        val android_passenger_colorcode: String,
        val android_passenger_language: String,
        val ios_driver_colorcode: String,
        val ios_driver_language: String,
        val ios_passenger_colorcode: String,
        val ios_passenger_language: String
    )
}