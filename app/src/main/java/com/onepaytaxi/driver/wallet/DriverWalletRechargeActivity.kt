package com.onepaytaxi.driver.wallet

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONException
import org.json.JSONObject

lateinit var walletbalTxt : TextView
 lateinit var addmoneyEdt : EditText
 lateinit var addmoneyBut : Button
class DriverWalletRechargeActivity: AppCompatActivity(), PaymentResultWithDataListener,
    ClickInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_wallet_recharge)

        walletbalTxt = findViewById(R.id.walletbalTxt)
        addmoneyEdt = findViewById(R.id.addmoneyEdt)
        addmoneyBut = findViewById(R.id.addmoneyBut)
        walletbalTxt.setText("${SessionSave.getSession("site_currency",this@DriverWalletRechargeActivity)} ${SessionSave.getSession("driver_wallet",this@DriverWalletRechargeActivity)}")
        addmoneyBut.setOnClickListener {
            val enteredAmount = addmoneyEdt.text.toString().trim()

            if (enteredAmount.isEmpty()) {
                Toast.makeText(
                    this@DriverWalletRechargeActivity,
                    "Kindly enter a valid amount",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                try {
                    val amount = enteredAmount.toIntOrNull() // safely convert to Int
                    if (amount == null || amount < 500) {
                        Toast.makeText(
                            this@DriverWalletRechargeActivity,
                            "Amount must be at least 1000",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val j = JSONObject().apply {
                            put("amount", amount)
                        }
                        val url = "type=get_order_id"
                        GetOrderid(url, j)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }

    inner class GetOrderid internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                println("homeactivity"+" "+"6")

                if (NetworkStatus.isOnline(this@DriverWalletRechargeActivity)) {
                    APIService_Retrofit_JSON(
                        this@DriverWalletRechargeActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@DriverWalletRechargeActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@DriverWalletRechargeActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess && result != null) {
                try {

                    println("homeactivity"+" "+"4")

                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {


                        try {
                            val json = JSONObject(result)
                            println("getOrder_id $result")
                            if (json.getInt("status") == 1) {
                                paymentFlow(json.getString("order_id"), addmoneyEdt .text.toString())
                            }
                        } catch (e: JSONException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }

                    } else {


                    }

                } catch (e: JSONException) {
                    throw java.lang.RuntimeException(e)
                }
            } else {
                //CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }


    fun paymentFlow(order_id: String?, addMoneys: String) {
        val samount = addMoneys.toString()

        // rounding off the amount.
        val amount = Math.round(addMoneys.toFloat() * 100)

        // initialize Razorpay account.
        val checkout = Checkout()

        // set your id as below
        checkout.setKeyID(SessionSave.getSession("razorpay_secret_key", this@DriverWalletRechargeActivity))

        // set image
        checkout.setImage(R.mipmap.ic_launcher_ride_logic)

        // initialize json object
        val `object` = JSONObject()
        try {
            // to put name
            `object`.put("name", "Onepaytaxi")

            // put description
            `object`.put("description", "Onepay taxi wallet recharege")
            `object`.put("image", R.mipmap.ic_launcher_ride_logic)
            `object`.put("theme.color", getResources().getColor(R.color.app_theme_main))
            `object`.put("currency", "INR")
            `object`.put("amount", amount)
            `object`.put("order_id", order_id)
            // put mobile number
            //    object.put("prefill.contact", SessionSave.getSession("Phone",requireContext()));
            `object`.put("prefill.contact", "")

            // put email
            `object`.put("prefill.email", "")

            // open razorpay to checkout activity
            checkout.open(this@DriverWalletRechargeActivity, `object`)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(s: String?, paymentData: PaymentData?) {
        println("Payment_success" + " " + "3")
        println("Payment_success" + " " + s.toString())
        println("Payment_success" + " " + paymentData!!.getPaymentId().toString())
        println("Payment_success" + " " + paymentData!!.getOrderId().toString())

        SessionSave.saveSession(
            "razepay_orderId",
            paymentData!!.getPaymentId().toString(),
            this@DriverWalletRechargeActivity
        )


        try {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@DriverWalletRechargeActivity))
            j.put("transaction_id", paymentData!!.getPaymentId().toString())
            j.put("recharge_amount", addmoneyEdt.text.toString())
            val url = "type=add_driver_wallet"
            rechargeAmount(url, j)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {

    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }

    inner class rechargeAmount internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                println("homeactivity"+" "+"6")

                if (NetworkStatus.isOnline(this@DriverWalletRechargeActivity)) {
                    APIService_Retrofit_JSON(
                        this@DriverWalletRechargeActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@DriverWalletRechargeActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@DriverWalletRechargeActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess && result != null) {
                try {

                    println("homeactivity"+" "+"4")

                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {


                        try {
                            val json = JSONObject(result)

                            if (json.getInt("status") == 1) {

                                walletbalTxt.setText( json.getString("account_balance"))

                                Utils.alert_view(
                                    this@DriverWalletRechargeActivity,
                                    NC.getString(R.string.message),
                                    json.getString("message"),
                                    NC.getString(R.string.ok),
                                    "",
                                    true,
                                    this@DriverWalletRechargeActivity,
                                    "4"
                                )                            }
                        } catch (e: JSONException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }

                    } else {

                    }

                } catch (e: JSONException) {
                    throw java.lang.RuntimeException(e)
                }
            } else {
                //CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }
}