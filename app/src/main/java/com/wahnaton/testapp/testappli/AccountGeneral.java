package com.wahnaton.testapp.testappli;


public class AccountGeneral {

    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "com.wahnaton.testapp";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "TestAppli";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to a TestAppli account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to a TestAppli account";

   public static final ServerAuthenticate sServerAuthenticate = new TestAppliServerAuthenticate();

}
