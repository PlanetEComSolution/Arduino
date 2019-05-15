package com.pombingsoft.planet_iot.util;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pombingsoft.planet_iot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class test extends AppCompatActivity {
    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        getTimeZoneJSON();

    }


    private void getTimeZoneJSON() {
        list.clear();
        //https://timezonedb.com/time-zones
        String data = "AF\tAfghanistan\tAsia/Kabul\tGMT +04:30" +"\t"+
                "AX\tAland Islands\tEurope/Mariehamn\tGMT +03:00" +"\t"+
                "AL\tAlbania\tEurope/Tirane\tGMT +02:00" +"\t"+
                "DZ\tAlgeria\tAfrica/Algiers\tGMT +01:00" +"\t"+
                "AS\tAmerican Samoa\tPacific/Pago_Pago\tGMT -11:00" +"\t"+
                "AD\tAndorra\tEurope/Andorra\tGMT +02:00" +"\t"+
                "AO\tAngola\tAfrica/Luanda\tGMT +01:00" +"\t"+
                "AI\tAnguilla\tAmerica/Anguilla\tGMT -04:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Casey\tGMT +08:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Davis\tGMT +07:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/DumontDUrville\tGMT +10:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Mawson\tGMT +05:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/McMurdo\tGMT +12:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Palmer\tGMT -03:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Rothera\tGMT -03:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Syowa\tGMT +03:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Troll\tGMT +02:00" +"\t"+
                "AQ\tAntarctica\tAntarctica/Vostok\tGMT +06:00" +"\t"+
                "AG\tAntigua and Barbuda\tAmerica/Antigua\tGMT -04:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Buenos_Aires\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Catamarca\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Cordoba\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Jujuy\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/La_Rioja\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Mendoza\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Rio_Gallegos\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Salta\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/San_Juan\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/San_Luis\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Tucuman\tGMT -03:00" +"\t"+
                "AR\tArgentina\tAmerica/Argentina/Ushuaia\tGMT -03:00" +"\t"+
                "AM\tArmenia\tAsia/Yerevan\tGMT +04:00" +"\t"+
                "AW\tAruba\tAmerica/Aruba\tGMT -04:00" +"\t"+
                "AU\tAustralia\tAntarctica/Macquarie\tGMT +11:00" +"\t"+
                "AU\tAustralia\tAustralia/Adelaide\tGMT +09:30" +"\t"+
                "AU\tAustralia\tAustralia/Brisbane\tGMT +10:00" +"\t"+
                "AU\tAustralia\tAustralia/Broken_Hill\tGMT +09:30" +"\t"+
                "AU\tAustralia\tAustralia/Currie\tGMT +10:00" +"\t"+
                "AU\tAustralia\tAustralia/Darwin\tGMT +09:30" +"\t"+
                "AU\tAustralia\tAustralia/Eucla\tGMT +08:45" +"\t"+
                "AU\tAustralia\tAustralia/Hobart\tGMT +10:00" +"\t"+
                "AU\tAustralia\tAustralia/Lindeman\tGMT +10:00" +"\t"+
                "AU\tAustralia\tAustralia/Lord_Howe\tGMT +10:30" +"\t"+
                "AU\tAustralia\tAustralia/Melbourne\tGMT +10:00" +"\t"+
                "AU\tAustralia\tAustralia/Perth\tGMT +08:00" +"\t"+
                "AU\tAustralia\tAustralia/Sydney\tGMT +10:00" +"\t"+
                "AT\tAustria\tEurope/Vienna\tGMT +02:00" +"\t"+
                "AZ\tAzerbaijan\tAsia/Baku\tGMT +04:00" +"\t"+
                "BS\tBahamas\tAmerica/Nassau\tGMT -04:00" +"\t"+
                "BH\tBahrain\tAsia/Bahrain\tGMT +03:00" +"\t"+
                "BD\tBangladesh\tAsia/Dhaka\tGMT +06:00" +"\t"+
                "BB\tBarbados\tAmerica/Barbados\tGMT -04:00" +"\t"+
                "BY\tBelarus\tEurope/Minsk\tGMT +03:00" +"\t"+
                "BE\tBelgium\tEurope/Brussels\tGMT +02:00" +"\t"+
                "BZ\tBelize\tAmerica/Belize\tGMT -06:00" +"\t"+
                "BJ\tBenin\tAfrica/Porto-Novo\tGMT +01:00" +"\t"+
                "BM\tBermuda\tAtlantic/Bermuda\tGMT -03:00" +"\t"+
                "BT\tBhutan\tAsia/Thimphu\tGMT +06:00" +"\t"+
                "BO\tBolivia\tAmerica/La_Paz\tGMT -04:00" +"\t"+
                "BQ\tBonaire, Saint Eustatius and Saba\tAmerica/Kralendijk\tGMT -04:00" +"\t"+
                "BA\tBosnia and Herzegovina\tEurope/Sarajevo\tGMT +02:00" +"\t"+
                "BW\tBotswana\tAfrica/Gaborone\tGMT +02:00" +"\t"+
                "BR\tBrazil\tAmerica/Araguaina\tGMT -03:00" +"\t"+
                "BR\tBrazil\tAmerica/Bahia\tGMT -03:00" +"\t"+
                "BR\tBrazil\tAmerica/Belem\tGMT -03:00" +"\t"+
                "BR\tBrazil\tAmerica/Boa_Vista\tGMT -04:00" +"\t"+
                "BR\tBrazil\tAmerica/Campo_Grande\tGMT -04:00" +"\t"+
                "BR\tBrazil\tAmerica/Cuiaba\tGMT -04:00" +"\t"+
                "BR\tBrazil\tAmerica/Eirunepe\tGMT -05:00" +"\t"+
                "BR\tBrazil\tAmerica/Fortaleza\tGMT -03:00" +"\t"+
                "BR\tBrazil\tAmerica/Maceio\tGMT -03:00" +"\t"+
                "BR\tBrazil\tAmerica/Manaus\tGMT -04:00" +"\t"+
                "BR\tBrazil\tAmerica/Noronha\tGMT -02:00" +"\t"+
                "BR\tBrazil\tAmerica/Porto_Velho\tGMT -04:00" +"\t"+
                "BR\tBrazil\tAmerica/Recife\tGMT -03:00" +"\t"+
                "BR\tBrazil\tAmerica/Rio_Branco\tGMT -05:00" +"\t"+
                "BR\tBrazil\tAmerica/Santarem\tGMT -03:00" +"\t"+
                "BR\tBrazil\tAmerica/Sao_Paulo\tGMT -03:00" +"\t"+
                "IO\tBritish Indian Ocean Territory\tIndian/Chagos\tGMT +06:00" +"\t"+
                "VG\tBritish Virgin Islands\tAmerica/Tortola\tGMT -04:00" +"\t"+
                "BN\tBrunei\tAsia/Brunei\tGMT +08:00" +"\t"+
                "BG\tBulgaria\tEurope/Sofia\tGMT +03:00" +"\t"+
                "BF\tBurkina Faso\tAfrica/Ouagadougou\tGMT" +"\t"+
                "BI\tBurundi\tAfrica/Bujumbura\tGMT +02:00" +"\t"+
                "KH\tCambodia\tAsia/Phnom_Penh\tGMT +07:00" +"\t"+
                "CM\tCameroon\tAfrica/Douala\tGMT +01:00" +"\t"+
                "CA\tCanada\tAmerica/Atikokan\tGMT -05:00" +"\t"+
                "CA\tCanada\tAmerica/Blanc-Sablon\tGMT -04:00" +"\t"+
                "CA\tCanada\tAmerica/Cambridge_Bay\tGMT -06:00" +"\t"+
                "CA\tCanada\tAmerica/Creston\tGMT -07:00" +"\t"+
                "CA\tCanada\tAmerica/Dawson\tGMT -07:00" +"\t"+
                "CA\tCanada\tAmerica/Dawson_Creek\tGMT -07:00" +"\t"+
                "CA\tCanada\tAmerica/Edmonton\tGMT -06:00" +"\t"+
                "CA\tCanada\tAmerica/Fort_Nelson\tGMT -07:00" +"\t"+
                "CA\tCanada\tAmerica/Glace_Bay\tGMT -03:00" +"\t"+
                "CA\tCanada\tAmerica/Goose_Bay\tGMT -03:00" +"\t"+
                "CA\tCanada\tAmerica/Halifax\tGMT -03:00" +"\t"+
                "CA\tCanada\tAmerica/Inuvik\tGMT -06:00" +"\t"+
                "CA\tCanada\tAmerica/Iqaluit\tGMT -04:00" +"\t"+
                "CA\tCanada\tAmerica/Moncton\tGMT -03:00" +"\t"+
                "CA\tCanada\tAmerica/Nipigon\tGMT -04:00" +"\t"+
                "CA\tCanada\tAmerica/Pangnirtung\tGMT -04:00" +"\t"+
                "CA\tCanada\tAmerica/Rainy_River\tGMT -05:00" +"\t"+
                "CA\tCanada\tAmerica/Rankin_Inlet\tGMT -05:00" +"\t"+
                "CA\tCanada\tAmerica/Regina\tGMT -06:00" +"\t"+
                "CA\tCanada\tAmerica/Resolute\tGMT -05:00" +"\t"+
                "CA\tCanada\tAmerica/St_Johns\tGMT -02:30" +"\t"+
                "CA\tCanada\tAmerica/Swift_Current\tGMT -06:00" +"\t"+
                "CA\tCanada\tAmerica/Thunder_Bay\tGMT -04:00" +"\t"+
                "CA\tCanada\tAmerica/Toronto\tGMT -04:00" +"\t"+
                "CA\tCanada\tAmerica/Vancouver\tGMT -07:00" +"\t"+
                "CA\tCanada\tAmerica/Whitehorse\tGMT -07:00" +"\t"+
                "CA\tCanada\tAmerica/Winnipeg\tGMT -05:00" +"\t"+
                "CA\tCanada\tAmerica/Yellowknife\tGMT -06:00" +"\t"+
                "CV\tCape Verde\tAtlantic/Cape_Verde\tGMT -01:00" +"\t"+
                "KY\tCayman Islands\tAmerica/Cayman\tGMT -05:00" +"\t"+
                "CF\tCentral African Republic\tAfrica/Bangui\tGMT +01:00" +"\t"+
                "TD\tChad\tAfrica/Ndjamena\tGMT +01:00" +"\t"+
                "CL\tChile\tAmerica/Punta_Arenas\tGMT -03:00" +"\t"+
                "CL\tChile\tAmerica/Santiago\tGMT -03:00" +"\t"+
                "CL\tChile\tPacific/Easter\tGMT -05:00" +"\t"+
                "CN\tChina\tAsia/Shanghai\tGMT +08:00" +"\t"+
                "CN\tChina\tAsia/Urumqi\tGMT +06:00" +"\t"+
                "CX\tChristmas Island\tIndian/Christmas\tGMT +07:00" +"\t"+
                "CC\tCocos Islands\tIndian/Cocos\tGMT +06:30" +"\t"+
                "CO\tColombia\tAmerica/Bogota\tGMT -05:00" +"\t"+
                "KM\tComoros\tIndian/Comoro\tGMT +03:00" +"\t"+
                "CK\tCook Islands\tPacific/Rarotonga\tGMT -10:00" +"\t"+
                "CR\tCosta Rica\tAmerica/Costa_Rica\tGMT -06:00" +"\t"+
                "HR\tCroatia\tEurope/Zagreb\tGMT +02:00" +"\t"+
                "CU\tCuba\tAmerica/Havana\tGMT -04:00" +"\t"+
                "CW\tCuraçao\tAmerica/Curacao\tGMT -04:00" +"\t"+
                "CY\tCyprus\tAsia/Famagusta\tGMT +03:00" +"\t"+
                "CY\tCyprus\tAsia/Nicosia\tGMT +03:00" +"\t"+
                "CZ\tCzech Republic\tEurope/Prague\tGMT +02:00" +"\t"+
                "CD\tDemocratic Republic of the Congo\tAfrica/Kinshasa\tGMT +01:00" +"\t"+
                "CD\tDemocratic Republic of the Congo\tAfrica/Lubumbashi\tGMT +02:00" +"\t"+
                "DK\tDenmark\tEurope/Copenhagen\tGMT +02:00" +"\t"+
                "DJ\tDjibouti\tAfrica/Djibouti\tGMT +03:00" +"\t"+
                "DM\tDominica\tAmerica/Dominica\tGMT -04:00" +"\t"+
                "DO\tDominican Republic\tAmerica/Santo_Domingo\tGMT -04:00" +"\t"+
                "TL\tEast Timor\tAsia/Dili\tGMT +09:00" +"\t"+
                "EC\tEcuador\tAmerica/Guayaquil\tGMT -05:00" +"\t"+
                "EC\tEcuador\tPacific/Galapagos\tGMT -06:00" +"\t"+
                "EG\tEgypt\tAfrica/Cairo\tGMT +02:00" +"\t"+
                "SV\tEl Salvador\tAmerica/El_Salvador\tGMT -06:00" +"\t"+
                "GQ\tEquatorial Guinea\tAfrica/Malabo\tGMT +01:00" +"\t"+
                "ER\tEritrea\tAfrica/Asmara\tGMT +03:00" +"\t"+
                "EE\tEstonia\tEurope/Tallinn\tGMT +03:00" +"\t"+
                "ET\tEthiopia\tAfrica/Addis_Ababa\tGMT +03:00" +"\t"+
                "FK\tFalkland Islands\tAtlantic/Stanley\tGMT -03:00" +"\t"+
                "FO\tFaroe Islands\tAtlantic/Faroe\tGMT +01:00" +"\t"+
                "FJ\tFiji\tPacific/Fiji\tGMT +12:00" +"\t"+
                "FI\tFinland\tEurope/Helsinki\tGMT +03:00" +"\t"+
                "FR\tFrance\tEurope/Paris\tGMT +02:00" +"\t"+
                "GF\tFrench Guiana\tAmerica/Cayenne\tGMT -03:00" +"\t"+
                "PF\tFrench Polynesia\tPacific/Gambier\tGMT -09:00" +"\t"+
                "PF\tFrench Polynesia\tPacific/Marquesas\tGMT -09:30" +"\t"+
                "PF\tFrench Polynesia\tPacific/Tahiti\tGMT -10:00" +"\t"+
                "TF\tFrench Southern Territories\tIndian/Kerguelen\tGMT +05:00" +"\t"+
                "GA\tGabon\tAfrica/Libreville\tGMT +01:00" +"\t"+
                "GM\tGambia\tAfrica/Banjul\tGMT" +"\t"+
                "GE\tGeorgia\tAsia/Tbilisi\tGMT +04:00" +"\t"+
                "DE\tGermany\tEurope/Berlin\tGMT +02:00" +"\t"+
                "DE\tGermany\tEurope/Busingen\tGMT +02:00" +"\t"+
                "GH\tGhana\tAfrica/Accra\tGMT" +"\t"+
                "GI\tGibraltar\tEurope/Gibraltar\tGMT +02:00" +"\t"+
                "GR\tGreece\tEurope/Athens\tGMT +03:00" +"\t"+
                "GL\tGreenland\tAmerica/Danmarkshavn\tGMT" +"\t"+
                "GL\tGreenland\tAmerica/Godthab\tGMT -02:00" +"\t"+
                "GL\tGreenland\tAmerica/Scoresbysund\tGMT" +"\t"+
                "GL\tGreenland\tAmerica/Thule\tGMT -03:00" +"\t"+
                "GD\tGrenada\tAmerica/Grenada\tGMT -04:00" +"\t"+
                "GP\tGuadeloupe\tAmerica/Guadeloupe\tGMT -04:00" +"\t"+
                "GU\tGuam\tPacific/Guam\tGMT +10:00" +"\t"+
                "GT\tGuatemala\tAmerica/Guatemala\tGMT -06:00" +"\t"+
                "GG\tGuernsey\tEurope/Guernsey\tGMT +01:00" +"\t"+
                "GN\tGuinea\tAfrica/Conakry\tGMT" +"\t"+
                "GW\tGuinea-Bissau\tAfrica/Bissau\tGMT" +"\t"+
                "GY\tGuyana\tAmerica/Guyana\tGMT -04:00" +"\t"+
                "HT\tHaiti\tAmerica/Port-au-Prince\tGMT -04:00" +"\t"+
                "HN\tHonduras\tAmerica/Tegucigalpa\tGMT -06:00" +"\t"+
                "HK\tHong Kong\tAsia/Hong_Kong\tGMT +08:00" +"\t"+
                "HU\tHungary\tEurope/Budapest\tGMT +02:00" +"\t"+
                "IS\tIceland\tAtlantic/Reykjavik\tGMT" +"\t"+
                "IN\tIndia\tAsia/Kolkata\tGMT +05:30" +"\t"+
                "ID\tIndonesia\tAsia/Jakarta\tGMT +07:00" +"\t"+
                "ID\tIndonesia\tAsia/Jayapura\tGMT +09:00" +"\t"+
                "ID\tIndonesia\tAsia/Makassar\tGMT +08:00" +"\t"+
                "ID\tIndonesia\tAsia/Pontianak\tGMT +07:00" +"\t"+
                "IR\tIran\tAsia/Tehran\tGMT +04:30" +"\t"+
                "IQ\tIraq\tAsia/Baghdad\tGMT +03:00" +"\t"+
                "IE\tIreland\tEurope/Dublin\tGMT +01:00" +"\t"+
                "IM\tIsle of Man\tEurope/Isle_of_Man\tGMT +01:00" +"\t"+
                "IL\tIsrael\tAsia/Jerusalem\tGMT +03:00" +"\t"+
                "IT\tItaly\tEurope/Rome\tGMT +02:00" +"\t"+
                "CI\tIvory Coast\tAfrica/Abidjan\tGMT" +"\t"+
                "JM\tJamaica\tAmerica/Jamaica\tGMT -05:00" +"\t"+
                "JP\tJapan\tAsia/Tokyo\tGMT +09:00" +"\t"+
                "JE\tJersey\tEurope/Jersey\tGMT +01:00" +"\t"+
                "JO\tJordan\tAsia/Amman\tGMT +03:00" +"\t"+
                "KZ\tKazakhstan\tAsia/Almaty\tGMT +06:00" +"\t"+
                "KZ\tKazakhstan\tAsia/Aqtau\tGMT +05:00" +"\t"+
                "KZ\tKazakhstan\tAsia/Aqtobe\tGMT +05:00" +"\t"+
                "KZ\tKazakhstan\tAsia/Atyrau\tGMT +05:00" +"\t"+
                "KZ\tKazakhstan\tAsia/Oral\tGMT +05:00" +"\t"+
                "KZ\tKazakhstan\tAsia/Qyzylorda\tGMT +06:00" +"\t"+
                "KE\tKenya\tAfrica/Nairobi\tGMT +03:00" +"\t"+
                "KI\tKiribati\tPacific/Enderbury\tGMT +13:00" +"\t"+
                "KI\tKiribati\tPacific/Kiritimati\tGMT +14:00" +"\t"+
                "KI\tKiribati\tPacific/Tarawa\tGMT +12:00" +"\t"+
                "KW\tKuwait\tAsia/Kuwait\tGMT +03:00" +"\t"+
                "KG\tKyrgyzstan\tAsia/Bishkek\tGMT +06:00" +"\t"+
                "LA\tLaos\tAsia/Vientiane\tGMT +07:00" +"\t"+
                "LV\tLatvia\tEurope/Riga\tGMT +03:00" +"\t"+
                "LB\tLebanon\tAsia/Beirut\tGMT +03:00" +"\t"+
                "LS\tLesotho\tAfrica/Maseru\tGMT +02:00" +"\t"+
                "LR\tLiberia\tAfrica/Monrovia\tGMT" +"\t"+
                "LY\tLibya\tAfrica/Tripoli\tGMT +02:00" +"\t"+
                "LI\tLiechtenstein\tEurope/Vaduz\tGMT +02:00" +"\t"+
                "LT\tLithuania\tEurope/Vilnius\tGMT +03:00" +"\t"+
                "LU\tLuxembourg\tEurope/Luxembourg\tGMT +02:00" +"\t"+
                "MO\tMacao\tAsia/Macau\tGMT +08:00" +"\t"+
                "MK\tMacedonia\tEurope/Skopje\tGMT +02:00" +"\t"+
                "MG\tMadagascar\tIndian/Antananarivo\tGMT +03:00" +"\t"+
                "MW\tMalawi\tAfrica/Blantyre\tGMT +02:00" +"\t"+
                "MY\tMalaysia\tAsia/Kuala_Lumpur\tGMT +08:00" +"\t"+
                "MY\tMalaysia\tAsia/Kuching\tGMT +08:00" +"\t"+
                "MV\tMaldives\tIndian/Maldives\tGMT +05:00" +"\t"+
                "ML\tMali\tAfrica/Bamako\tGMT" +"\t"+
                "MT\tMalta\tEurope/Malta\tGMT +02:00" +"\t"+
                "MH\tMarshall Islands\tPacific/Kwajalein\tGMT +12:00" +"\t"+
                "MH\tMarshall Islands\tPacific/Majuro\tGMT +12:00" +"\t"+
                "MQ\tMartinique\tAmerica/Martinique\tGMT -04:00" +"\t"+
                "MR\tMauritania\tAfrica/Nouakchott\tGMT" +"\t"+
                "MU\tMauritius\tIndian/Mauritius\tGMT +04:00" +"\t"+
                "YT\tMayotte\tIndian/Mayotte\tGMT +03:00" +"\t"+
                "MX\tMexico\tAmerica/Bahia_Banderas\tGMT -05:00" +"\t"+
                "MX\tMexico\tAmerica/Cancun\tGMT -05:00" +"\t"+
                "MX\tMexico\tAmerica/Chihuahua\tGMT -06:00" +"\t"+
                "MX\tMexico\tAmerica/Hermosillo\tGMT -07:00" +"\t"+
                "MX\tMexico\tAmerica/Matamoros\tGMT -05:00" +"\t"+
                "MX\tMexico\tAmerica/Mazatlan\tGMT -06:00" +"\t"+
                "MX\tMexico\tAmerica/Merida\tGMT -05:00" +"\t"+
                "MX\tMexico\tAmerica/Mexico_City\tGMT -05:00" +"\t"+
                "MX\tMexico\tAmerica/Monterrey\tGMT -05:00" +"\t"+
                "MX\tMexico\tAmerica/Ojinaga\tGMT -06:00" +"\t"+
                "MX\tMexico\tAmerica/Tijuana\tGMT -07:00" +"\t"+
                "FM\tMicronesia\tPacific/Chuuk\tGMT +10:00" +"\t"+
                "FM\tMicronesia\tPacific/Kosrae\tGMT +11:00" +"\t"+
                "FM\tMicronesia\tPacific/Pohnpei\tGMT +11:00" +"\t"+
                "MD\tMoldova\tEurope/Chisinau\tGMT +03:00" +"\t"+
                "MC\tMonaco\tEurope/Monaco\tGMT +02:00" +"\t"+
                "MN\tMongolia\tAsia/Choibalsan\tGMT +08:00" +"\t"+
                "MN\tMongolia\tAsia/Hovd\tGMT +07:00" +"\t"+
                "MN\tMongolia\tAsia/Ulaanbaatar\tGMT +08:00" +"\t"+
                "ME\tMontenegro\tEurope/Podgorica\tGMT +02:00" +"\t"+
                "MS\tMontserrat\tAmerica/Montserrat\tGMT -04:00" +"\t"+
                "MA\tMorocco\tAfrica/Casablanca\tGMT +01:00" +"\t"+
                "MZ\tMozambique\tAfrica/Maputo\tGMT +02:00" +"\t"+
                "MM\tMyanmar\tAsia/Yangon\tGMT +06:30" +"\t"+
                "NA\tNamibia\tAfrica/Windhoek\tGMT +02:00" +"\t"+
                "NR\tNauru\tPacific/Nauru\tGMT +12:00" +"\t"+
                "NP\tNepal\tAsia/Kathmandu\tGMT +05:45" +"\t"+
                "NL\tNetherlands\tEurope/Amsterdam\tGMT +02:00" +"\t"+
                "NC\tNew Caledonia\tPacific/Noumea\tGMT +11:00" +"\t"+
                "NZ\tNew Zealand\tPacific/Auckland\tGMT +12:00" +"\t"+
                "NZ\tNew Zealand\tPacific/Chatham\tGMT +12:45" +"\t"+
                "NI\tNicaragua\tAmerica/Managua\tGMT -06:00" +"\t"+
                "NE\tNiger\tAfrica/Niamey\tGMT +01:00" +"\t"+
                "NG\tNigeria\tAfrica/Lagos\tGMT +01:00" +"\t"+
                "NU\tNiue\tPacific/Niue\tGMT -11:00" +"\t"+
                "NF\tNorfolk Island\tPacific/Norfolk\tGMT +11:00" +"\t"+
                "KP\tNorth Korea\tAsia/Pyongyang\tGMT +08:30" +"\t"+
                "MP\tNorthern Mariana Islands\tPacific/Saipan\tGMT +10:00" +"\t"+
                "NO\tNorway\tEurope/Oslo\tGMT +02:00" +"\t"+
                "OM\tOman\tAsia/Muscat\tGMT +04:00" +"\t"+
                "PK\tPakistan\tAsia/Karachi\tGMT +05:00" +"\t"+
                "PW\tPalau\tPacific/Palau\tGMT +09:00" +"\t"+
                "PS\tPalestinian Territory\tAsia/Gaza\tGMT +03:00" +"\t"+
                "PS\tPalestinian Territory\tAsia/Hebron\tGMT +03:00" +"\t"+
                "PA\tPanama\tAmerica/Panama\tGMT -05:00" +"\t"+
                "PG\tPapua New Guinea\tPacific/Bougainville\tGMT +11:00" +"\t"+
                "PG\tPapua New Guinea\tPacific/Port_Moresby\tGMT +10:00" +"\t"+
                "PY\tParaguay\tAmerica/Asuncion\tGMT -04:00" +"\t"+
                "PE\tPeru\tAmerica/Lima\tGMT -05:00" +"\t"+
                "PH\tPhilippines\tAsia/Manila\tGMT +08:00" +"\t"+
                "PN\tPitcairn\tPacific/Pitcairn\tGMT -08:00" +"\t"+
                "PL\tPoland\tEurope/Warsaw\tGMT +02:00" +"\t"+
                "PT\tPortugal\tAtlantic/Azores\tGMT" +"\t"+
                "PT\tPortugal\tAtlantic/Madeira\tGMT +01:00" +"\t"+
                "PT\tPortugal\tEurope/Lisbon\tGMT +01:00" +"\t"+
                "PR\tPuerto Rico\tAmerica/Puerto_Rico\tGMT -04:00" +"\t"+
                "QA\tQatar\tAsia/Qatar\tGMT +03:00" +"\t"+
                "CG\tRepublic of the Congo\tAfrica/Brazzaville\tGMT +01:00" +"\t"+
                "RE\tReunion\tIndian/Reunion\tGMT +04:00" +"\t"+
                "RO\tRomania\tEurope/Bucharest\tGMT +03:00" +"\t"+
                "RU\tRussia\tAsia/Anadyr\tGMT +12:00" +"\t"+
                "RU\tRussia\tAsia/Barnaul\tGMT +07:00" +"\t"+
                "RU\tRussia\tAsia/Chita\tGMT +09:00" +"\t"+
                "RU\tRussia\tAsia/Irkutsk\tGMT +08:00" +"\t"+
                "RU\tRussia\tAsia/Kamchatka\tGMT +12:00" +"\t"+
                "RU\tRussia\tAsia/Khandyga\tGMT +09:00" +"\t"+
                "RU\tRussia\tAsia/Krasnoyarsk\tGMT +07:00" +"\t"+
                "RU\tRussia\tAsia/Magadan\tGMT +11:00" +"\t"+
                "RU\tRussia\tAsia/Novokuznetsk\tGMT +07:00" +"\t"+
                "RU\tRussia\tAsia/Novosibirsk\tGMT +07:00" +"\t"+
                "RU\tRussia\tAsia/Omsk\tGMT +06:00" +"\t"+
                "RU\tRussia\tAsia/Sakhalin\tGMT +11:00" +"\t"+
                "RU\tRussia\tAsia/Srednekolymsk\tGMT +11:00" +"\t"+
                "RU\tRussia\tAsia/Tomsk\tGMT +07:00" +"\t"+
                "RU\tRussia\tAsia/Ust-Nera\tGMT +10:00" +"\t"+
                "RU\tRussia\tAsia/Vladivostok\tGMT +10:00" +"\t"+
                "RU\tRussia\tAsia/Yakutsk\tGMT +09:00" +"\t"+
                "RU\tRussia\tAsia/Yekaterinburg\tGMT +05:00" +"\t"+
                "RU\tRussia\tEurope/Astrakhan\tGMT +04:00" +"\t"+
                "RU\tRussia\tEurope/Kaliningrad\tGMT +02:00" +"\t"+
                "RU\tRussia\tEurope/Kirov\tGMT +03:00" +"\t"+
                "RU\tRussia\tEurope/Moscow\tGMT +03:00" +"\t"+
                "RU\tRussia\tEurope/Samara\tGMT +04:00" +"\t"+
                "RU\tRussia\tEurope/Saratov\tGMT +04:00" +"\t"+
                "RU\tRussia\tEurope/Simferopol\tGMT +03:00" +"\t"+
                "RU\tRussia\tEurope/Ulyanovsk\tGMT +04:00" +"\t"+
                "RU\tRussia\tEurope/Volgograd\tGMT +03:00" +"\t"+
                "RW\tRwanda\tAfrica/Kigali\tGMT +02:00" +"\t"+
                "BL\tSaint Barthélemy\tAmerica/St_Barthelemy\tGMT -04:00" +"\t"+
                "SH\tSaint Helena\tAtlantic/St_Helena\tGMT" +"\t"+
                "KN\tSaint Kitts and Nevis\tAmerica/St_Kitts\tGMT -04:00" +"\t"+
                "LC\tSaint Lucia\tAmerica/St_Lucia\tGMT -04:00" +"\t"+
                "MF\tSaint Martin\tAmerica/Marigot\tGMT -04:00" +"\t"+
                "PM\tSaint Pierre and Miquelon\tAmerica/Miquelon\tGMT -02:00" +"\t"+
                "VC\tSaint Vincent and the Grenadines\tAmerica/St_Vincent\tGMT -04:00" +"\t"+
                "WS\tSamoa\tPacific/Apia\tGMT +13:00" +"\t"+
                "SM\tSan Marino\tEurope/San_Marino\tGMT +02:00" +"\t"+
                "ST\tSao Tome and Principe\tAfrica/Sao_Tome\tGMT +01:00" +"\t"+
                "SA\tSaudi Arabia\tAsia/Riyadh\tGMT +03:00" +"\t"+
                "SN\tSenegal\tAfrica/Dakar\tGMT" +"\t"+
                "RS\tSerbia\tEurope/Belgrade\tGMT +02:00" +"\t"+
                "SC\tSeychelles\tIndian/Mahe\tGMT +04:00" +"\t"+
                "SL\tSierra Leone\tAfrica/Freetown\tGMT" +"\t"+
                "SG\tSingapore\tAsia/Singapore\tGMT +08:00" +"\t"+
                "SX\tSint Maarten\tAmerica/Lower_Princes\tGMT -04:00" +"\t"+
                "SK\tSlovakia\tEurope/Bratislava\tGMT +02:00" +"\t"+
                "SI\tSlovenia\tEurope/Ljubljana\tGMT +02:00" +"\t"+
                "SB\tSolomon Islands\tPacific/Guadalcanal\tGMT +11:00" +"\t"+
                "SO\tSomalia\tAfrica/Mogadishu\tGMT +03:00" +"\t"+
                "ZA\tSouth Africa\tAfrica/Johannesburg\tGMT +02:00" +"\t"+
                "GS\tSouth Georgia and the South Sandwich Islands\tAtlantic/South_Georgia\tGMT -02:00" +"\t"+
                "KR\tSouth Korea\tAsia/Seoul\tGMT +09:00" +"\t"+
                "SS\tSouth Sudan\tAfrica/Juba\tGMT +03:00" +"\t"+
                "ES\tSpain\tAfrica/Ceuta\tGMT +02:00" +"\t"+
                "ES\tSpain\tAtlantic/Canary\tGMT +01:00" +"\t"+
                "ES\tSpain\tEurope/Madrid\tGMT +02:00" +"\t"+
                "LK\tSri Lanka\tAsia/Colombo\tGMT +05:30" +"\t"+
                "SD\tSudan\tAfrica/Khartoum\tGMT +02:00" +"\t"+
                "SR\tSuriname\tAmerica/Paramaribo\tGMT -03:00" +"\t"+
                "SJ\tSvalbard and Jan Mayen\tArctic/Longyearbyen\tGMT +02:00" +"\t"+
                "SZ\tSwaziland\tAfrica/Mbabane\tGMT +02:00" +"\t"+
                "SE\tSweden\tEurope/Stockholm\tGMT +02:00" +"\t"+
                "CH\tSwitzerland\tEurope/Zurich\tGMT +02:00" +"\t"+
                "SY\tSyria\tAsia/Damascus\tGMT +03:00" +"\t"+
                "TW\tTaiwan\tAsia/Taipei\tGMT +08:00" +"\t"+
                "TJ\tTajikistan\tAsia/Dushanbe\tGMT +05:00" +"\t"+
                "TZ\tTanzania\tAfrica/Dar_es_Salaam\tGMT +03:00" +"\t"+
                "TH\tThailand\tAsia/Bangkok\tGMT +07:00" +"\t"+
                "TG\tTogo\tAfrica/Lome\tGMT" +"\t"+
                "TK\tTokelau\tPacific/Fakaofo\tGMT +13:00" +"\t"+
                "TO\tTonga\tPacific/Tongatapu\tGMT +13:00" +"\t"+
                "TT\tTrinidad and Tobago\tAmerica/Port_of_Spain\tGMT -04:00" +"\t"+
                "TN\tTunisia\tAfrica/Tunis\tGMT +01:00" +"\t"+
                "TR\tTurkey\tEurope/Istanbul\tGMT +03:00" +"\t"+
                "TM\tTurkmenistan\tAsia/Ashgabat\tGMT +05:00" +"\t"+
                "TC\tTurks and Caicos Islands\tAmerica/Grand_Turk\tGMT -04:00" +"\t"+
                "TV\tTuvalu\tPacific/Funafuti\tGMT +12:00" +"\t"+
                "VI\tU.S. Virgin Islands\tAmerica/St_Thomas\tGMT -04:00" +"\t"+
                "UG\tUganda\tAfrica/Kampala\tGMT +03:00" +"\t"+
                "UA\tUkraine\tEurope/Kiev\tGMT +03:00" +"\t"+
                "UA\tUkraine\tEurope/Uzhgorod\tGMT +03:00" +"\t"+
                "UA\tUkraine\tEurope/Zaporozhye\tGMT +03:00" +"\t"+
                "AE\tUnited Arab Emirates\tAsia/Dubai\tGMT +04:00" +"\t"+
                "GB\tUnited Kingdom\tEurope/London\tGMT +01:00" +"\t"+
                "US\tUnited States\tAmerica/Adak\tGMT -09:00" +"\t"+
                "US\tUnited States\tAmerica/Anchorage\tGMT -08:00" +"\t"+
                "US\tUnited States\tAmerica/Boise\tGMT -06:00" +"\t"+
                "US\tUnited States\tAmerica/Chicago\tGMT -05:00" +"\t"+
                "US\tUnited States\tAmerica/Denver\tGMT -06:00" +"\t"+
                "US\tUnited States\tAmerica/Detroit\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Indianapolis\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Knox\tGMT -05:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Marengo\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Petersburg\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Tell_City\tGMT -05:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Vevay\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Vincennes\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Indiana/Winamac\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Juneau\tGMT -08:00" +"\t"+
                "US\tUnited States\tAmerica/Kentucky/Louisville\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Kentucky/Monticello\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Los_Angeles\tGMT -07:00" +"\t"+
                "US\tUnited States\tAmerica/Menominee\tGMT -05:00" +"\t"+
                "US\tUnited States\tAmerica/Metlakatla\tGMT -08:00" +"\t"+
                "US\tUnited States\tAmerica/New_York\tGMT -04:00" +"\t"+
                "US\tUnited States\tAmerica/Nome\tGMT -08:00" +"\t"+
                "US\tUnited States\tAmerica/North_Dakota/Beulah\tGMT -05:00" +"\t"+
                "US\tUnited States\tAmerica/North_Dakota/Center\tGMT -05:00" +"\t"+
                "US\tUnited States\tAmerica/North_Dakota/New_Salem\tGMT -05:00" +"\t"+
                "US\tUnited States\tAmerica/Phoenix\tGMT -07:00" +"\t"+
                "US\tUnited States\tAmerica/Sitka\tGMT -08:00" +"\t"+
                "US\tUnited States\tAmerica/Yakutat\tGMT -08:00" +"\t"+
                "US\tUnited States\tPacific/Honolulu\tGMT -10:00" +"\t"+
                "UM\tUnited States Minor Outlying Islands\tPacific/Midway\tGMT -11:00" +"\t"+
                "UM\tUnited States Minor Outlying Islands\tPacific/Wake\tGMT +12:00" +"\t"+
                "UY\tUruguay\tAmerica/Montevideo\tGMT -03:00" +"\t"+
                "UZ\tUzbekistan\tAsia/Samarkand\tGMT +05:00" +"\t"+
                "UZ\tUzbekistan\tAsia/Tashkent\tGMT +05:00" +"\t"+
                "VU\tVanuatu\tPacific/Efate\tGMT +11:00" +"\t"+
                "VA\tVatican\tEurope/Vatican\tGMT +02:00" +"\t"+
                "VE\tVenezuela\tAmerica/Caracas\tGMT -04:00" +"\t"+
                "VN\tVietnam\tAsia/Ho_Chi_Minh\tGMT +07:00" +"\t"+
                "WF\tWallis and Futuna\tPacific/Wallis\tGMT +12:00" +"\t"+
                "EH\tWestern Sahara\tAfrica/El_Aaiun\tGMT +01:00" +"\t"+
                "YE\tYemen\tAsia/Aden\tGMT +03:00" +"\t"+
                "ZM\tZambia\tAfrica/Lusaka\tGMT +02:00" +"\t"+
                "ZW\tZimbabwe\tAfrica/Harare\tGMT +02:00";


        String ar[] = data.split("\t");

        for (int i = 0; i < ar.length; i=i+4) {
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Country_Code", ar[i]);
                hashMap.put("Country_Name", ar[i + 1]);
                hashMap.put("Time_Zone", ar[i + 2]);
                hashMap.put("GMT_Offset", ar[i + 3]);
                list.add(hashMap);
            } catch (Exception e) {
                e.getCause();
            }
        }
        JSONArray jsonObject = ConvertToJsonString(list);


    }


    public JSONArray ConvertToJsonString(ArrayList<HashMap<String, String>> DataList) {
        String jsonStr = "";
        JSONArray jsonArray = new JSONArray();
        JSONObject FinalJSonObj = new JSONObject();


        for (int i = 0; i < DataList.size(); i++) {
            JSONObject contact = new JSONObject();
            try {
                contact.put("Country_Code", DataList.get(i).get("Country_Code"));
                contact.put("Country_Name", DataList.get(i).get("Country_Name"));
                contact.put("Time_Zone", DataList.get(i).get("Time_Zone"));
                contact.put("GMT_Offset", DataList.get(i).get("GMT_Offset"));

                jsonArray.put(contact);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        try {

            FinalJSonObj = new JSONObject();

            // FinalJSonObj.put("userid", "171917");

            FinalJSonObj.put("TimeZone", jsonArray);


            System.out.println("jsonString: " +"\t"+ FinalJSonObj.toString());


        } catch (Exception e) {
            e.getMessage();
        }

        return jsonArray;
    }


}
