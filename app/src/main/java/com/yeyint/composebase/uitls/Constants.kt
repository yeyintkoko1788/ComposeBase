package com.yeyint.composebase.uitls

import android.Manifest

val SOME_CONSTANTS = "some_constants"
const val LOCALE_MM = "my"
const val LOCALE_EN = "en"
const val LOCALE_CN = "zh"
val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
const val PERMISSION_REQUEST_CODE_CAMERA = 101
const val SERVER_IMAGE_URL = ""
const val VALUE_MAP_ZOOM = 15f
const val HOME_VALUE_MAP_ZOOM = 18f
const val VALUE_MAP_TILE_COUNT = 20f

//header type
const val CATEGORY = "category"
const val FAVOURITE = "favourite"
const val NEARBY = "nearby"
const val RESTAURANT = "restaurant"
const val FOOD = "food"


//social type
const val PUBLIC = "public"
const val PRIVATE = "private"

//User type
const val ADMIN_USER = "admin_user"

//room type
const val CHANNEL = "CHANNEL"
const val GROUP = "GROUP"
const val TOPIC = "TOPIC"

//noti type
const val NOTIFICATION_REPORT = "REPORT"
const val NOTIFICATION_BAN = "BAN"
const val NOTIFICATION_MESSAGE = "MESSAGE"
const val NOTIFICATION_COMMENT = "COMMENT"

//woke manager
const val IMAGE_UPLOAD = "ImageUpload"
const val VIDEO_UPLOAD = "VideoUpload"
const val FILE_UPLOAD = "FileUpload"
const val IMAGE = "image"
const val VIDEO = "video"
const val FILE = "file"
const val TEXT = "text"
const val STICKER = "sticker"
const val LAST_SEEN_UPLOAD = "LastSeenUpload"
const val NOTIFICATION_SETTING = "NotificationSetting"

//Search type
const val SEARCH_ALL = "all"
const val SEARCH_CHANNEL = "channel"
const val SEARCH_GROUP = "group"
const val SEARCH_PHOTO = "photo"
const val SEARCH_VIDEO = "video"
const val SEARCH_FILE = "file"
const val SEARCH_LINK = "link"

//Notification types
const val NOTIFICATION_ALL = "All"
const val NOTIFICATION_PROMOTION = "Promotion"
const val NOTIFICATION_SYSTEM = "System"

//Order types
const val ACTIVE_ORDER = "Active"
const val COMPLETED_ORDER = "Completed"
const val CANCELLED_ORDER = "Cancelled"


enum class CONNECTION_STATUS(val label : String){
    CONNECTED("connected"),
    DISCONNECTED("disconnected"),
    CONNECTING("connecting")
}

enum class RESTAURANT_FILTER(val label: String) {
    DISCOUNT("discount"),
    NEAREST("nearest"),
    RECOMMEND("recommend"),
    POPULARITY("popularity")
}

enum class NOTIFICATION_TYPE(val label: String) {
    UPDATE("update"),
    PROMOTION("promotion"),
    SYSTEM("system")
}

enum class ORDER_STATYS_TYPE(val label : String){
    ACTIVE("active"),
    COMPLETED("complete"),
    CANCELLED("cancel")
}

enum class ORDER_TYPE(val label : String){
    DELIVERY("delivery"),
    PICKUP("pickup")
}

enum class SERIVICE_TYPE(val label: String) {
    DELIVERY("delivery"),
    PICKUP("pickup"),
    BOTH("both")
}

enum class DISCOUNT_TYPE(val label : String) {
    PERCENTAGE("percentage"),
    FIXED("fixed"),
    FREE_DELIVERY("free_delivery")
}
enum class VERIFY_STATE {
    DEFAULT,
    REGISTER,
    LOGIN,
    DELETE_ACCOUNT
}

enum class ORDER_STATUS(val label : String){
    PENDING("Pending"),
    PREPARING("Preparing"),
    ON_THE_WAY("On The Way"),
    READY_TO_PICKUP("Ready To Pick"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    REORDERED("Reordered")
}


enum class NOTIFICATION_STATUS(val label: String) {
    MUTED("muted"),
    DEFAULT("default")
}
enum class MESSAGE_TYPE(val label : String) {
    TEXT_MESSAGE("MESSAGE"),
    PHOTO_MESSAGE("photo message")
}

enum class APP_PHOTO_TYPE(val label : String) {
    SPLASH("splash_screen"),
    ONBOARDING("onboarding_screen"),
    WELCOME("welcome_screen")
}

enum class LOGIN_TYPE{
    PHONE,
    EMAIL
}

enum class CONTACT_INFO(val label : String){
    PHONE("Contact Phone"),
    WHATSAPP("Whatsapp"),
    FACEBOOK("Facebook"),
    TELEGRAM("Telegram"),
    LINE("Line"),
    VIBER("Viber"),
    UNKNOWN("Website")
}