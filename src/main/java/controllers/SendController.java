package controllers;

import models.Send;

/**
 * Created by Samuel on 10/09/2016.
 */
public class SendController extends BaseController {
    String adminId = "153878723";
    Send sendModel = new Send();

    protected void test(String text){
        sendModel.sendMessage(text,adminId);

    }
}
