package com.mayan.sospluginmodlue.model

class ApiRequestData {
    data class getEmergencyRequestData(var user_type: String, var user_id: Int, var company_id: String)


    data class AddContactRequestData(var user_id: Int, var user_type: String,
                                     var contact_number: String, var contact_name: String,
                                     var country_code: String, var company_id: String)

    data class DeleteContactRequestData(var user_id: Int, var user_type: String,
                                        var contact_id: Int, var company_id: String)

    data class EmergencyRequestData(var user_id: Int, var user_type: String, var trip_id:Int ,
                                    var latitude: String, var longitude: String, var location: String,
                                    var company_id: String)
    data class StandardResponse(var status: Int, var message: String, var company_id: String)

    data class callCheckCompanyDomain(var company_domain : String,var company_main_domain:String, var device_type:String)

}
