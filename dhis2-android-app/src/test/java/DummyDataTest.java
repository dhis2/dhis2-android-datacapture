/**
 * Created by george on 9/2/16.
 */
public class DummyDataTest {

    public static final String FIRST_NAME = "admin";
    public static final  String FIRST_DATA_ELEMENT_IN_USER_ACCOUNT_FIELDS = "firstName";

    public static String SUCCESSFUL_IMPORT_RESPONSE = "{\"responseType\":\"ImportSummary\",\"status\":\"SUCCESS\",\"description\":\"Import process completed successfully\",\"importCount\":{\"imported\":0,\"updated\":2,\"ignored\":0,\"deleted\":0},\"dataSetComplete\":\"false\"}";
    public static String ERROR_IMPORT_RESPONSE = "{\"responseType\":\"ImportSummary\",\"status\":\"ERROR\",\"description\":\"Import process was aborted\",\"importCount\":{\"imported\":0,\"updated\":0,\"ignored\":0,\"deleted\":0},\"conflicts\":[{\"object\":\"vjAYdNmic8v\",\"value\":\"Org unit not found or not accessible\"}]},";

    public static String PROFILE_DATA ="{\"id\":\"mQCdPdUipPj\",\"username\":\"admin\",\"firstName\":\"admin\",\"surname\":\"admin\",\"email\":\"admin@sl.ehealthafrica.org\",\"phoneNumber\":\"+2327600000\",\"gender\":\"gender_male\",\"settings\":{\"keyDbLocale\":null,\"keyMessageSmsNotification\":\"true\",\"keyUiLocale\":\"en\",\"keyAnalysisDisplayProperty\":\"name\",\"keyMessageEmailNotification\":\"true\"}}";

    //Data that has gone through the User account handler.
    public static String PROCESSED_PROFILE_DATA = "{\"firstName\":\"admin\",\"surname\":\"admin\",\"email\":\"admin@sl.ehealthafrica.org\",\"phoneNumber\":\"+2327600000\",\"introduction\":\"\",\"jobTitle\":\"\",\"gender\":\"gender_male\",\"birthday\":\"\",\"nationality\":\"\",\"employer\":\"\",\"education\":\"\",\"interests\":\"\",\"languages\":\"\"}";

    public static final String GOOD_GET_METHOD_FORM_RESPONSE = "{\"iQgaTATK59f\":{\"id\":\"iQgaTATK59f\",\"label\":\"Bonthe\",\"level\":2,\"parent\":\"Plmg8ikyfrK\",\"dataSets\":[{\"id\":\"C9bv64TkafG\",\"label\":\"Sabeen Test Form\"},{\"id\":\"PKV9PQ1YXc0\",\"label\":\"Jasper Test Form\"},{\"id\":\"UvsIrD50wUy\",\"label\":\"weekly disease reporting form\"},{\"id\":\"w9c9UJ3Prus\",\"label\":\"IDSR Weekly Disease Report(WDR)_old\"},{\"id\":\"PILVKnAwy4Q\",\"label\":\"eIDSR Report\"},{\"id\":\"hONZE29XBxq\",\"label\":\"Lutz Test Form\"},{\"id\":\"EB5hz0gpUnf\",\"label\":\"Paris Test Form\"},{\"id\":\"h6PK4knx7uW\",\"label\":\"Week report form Test\"},{\"id\":\"rq0LNr72Ndo\",\"label\":\"IDSR Weekly Disease Report(WDR)\"}]}}";

    public static final String GOOD_GET_METHOD_FORM_RESPONSE_ARRAY = "[{\"iQgaTATK59f\":{\"id\":\"iQgaTATK59f\",\"label\":\"Bonthe\",\"level\":2,\"parent\":\"Plmg8ikyfrK\",\"dataSets\":[{\"id\":\"C9bv64TkafG\",\"label\":\"Sabeen Test Form\"},{\"id\":\"PKV9PQ1YXc0\",\"label\":\"Jasper Test Form\"},{\"id\":\"UvsIrD50wUy\",\"label\":\"weekly disease reporting form\"},{\"id\":\"w9c9UJ3Prus\",\"label\":\"IDSR Weekly Disease Report(WDR)_old\"},{\"id\":\"PILVKnAwy4Q\",\"label\":\"eIDSR Report\"},{\"id\":\"hONZE29XBxq\",\"label\":\"Lutz Test Form\"},{\"id\":\"EB5hz0gpUnf\",\"label\":\"Paris Test Form\"},{\"id\":\"h6PK4knx7uW\",\"label\":\"Week report form Test\"},{\"id\":\"rq0LNr72Ndo\",\"label\":\"IDSR Weekly Disease Report(WDR)\"}]}}]";

    public static final String JSON_EXCEPTION_STRING = "The incoming Json is bad/malicious";

    public static final String ID = "iQgaTATK59f";

    public static final String ORG_UNITS = "[{\"id\":\"dar4XkzRmN0\",\"displayName\":\"ABERDEEN WOMENS CENTER Hospital \"},{\"id\":\"Dq86w8tvw1h\",\"displayName\":\"Ad-Bangs Quarry MCHP\"},{\"id\":\"OWlc4mW2hSs\",\"displayName\":\"Adonkia CHP\"},{\"id\":\"jRubpdIY41X\",\"displayName\":\"Adra Hospital-Waterloo\"},{\"id\":\"bYHihlIgrwM\",\"displayName\":\"Afro Arab Clinic\"},{\"id\":\"r7HlpPKHfWW\",\"displayName\":\"Agape CHP\"},{\"id\":\"NXPBaEbfw1g\",\"displayName\":\"Ahamadyya Mission Cl\"},{\"id\":\"rfK14JAVwtU\",\"displayName\":\"Ahmadiyya Muslim Hospital\"},{\"id\":\"DE3Qyg8PUTv\",\"displayName\":\"Air Port Centre, Lungi\"},{\"id\":\"gZpSPgbuBVd\",\"displayName\":\"Alkalia CHP\"},{\"id\":\"jgKElj4o4a6\",\"displayName\":\"Al-Khatab Clinic CHP\"},{\"id\":\"FFDoJvNboOc\",\"displayName\":\"Allen Town Health Post CHP \"},{\"id\":\"GfkWK0go9uI\",\"displayName\":\"Approved School CHP\"},{\"id\":\"phtyLB0ZfIz\",\"displayName\":\"AWAKE CHP\"},{\"id\":\"k5qLtwbMxwV\",\"displayName\":\"Baama CHC\"},{\"id\":\"DDEZcdYY7Zv\",\"displayName\":\"Babara CHC\"},{\"id\":\"OYB4r8Ess1c\",\"displayName\":\"Badala MCHP\"},{\"id\":\"t3OhkRzFmGl\",\"displayName\":\"Badjia\"},{\"id\":\"V5pqLY1TQC3\",\"displayName\":\"Bafodia CHC\"},{\"id\":\"R7c4EO4zFN8\",\"displayName\":\"Bagruwa\"},{\"id\":\"F7rKI2wvyCE\",\"displayName\":\"Baiama CHP\"},{\"id\":\"mjr7fbUFP93\",\"displayName\":\"Bai Bureh Memorial Hospital\"},{\"id\":\"q8y4sU9Mia6\",\"displayName\":\"Baiima CHP\"},{\"id\":\"v7grvTXSGvs\",\"displayName\":\"Bai Largo MCHP\"},{\"id\":\"NQpLWIjmmWY\",\"displayName\":\"Bailor CHP\"}]";
}
