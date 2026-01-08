package com.onepaytaxi.driver.earnings

data class ResponseEarnings(
    val auth_key: String,
    val bank_id_status: Int,
    val message: String,
    val min_wallet_amount: String,
    val status: Int,
    val today_earnings: List<TodayEarning>,
    val weekly_earnings: List<WeeklyEarning>,
    val withdraw_array: List<WithdrawArray>
) {
    data class TodayEarning(
        val average_amount: Int,
        val average_rating: Int,
        val total_amount: String,
        val total_trips: Int
    )

    data class WeeklyEarning(
        val date_text: String,
        val day_list: List<String>,
        val this_week_earnings: String,
        val trip_amount: List<String>
    )

    data class WithdrawArray(
        val driver_incentive_pending_amount: String,
        val driver_incetive_amount: String,
        val driver_trip_wallet_amount: String,
        val driver_wallet_amount: String,
        val driver_wallet_pending_amount: String,
        val total_amount: Double,
        val trip_amount: String,
        val trip_pending_amount: Int,
        val wallet_balance: String
    )
}