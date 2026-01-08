package com.mayan.sospluginmodlue.model

data class ContactsData(var contact_name: String, var contact_number: String,
                        var country_code: String? = null,
                        var contact_id: Int? = null)