package com.mayan.sospluginmodlue.service

import android.content.Context
import com.mayan.sospluginmodlue.R
import com.mayan.sospluginmodlue.model.ApiRequestData
import com.mayan.sospluginmodlue.model.CompanyDomainResponse
import com.mayan.sospluginmodlue.util.CToast
import com.mayan.sospluginmodlue.util.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckCompanyDomain {

    fun callCheckCompanyDomain(context: Context) {
        val request = ApiRequestData.callCheckCompanyDomain(SessionSave.getSession("access_keyu", context), SessionSave.getSession("company_main_domain", context), "1")
        val client = ServiceGenerator(context, SessionSave.getSession("domain_url", context)).createService(CoreClient::class.java)
        val response = client.callData(request)

        response.enqueue(object : Callback<CompanyDomainResponse> {
            override fun onResponse(call: Call<CompanyDomainResponse>, response: Response<CompanyDomainResponse>) {
                try {
                    if (response.isSuccessful) {
                        val cr = response.body()
                        if (cr!!.auth_key != null && cr.auth_key != "") {
                            SessionSave.saveSession("auth_key", cr.auth_key, context)
                        } else {
//                            CToast.ShowToast(context, context.resources.getString(R.string.server_error))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<CompanyDomainResponse>, t: Throwable) {

                t.printStackTrace()
            }

        })

    }
}